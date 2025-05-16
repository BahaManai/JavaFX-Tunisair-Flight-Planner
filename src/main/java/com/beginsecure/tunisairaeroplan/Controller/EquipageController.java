package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.dao.EquipageDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EquipageController {

    @FXML private TableView<Equipage> tableEquipages;
    @FXML private TableColumn<Equipage, String> colId;
    @FXML private TableColumn<Equipage, String> colNom;
    @FXML private TableColumn<Equipage, String> colVol;
    @FXML private TableColumn<Equipage, String> colHeure;

    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, String> colNomMembre;
    @FXML private TableColumn<Membre, String> colPrenomMembre;
    @FXML private TableColumn<Membre, String> colRoleMembre;

    @FXML private Label lblMessage;

    private EquipageDao equipageDao;

    @FXML
    public void initialize() throws SQLException {
        Connection con = com.beginsecure.tunisairaeroplan.utilites.LaConnexion.seConnecter();
        equipageDao = new EquipageDao(con);

        setupEquipageTable();
        setupMembreTable();

        chargerEquipages();

        tableEquipages.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherMembresEquipage(newVal.getId());
            }
        });
    }

    private void setupEquipageTable() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomEquipage()));
        colVol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroVol()));
        colHeure.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getHeureDepart().toString() + " → " + cellData.getValue().getHeureArrivee().toString()
                )
        );
    }

    private void setupMembreTable() {
        colNomMembre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenomMembre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colRoleMembre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().name()));
    }

    private void chargerEquipages() {
        try {
            List<Equipage> liste = equipageDao.getAllEquipagesAvecVol();
            ObservableList<Equipage> data = FXCollections.observableArrayList(liste);
            tableEquipages.setItems(data);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement des équipages", "Impossible de charger la liste des équipages : " + e.getMessage());
            tableEquipages.setItems(FXCollections.observableArrayList()); // Clear table on error
        }
    }

    private void afficherMembresEquipage(int equipageId) {
        try {
            List<Membre> membres = equipageDao.getMembresByEquipageId(equipageId);
            tableMembres.setItems(FXCollections.observableArrayList(membres));
            lblMessage.setText("Membres de l’équipage sélectionné.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Affichage des membres", "Impossible d'afficher les membres de l’équipage : " + e.getMessage());
            tableMembres.setItems(FXCollections.observableArrayList()); // Clear table on error
            lblMessage.setText("Erreur lors du chargement des membres.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}