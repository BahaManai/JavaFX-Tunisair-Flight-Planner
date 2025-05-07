package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.List;


public class ListeVolController {

    @FXML
    private TableView<vol> volTable;
    @FXML
    private TableColumn<vol, String> colNumVol;
    @FXML
    private TableColumn<vol, String> colDestination;
    @FXML
    private TableColumn<vol, java.util.Date> colDepart;
    @FXML
    private TableColumn<vol, java.util.Date> colArrivee;
    @FXML
    private TableColumn<vol, TypeTrajet> colTypeTrajet;
    @FXML
    private TableColumn<vol, StatutVol> colStatut;
    @FXML
    private TableColumn<vol, Void> colActions;
    @FXML
    private Button btnAjouterVol;
    @FXML
    private Button btnVoirArchive;
    @FXML
    private TableColumn<vol, Void> colModifier;
    @FXML
    private TableColumn<vol, Void> colArchiver;

    private volDao dao;
    private Connection con;

    public ListeVolController() {
        con = LaConnexion.seConnecter();
    }

    @FXML
    public void initialize() {
        try {
            dao = new volDao(con);

            colNumVol.setCellValueFactory(new PropertyValueFactory<>("numVol"));
            colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
            colDepart.setCellValueFactory(new PropertyValueFactory<>("heureDepart"));
            colArrivee.setCellValueFactory(new PropertyValueFactory<>("heureArrivee"));
            colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
            colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
            configurerColonneModifier();
            configurerColonneArchiver();
            afficherVols();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurerColonneArchiver() {
        colArchiver.setCellFactory(param -> new TableCell<vol, Void>() {
            private final Button btn = new Button("Archiver");

            {
                btn.setOnAction(event -> {
                    vol v = getTableRow() != null ? getTableRow().getItem() : null;
                    if (v != null) {
                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirmation");
                        confirmation.setHeaderText("Archivage du vol");
                        confirmation.setContentText("Voulez-vous vraiment archiver ce vol ?");
                        ButtonType oui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                        ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.NO);
                        confirmation.getButtonTypes().setAll(oui, non);

                        confirmation.showAndWait().ifPresent(response -> {
                            if (response == oui) {
                                try {
                                    dao.archiver(v.getIdVol());
                                    afficherVols();
                                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                    successAlert.setTitle("Succès");
                                    successAlert.setHeaderText("Vol archivé");
                                    successAlert.setContentText("Le vol a été archivé avec succès.");
                                    successAlert.showAndWait();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                    errorAlert.setTitle("Erreur");
                                    errorAlert.setHeaderText("Échec de l'archivage");
                                    errorAlert.setContentText("Une erreur est survenue lors de l'archivage du vol.");
                                    errorAlert.showAndWait();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }    private void configurerColonneModifier() {
        colModifier.setCellFactory(param -> new TableCell<vol, Void>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    vol v = getTableRow() != null ? getTableRow().getItem() : null;
                    if (v != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/ModifierVol.fxml"));
                            Stage stage = new Stage();
                            stage.setScene(new Scene(loader.load()));
                            stage.setTitle("Modifier Vol");

                            ModifierVolController controller = loader.getController();
                            controller.initData(v);

                            stage.showAndWait();
                            afficherVols();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Succès");
                            alert.setHeaderText(null);
                            alert.setContentText("Le vol a été modifié avec succès.");
                            alert.showAndWait();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void afficherVols() {
        try {
            List<vol> liste = dao.getAllVols();
            ObservableList<vol> vols = FXCollections.observableArrayList(liste);
            volTable.setItems(vols);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirAjoutVol(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/ajouter-vol.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ajouter un Vol");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirArchiveVol(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/ArchivVolView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Vols Archivés");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Fichier FXML introuvable");
            alert.setContentText("Le fichier ArchivVolView.fxml n'a pas été trouvé dans:\n"
                    + "src/main/resources/com/beginsecure/tunisairaeroplan/view/");
            alert.showAndWait();
        }
    }
}