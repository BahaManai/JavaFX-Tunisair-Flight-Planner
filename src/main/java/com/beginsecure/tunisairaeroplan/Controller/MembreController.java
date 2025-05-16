package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.Model.enums.RoleMembre;
import com.beginsecure.tunisairaeroplan.dao.membreDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.Connection;
import java.sql.SQLException;

public class MembreController {

    @FXML private TextField txtCin, txtNom, txtPrenom;
    @FXML private ComboBox<RoleMembre> cbRole;
    @FXML private CheckBox chkDisponible;
    @FXML private TableView<Membre> tvMembre;
    @FXML private TableColumn<Membre, String> colCin, colNom, colPrenom, colRole;
    @FXML private TableColumn<Membre, String> colDisponible;

    private ObservableList<Membre> observableList;
    private membreDao membreDao;

    @FXML
    public void initialize() {
        try {
            Connection connection = LaConnexion.seConnecter();
            membreDao = new membreDao(connection);
            observableList = FXCollections.observableArrayList(membreDao.lister());

            colCin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCin()));
            colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
            colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole().toString()));
            colPrenom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));
            colDisponible.setCellValueFactory(data -> {
                boolean disponible = data.getValue().isDisponible();
                String affichage = disponible ? "Oui" : "Non";
                return new SimpleStringProperty(affichage);
            });
            cbRole.getItems().setAll(RoleMembre.values());
            tvMembre.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Impossible de se connecter à la base de données : " + e.getMessage());
        }
    }

    @FXML
    private void ajouterMembre() {
        if (!validerChamps()) return;

        Membre m = new Membre(0, txtCin.getText(), txtNom.getText(), txtPrenom.getText(), cbRole.getValue(), chkDisponible.isSelected());
        try {
            membreDao.ajouter(m);
            rafraichirListe();
            clearChamps();
            showNotification("Succès", "Membre ajouté avec succès.");
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique") || e.getMessage().toLowerCase().contains("duplicate")) {
                showAlert(Alert.AlertType.WARNING, "Conflit de données", "Le CIN saisi existe déjà. Veuillez en saisir un autre.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur d'ajout", "Impossible d'ajouter le membre : " + e.getMessage());
            }
        }
    }

    @FXML
    private void modifierMembre() {
        Membre selectedMembre = tvMembre.getSelectionModel().getSelectedItem();
        if (selectedMembre == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun membre sélectionné", "Veuillez sélectionner un membre à modifier.");
            return;
        }
        if (!validerChamps()) return;

        Membre m = new Membre(selectedMembre.getId(), txtCin.getText(), txtNom.getText(), txtPrenom.getText(), cbRole.getValue(), chkDisponible.isSelected());
        try {
            membreDao.modifier(m);
            rafraichirListe();
            clearChamps();
            showNotification("Modification", "Membre modifié avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de modification", "Impossible de modifier le membre : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerMembre() {
        Membre m = tvMembre.getSelectionModel().getSelectedItem();
        if (m != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer ce membre ?");
            confirmation.setContentText(m.getNom() + " " + m.getPrenom());

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        membreDao.supprimer(m);
                        rafraichirListe();
                        clearChamps();
                        showNotification("Suppression", "Membre supprimé avec succès.");
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur de suppression", "Impossible de supprimer le membre : " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun membre sélectionné", "Veuillez sélectionner un membre à supprimer.");
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
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun membre sélectionné", "Veuillez sélectionner un membre à consulter.");
        }
    }

    private boolean validerChamps() {
        if (txtCin.getText().isEmpty() || txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() || cbRole.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }
        return true;
    }

    private void clearChamps() {
        txtCin.clear();
        txtNom.clear();
        txtPrenom.clear();
        cbRole.setValue(null);
        chkDisponible.setSelected(false);
    }

    private void rafraichirListe() {
        try {
            observableList.setAll(membreDao.lister());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de rafraîchissement", "Impossible de rafraîchir la liste : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Gestion des Membres");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showNotification(String titre, String message) {
        Notifications.create()
                .title(titre)
                .text(message)
                .hideAfter(Duration.seconds(3))
                .position(javafx.geometry.Pos.BOTTOM_RIGHT)
                .showInformation();
    }
}
