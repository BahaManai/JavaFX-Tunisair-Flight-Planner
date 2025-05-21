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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListeVolController {
    @FXML private TableView<vol> volTable;
    @FXML private TableColumn<vol, String> colNumVol;
    @FXML private TableColumn<vol, String> colOrigine;
    @FXML private TableColumn<vol, String> colDestination;
    @FXML private TableColumn<vol, String> colDepart;
    @FXML private TableColumn<vol, String> colArrivee;
    @FXML private TableColumn<vol, TypeTrajet> colTypeTrajet;
    @FXML private TableColumn<vol, StatutVol> colStatut;
    @FXML private TableColumn<vol, String> colAvion;
    @FXML private TableColumn<vol, String> colEquipage;
    @FXML private TableColumn<vol, Void> colActions;
    @FXML private TextField searchField;
    @FXML private Label searchLabel;
    @FXML private ComboBox<StatutVol> statutFilterComboBox;
    @FXML private ComboBox<TypeTrajet> typeTrajetFilterComboBox;
    private volDao dao;
    private ObservableList<vol> volList = FXCollections.observableArrayList();
    private FilteredList<vol> filteredVolList;

    @FXML
    public void initialize() {
        dao = new volDao();
        try {
            dao.updatePastFlightsStatus();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update flight statuses: " + e.getMessage());
        }
        try {
            dao.updatePastFlightsStatus();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update flight statuses: " + e.getMessage());
        }
        statutFilterComboBox.setItems(FXCollections.observableArrayList(StatutVol.values()));
        statutFilterComboBox.getSelectionModel().selectFirst();

        typeTrajetFilterComboBox.setItems(FXCollections.observableArrayList(TypeTrajet.values()) );
        typeTrajetFilterComboBox.getSelectionModel().selectFirst();
        setupFilters();
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(javafx.scene.paint.Color.web("#333333"));
        searchLabel.setGraphic(searchIcon);

        setupTableColumns();
        loadVols();
        setupSearchFilter();
        addActionButtons();
    }
    @FXML
    private void resetFilters() {
        statutFilterComboBox.getSelectionModel().selectFirst();
        typeTrajetFilterComboBox.getSelectionModel().selectFirst();
        searchField.clear();
    }
    private void setupFilters() {
        statutFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
        typeTrajetFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        filteredVolList.setPredicate(vol -> {
            boolean matchesSearch = true;
            boolean matchesStatut = true;
            boolean matchesType = true;

            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                matchesSearch = vol.getNumVol().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getOrigine().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getDestination().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getStatut().toString().toLowerCase().contains(lowerCaseFilter);
            }

            StatutVol selectedStatut = statutFilterComboBox.getValue();
            if (selectedStatut != null && selectedStatut != StatutVol.values()[0]) {
                matchesStatut = vol.getStatut() == selectedStatut;
            } else {
                matchesStatut = true;
            }

            TypeTrajet selectedType = typeTrajetFilterComboBox.getValue();
            if (selectedType != null && selectedType != TypeTrajet.values()[0]) {
                matchesType = vol.getTypeTrajet() == selectedType;
            } else {
                matchesType = true;
            }

            return matchesSearch && matchesStatut && matchesType;
        });
    }


    private void setupSearchFilter() {
        filteredVolList = new FilteredList<>(volList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredVolList.setPredicate(vol -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return vol.getNumVol().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getOrigine().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getDestination().toLowerCase().contains(lowerCaseFilter) ||
                        vol.getStatut().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });
        volTable.setItems(filteredVolList);
    }

    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button modifyBtn = new Button();
            private final Button archiveBtn = new Button();
            private final Button cancelBtn = new Button();
            private final HBox hbox = new HBox(modifyBtn, archiveBtn, cancelBtn);

            {
                FontIcon modifyIcon = new FontIcon(FontAwesomeSolid.EDIT);
                modifyIcon.setIconSize(16);
                modifyIcon.setIconColor(javafx.scene.paint.Color.web("#2196F3"));
                modifyBtn.setGraphic(modifyIcon);
                modifyBtn.setStyle("-fx-background-color: transparent;");
                modifyBtn.setTooltip(new Tooltip("Modifier"));
                FontIcon archiveIcon = new FontIcon(FontAwesomeSolid.ARCHIVE);
                archiveIcon.setIconSize(16);
                archiveIcon.setIconColor(javafx.scene.paint.Color.web("#607D8B"));
                archiveBtn.setGraphic(archiveIcon);
                archiveBtn.setStyle("-fx-background-color: transparent;");
                archiveBtn.setTooltip(new Tooltip("Archiver"));
                FontIcon cancelIcon = new FontIcon(FontAwesomeSolid.TIMES);
                cancelIcon.setIconSize(16);
                cancelIcon.setIconColor(javafx.scene.paint.Color.web("#e74c3c"));
                cancelBtn.setGraphic(cancelIcon);
                cancelBtn.setStyle("-fx-background-color: transparent;");
                cancelBtn.setTooltip(new Tooltip("Annuler"));
                hbox.setSpacing(5);
                hbox.setAlignment(javafx.geometry.Pos.CENTER);
                modifyBtn.setOnAction(event -> ouvrirModifierVol(getTableView().getItems().get(getIndex())));
                archiveBtn.setOnAction(event -> archiverVol(getTableView().getItems().get(getIndex())));
                cancelBtn.setOnAction(event -> annulerVol(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    vol v = getTableView().getItems().get(getIndex());
                    cancelBtn.setVisible(v.getStatut() != StatutVol.Annulé);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void annulerVol(vol v) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annulation du vol");
        confirmation.setContentText("Voulez-vous vraiment annuler ce vol ?");
        confirmation.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES), new ButtonType("Non", ButtonBar.ButtonData.NO));
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
        colOrigine.setCellValueFactory(new PropertyValueFactory<>("origine"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDepart.setCellValueFactory(data -> {
            LocalDateTime dateTime = data.getValue().getHeureDepart().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            return new SimpleStringProperty(dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });
        colArrivee.setCellValueFactory(data -> {
            LocalDateTime dateTime = data.getValue().getHeureArrivee().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            return new SimpleStringProperty(dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });
        colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colAvion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAvion() != null ? data.getValue().getAvion().getMarque() + " " + data.getValue().getAvion().getModele() : "N/A")
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
                LocalDateTime depart = v.getHeureDepart().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                if (v.getStatut() != StatutVol.Annulé && depart.isBefore(now)) {
                    v.setStatut(StatutVol.Terminé);
                    dao.updateVol(v);
                }
                volList.add(v);
            }
            volTable.setItems(volList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load flights: " + e.getMessage());
        }
    }

    private void archiverVol(vol v) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Archivage du vol");
        confirmation.setContentText("Voulez-vous vraiment archiver ce vol ?");
        confirmation.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES), new ButtonType("Non", ButtonBar.ButtonData.NO));
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