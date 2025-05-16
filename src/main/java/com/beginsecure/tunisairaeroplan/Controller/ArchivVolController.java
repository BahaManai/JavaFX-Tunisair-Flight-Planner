package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.dao.ArchivVolDao;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ArchivVolController {
    @FXML
    private TableView<vol> tableVolsArchives;
    @FXML
    private TableColumn<vol, String> colNumVol;
    @FXML
    private TableColumn<vol, String> colDestination;
    @FXML
    private TableColumn<vol, java.util.Date> colDepart;
    @FXML
    private TableColumn<vol, java.util.Date> colArrivee;
    @FXML
    private TableColumn<vol, String> colTypeTrajet;
    @FXML
    private TableColumn<vol, String> colStatut;
    @FXML
    private TableColumn<vol, Void> colRestaurer;
    @FXML
    private TableColumn<vol, Void> colSupprimer;
    @FXML private TableColumn<vol, Integer> colAvion;
    @FXML private TableColumn<vol, Integer> colEquipage;


    private volDao dao;

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        try {
            dao = new volDao(); // sans connexion
            colNumVol.setCellValueFactory(new PropertyValueFactory<>("numVol"));
            colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
            colDepart.setCellValueFactory(new PropertyValueFactory<>("heureDepart"));
            colArrivee.setCellValueFactory(new PropertyValueFactory<>("heureArrivee"));
            colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
            colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
            colAvion.setCellValueFactory(new PropertyValueFactory<>("avionId"));
            colEquipage.setCellValueFactory(new PropertyValueFactory<>("equipageId"));


            configurerColonneRestaurer();
            configurerColonneSupprimer();
            afficherVolsArchives();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur d'initialisation", e.getMessage());
        }
    }

    private void afficherVolsArchives() {
        try {
            List<vol> liste = dao.getAllVolsArchives();
            ObservableList<vol> vols = FXCollections.observableArrayList(liste);
            tableVolsArchives.setItems(vols);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur de chargement", "Impossible de charger les vols archivés");
        }
    }

    private void configurerColonneRestaurer() {
        colRestaurer.setCellFactory(param -> new TableCell<vol, Void>() {
            private final Button btnRestaurer = new Button("Restaurer");

            {
                btnRestaurer.setOnAction(event -> {
                    vol v = getTableView().getItems().get(getIndex());
                    if (v != null) {
                        restaurerVol(v);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRestaurer);
                }
            }
        });
    }

    private void configurerColonneSupprimer() {
        colSupprimer.setCellFactory(param -> new TableCell<vol, Void>() {
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnSupprimer.setOnAction(event -> {
                    vol v = getTableView().getItems().get(getIndex());
                    if (v != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Confirmer la suppression définitive");
                        alert.setContentText("Voulez-vous vraiment supprimer définitivement ce vol ?");
                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                supprimerVol(v);
                            }
                        });
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(btnSupprimer);
                }
            }
        });
    }

    private void restaurerVol(vol v) {
        try {
            ArchivVolDao archivDao = new ArchivVolDao(LaConnexion.seConnecter());
            archivDao.restaurerVol(v.getIdVol());
            afficherVolsArchives();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Vol restauré");
            alert.setContentText("Le vol a été restauré avec succès.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Table 'tunisair_flight_planner.archive' doesn't exist")) {
                showAlert("Erreur", "Table inexistante", "La table 'archive' n'existe pas dans la base de données.");
            } else {
                showAlert("Erreur", "Échec de la restauration", "Une erreur est survenue lors de la restauration du vol.");
            }
        }
    }
    private void supprimerVol(vol v) {
        try {
            ArchivVolDao archivDao = new ArchivVolDao(LaConnexion.seConnecter());
            archivDao.supprimerArchive(v.getIdVol());
            afficherVolsArchives();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Vol supprimé");
            alert.setContentText("Le vol a été supprimé définitivement.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Table 'tunisair_flight_planner.archive' doesn't exist")) {
                showAlert("Erreur", "Table inexistante", "La table 'archive' n'existe pas dans la base de données.");
            } else {
                showAlert("Erreur", "Échec de la suppression", "Une erreur est survenue lors de la suppression du vol.");
            }
        }
    }
}