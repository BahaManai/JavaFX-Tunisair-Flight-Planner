package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.dao.equipageDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class EquipageController {

    @FXML
    private TextField txtNomEquipage;
    @FXML
    private TableView<Equipage> tableEquipages;
    @FXML
    private TableColumn<Equipage, Integer> colId;
    @FXML
    private TableColumn<Equipage, String> colNom;
    @FXML
    private TableColumn<Equipage, Integer> colNbMembres;
    @FXML
    private TableView<Membre> tableMembres;
    @FXML
    private TableColumn<Membre, String> colNomMembre;
    @FXML
    private TableColumn<Membre, String> colPrenomMembre;
    @FXML
    private TableColumn<Membre, String> colRoleMembre;

    private ObservableList<Equipage> equipageList;
    @FXML
    private Equipage equipageSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNomEquipage()));
        colNbMembres.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getMembres().size()).asObject());

        colNomMembre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colPrenomMembre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        colRoleMembre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));

        listerEquipages();
    }

    public void listerEquipages() {
        equipageList = FXCollections.observableArrayList(equipageDao.lister());
        tableEquipages.setItems(equipageList);
    }

    public void ajouterEquipage() {
        String nom = txtNomEquipage.getText().trim();
        if (!nom.isEmpty()) {
            Equipage e = new Equipage(0, nom);
            if (equipageDao.ajouter(e)) {
                listerEquipages();
                txtNomEquipage.clear();
            }
        }
    }

    public void modifierEquipage() {
        Equipage selected = tableEquipages.getSelectionModel().getSelectedItem();
        String nouveauNom = txtNomEquipage.getText().trim();
        if (selected != null && !nouveauNom.isEmpty()) {
            selected = new Equipage(selected.getId(), nouveauNom);
            if (equipageDao.modifier(selected)) {
                listerEquipages();
                txtNomEquipage.clear();
            }
        }
    }

    public void supprimerEquipage() {
        Equipage selected = tableEquipages.getSelectionModel().getSelectedItem();
        if (selected != null) {
            equipageDao.supprimer(selected);
            listerEquipages();
        }
    }
    @FXML
    private void selectionnerEquipage(MouseEvent event) {
        equipageSelectionne = tableEquipages.getSelectionModel().getSelectedItem();
        if (equipageSelectionne != null) {
            tableMembres.getItems().setAll(equipageDao.recupererMembres(equipageSelectionne.getId()));
        } else {
            tableMembres.getItems().clear(); // bonne pratique
        }
    }

}
