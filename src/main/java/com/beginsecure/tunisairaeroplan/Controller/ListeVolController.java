package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ListeVolController {
    @FXML private TableColumn<vol, Void> colAnnuler;    @FXML private TableView<vol> volTable;
    @FXML private TableColumn<vol, String> colNumVol;
    @FXML private TableColumn<vol, String> colDestination;
    @FXML private TableColumn<vol, java.util.Date> colDepart;
    @FXML private TableColumn<vol, java.util.Date> colArrivee;
    @FXML private TableColumn<vol, TypeTrajet> colTypeTrajet;
    @FXML private TableColumn<vol, StatutVol> colStatut;
    @FXML private TableColumn<vol, Void> colModifier;
    @FXML private TableColumn<vol, Void> colArchiver;
    @FXML private TableColumn<vol, String> colAvion;
    @FXML private TableColumn<vol, String> colEquipage;
    @FXML private TextField searchField;
    private volDao dao;
    private ObservableList<vol> volList = FXCollections.observableArrayList();
    private FilteredList<vol> filteredVolList;
    @FXML
    public void initialize() {
        dao = new volDao();
        try {
            // Update past flights to Terminé if their departure date is before now
            dao.updatePastFlightsStatus(); // Ensure past flights are marked as Terminé
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update flight statuses: " + e.getMessage());
        }
        setupTableColumns();
        loadVols();
        setupSearchFilter();
        addModifyButtons();
        addArchiveButtons();
        addCancelButtons(); // Add the cancel button logic
    }
    private void setupSearchFilter() {
        // Initialize the FilteredList with the volList
        filteredVolList = new FilteredList<>(volList, p -> true);

        // Add listener to the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredVolList.setPredicate(vol -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true; // Show all flights if search field is empty
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return vol.getNumVol().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getDestination().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getStatut().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });

        volTable.setItems(filteredVolList);
    }
    private void addCancelButtons() {
        colAnnuler.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Annuler");

            {
                btn.setOnAction(event -> {
                    vol v = getTableView().getItems().get(getIndex());
                    annulerVol(v);
                });

                // Style du bouton
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    vol v = getTableView().getItems().get(getIndex());
                    // Show button only for Planifié or Terminé
                    btn.setVisible(v.getStatut() == StatutVol.Planifié || v.getStatut() == StatutVol.Terminé);
                    setGraphic(btn);
                }
            }
        });
    }
    private void annulerVol(vol v) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annulation du vol");
        confirmation.setContentText("Voulez-vous vraiment annuler ce vol ?");

        confirmation.getButtonTypes().setAll(
                new ButtonType("Oui", ButtonBar.ButtonData.YES),
                new ButtonType("Non", ButtonBar.ButtonData.NO)
        );

        confirmation.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.YES) {
                try {
                    v.setStatut(StatutVol.Annulé);
                    dao.updateVol(v);
                    loadVols();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le vol a été annulé avec succès.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'annulation du vol.");
                }
            }
        });
    }


    private void setupTableColumns() {
        colNumVol.setCellValueFactory(new PropertyValueFactory<>("numVol"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDepart.setCellValueFactory(new PropertyValueFactory<>("heureDepart"));
        colArrivee.setCellValueFactory(new PropertyValueFactory<>("heureArrivee"));
        colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colAvion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAvion() != null ?data.getValue().getAvion().getMarque() +" "+ data.getValue().getAvion().getModele() : "N/A")
        );
        colEquipage.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEquipage() != null ? data.getValue().getEquipage().getNomEquipage() : "N/A")
        );

    }
    private void loadVols() {
        try {
            volList.clear();
            List<vol> vols = dao.getAllVols();
            LocalDateTime now = LocalDateTime.now();
            for (vol v : vols) {
                // Convert java.util.Date to LocalDateTime
                LocalDateTime depart = v.getHeureDepart().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                if (v.getStatut() != StatutVol.Annulé && depart.isBefore(now)) {
                    v.setStatut(StatutVol.Terminé);
                    dao.updateVol(v); // Persist the status change to the database
                }
                volList.add(v);
            }
            volTable.setItems(volList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load flights: " + e.getMessage());
        }
    }

    private void addModifyButtons() {
        colModifier.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    vol v = getTableView().getItems().get(getIndex());
                    ouvrirModifierVol(v);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void addArchiveButtons() {
        colArchiver.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Archiver");

            {
                btn.setOnAction(event -> {
                    vol v = getTableView().getItems().get(getIndex());
                    archiverVol(v);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void archiverVol(vol v) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Archivage du vol");
        confirmation.setContentText("Voulez-vous vraiment archiver ce vol ?");

        confirmation.getButtonTypes().setAll(
                new ButtonType("Oui", ButtonBar.ButtonData.YES),
                new ButtonType("Non", ButtonBar.ButtonData.NO)
        );

        confirmation.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.YES) {
                try {
                    dao.archiver(v.getIdVol());
                    loadVols();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le vol a été archivé avec succès.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'archivage du vol.");
                }
            }
        });
    }

    @FXML
    private void ouvrirAjoutVol() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/ajouter-vol.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Vol");
            stage.showAndWait();
            loadVols();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirArchiveVol() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/ArchivVolView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Vols Archivés");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier ArchivVolView.fxml n'a pas été trouvé.");
        }
    }

    private void ouvrirModifierVol(vol v) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/ModifierVol.fxml"));
            Parent root = loader.load();

            ModifierVolController controller = loader.getController();
            controller.initData(v);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Vol");
            stage.showAndWait();
            loadVols();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}