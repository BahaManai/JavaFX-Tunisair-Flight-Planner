package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ListeVolController {

    @FXML private TableView<vol> volTable;
    @FXML private TableColumn<vol, String> colNumVol;
    @FXML private TableColumn<vol, String> colDestination;
    @FXML private TableColumn<vol, java.util.Date> colDepart;
    @FXML private TableColumn<vol, java.util.Date> colArrivee;
    @FXML private TableColumn<vol, TypeTrajet> colTypeTrajet;
    @FXML private TableColumn<vol, StatutVol> colStatut;
    @FXML private TableColumn<vol, Void> colModifier;
    @FXML private TableColumn<vol, Void> colArchiver;

    private volDao dao;
    private Connection con;
    private ObservableList<vol> volList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        con = LaConnexion.seConnecter();
        dao = new volDao(con);

        setupTableColumns();
        loadVols();
        addModifyButtons();
        addArchiveButtons();
    }

    private void setupTableColumns() {
        colNumVol.setCellValueFactory(new PropertyValueFactory<>("numVol"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDepart.setCellValueFactory(new PropertyValueFactory<>("heureDepart"));
        colArrivee.setCellValueFactory(new PropertyValueFactory<>("heureArrivee"));
        colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }
    private void loadVols() {
        try {
            volList.clear();
            volList.addAll(dao.getAllVols());
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