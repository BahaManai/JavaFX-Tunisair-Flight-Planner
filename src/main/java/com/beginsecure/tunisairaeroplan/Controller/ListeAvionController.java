package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
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

public class ListeAvionController {

    @FXML private TableView<Avion> avionTable;
    @FXML private TableColumn<Avion, Integer> colId;
    @FXML private TableColumn<Avion, String> colModele;
    @FXML private TableColumn<Avion, Integer> colCapacite;
    @FXML private TableColumn<Avion, Boolean> colDisponible;
    @FXML private TableColumn<Avion, String> colTypeTrajet;
    @FXML private TableColumn<Avion, Void> colModifier;
    @FXML private TableColumn<Avion, Void> colArchiver;

    private DAOAvion daoAvion = new DAOAvion();
    private ObservableList<Avion> avionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAvions();
        addModifyButtons();
        addArchiveButtons();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("estDisponible"));
        colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
    }

    private void loadAvions() {
        avionList.clear();
        avionList.addAll(daoAvion.getAllAvions());
        avionTable.setItems(avionList);
    }

    private void addModifyButtons() {
        colModifier.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    Avion avion = getTableView().getItems().get(getIndex());
                    ouvrirModifierAvion(avion);
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
                    Avion avion = getTableView().getItems().get(getIndex());
                    archiverAvion(avion);
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

    private void archiverAvion(Avion avion) {
        if (daoAvion.archiverAvion(avion)) {
            loadAvions();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Avion archivé avec succès");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'archivage de l'avion");
        }
    }

    @FXML
    private void ouvrirAjoutAvion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/AjoutAvion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un avion");
            stage.showAndWait();
            loadAvions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirArchiveAvion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/ArchivAvion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Archive des avions");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirModifierAvion(Avion avion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/ModifierAvion.fxml"));
            Parent root = loader.load();

            ModifierAvionController controller = loader.getController();
            controller.setAvionData(avion);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier l'avion");
            stage.showAndWait();
            loadAvions();
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