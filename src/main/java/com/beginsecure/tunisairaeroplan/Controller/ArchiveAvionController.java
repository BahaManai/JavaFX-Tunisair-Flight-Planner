package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.dao.ArchiveAvionDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Optional;

public class ArchiveAvionController {

    @FXML private TableView<Avion> archiveTable;
    @FXML private TableColumn<Avion, Integer> colId;
    @FXML private TableColumn<Avion, String> colMarque;
    @FXML private TableColumn<Avion, String> colModele;
    @FXML private TableColumn<Avion, Integer> colCapacite;
    @FXML private TableColumn<Avion, Boolean> colDisponible;
    @FXML private TableColumn<Avion, String> colTypeTrajet;

    private ArchiveAvionDao archiveDao = new ArchiveAvionDao();
    private ObservableList<Avion> archiveList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadArchivedAvions();
    }

    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("estDisponible"));
        colTypeTrajet.setCellValueFactory(new PropertyValueFactory<>("typeTrajet"));
    }

    private void loadArchivedAvions() {
        archiveList.setAll(archiveDao.getAllArchivedAvions());
        archiveTable.setItems(archiveList);
    }
    @FXML
    private void handleRestaurer() {
        Avion avionSelectionne = archiveTable.getSelectionModel().getSelectedItem();

        if (avionSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un avion à restaurer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de restauration");
        confirmation.setHeaderText("Restaurer l'avion " + avionSelectionne.getModele());
        confirmation.setContentText("Êtes-vous sûr de vouloir restaurer cet avion?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ArchiveAvionDao archiveDao = new ArchiveAvionDao();
            if (archiveDao.restaurerAvion(avionSelectionne)) {
                loadArchivedAvions(); // Rafraîchir la liste
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Avion restauré avec succès");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la restauration de l'avion");
            }
        }
    }

    @FXML
    private void handleSupprimer() {
        Avion avionSelectionne = archiveTable.getSelectionModel().getSelectedItem();

        if (avionSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un avion à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer définitivement l'avion " + avionSelectionne.getModele());
        confirmation.setContentText("Cette action est irréversible. Êtes-vous sûr?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ArchiveAvionDao archiveDao = new ArchiveAvionDao();
            if (archiveDao.supprimerDefinitivement(avionSelectionne.getId())) {
                loadArchivedAvions(); // Rafraîchir la liste
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Avion supprimé définitivement");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression de l'avion");
            }
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) archiveTable.getScene().getWindow();
        stage.close();
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}