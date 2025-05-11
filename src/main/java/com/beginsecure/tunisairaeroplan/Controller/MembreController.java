package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.Model.enums.RoleMembre;
import com.beginsecure.tunisairaeroplan.dao.membreDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MembreController {

    @FXML private TextField txtCin, txtNom, txtPrenom;
    @FXML private ComboBox<RoleMembre> cbRole;
    @FXML private CheckBox chkDisponible;
    @FXML private TableView<Membre> tvMembre;
    @FXML private TableColumn<Membre, String> colCin, colNom, colPrenom, colRole;
    @FXML private TableColumn<Membre, Boolean> colDisponible;

    private ObservableList<Membre> observableList;

    @FXML
    public void initialize() {
        observableList = FXCollections.observableArrayList(membreDao.lister());

        colCin.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCin()));
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));
        colPrenom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        colDisponible.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isDisponible()));
        cbRole.getItems().setAll(RoleMembre.values());
        tvMembre.setItems(observableList);
    }

    @FXML
    private void ajouterMembre() {
        Membre m = new Membre(0, txtCin.getText(), txtNom.getText(), txtPrenom.getText(), cbRole.getValue(), chkDisponible.isSelected());
        membreDao.ajouter(m);
        rafraichirListe();
        clearChamps();
    }

    @FXML
    private void modifierMembre() {
        Membre m = new Membre(0, txtCin.getText(), txtNom.getText(), txtPrenom.getText(), cbRole.getValue(), chkDisponible.isSelected());
        membreDao.modifier(m);
        rafraichirListe();
        clearChamps();
    }

    @FXML
    private void supprimerMembre() {
        Membre m = tvMembre.getSelectionModel().getSelectedItem();
        if (m != null) {
            membreDao.supprimer(m);
            rafraichirListe();
            clearChamps();
        }
    }

    @FXML
    private void consulterMembre() {
        Membre m = tvMembre.getSelectionModel().getSelectedItem();
        if (m != null) {
            txtCin.setText(m.getCin());
            txtNom.setText(m.getNom());
            txtPrenom.setText(m.getPrenom());
            cbRole.setValue(m.getRole());
            chkDisponible.setSelected(m.isDisponible());
        }
    }

    private void clearChamps() {
        txtCin.clear();
        txtNom.clear();
        txtPrenom.clear();
        chkDisponible.setSelected(false);
    }

    private void rafraichirListe() {
        observableList.setAll(membreDao.lister());
    }
}
