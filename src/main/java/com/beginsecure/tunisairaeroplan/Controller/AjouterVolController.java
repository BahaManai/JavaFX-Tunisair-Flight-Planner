package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AjouterVolController {

    @FXML private TextField numVolField;
    @FXML private TextField destinationField;
    @FXML private DatePicker heureDepartField;
    @FXML private TextField heureDepartTimeField;
    @FXML private DatePicker heureArriveeField;
    @FXML private TextField heureArriveeTimeField;
    @FXML private ComboBox<TypeTrajet> typeTrajetCombo;
    @FXML private ComboBox<StatutVol> statutCombo;
    @FXML private ComboBox<Avion> avionCombo;
    @FXML private ComboBox<Equipage> equipageCombo;

    @FXML
    public void initialize() {
        typeTrajetCombo.setItems(FXCollections.observableArrayList(TypeTrajet.values()));
        statutCombo.setItems(FXCollections.observableArrayList(StatutVol.values()));
        chargerAvionsDisponibles();
    }

    @FXML
    public void ajouterVol() {
        try {
            if (!validerChamps()) return;

            vol vol = creerVol();
            Connection connection = LaConnexion.seConnecter();
            new volDao(connection).insertVol(vol);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté", "Le vol a été ajouté avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Exception", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        if (numVolField.getText().isEmpty() ||
                destinationField.getText().isEmpty() ||
                heureDepartField.getValue() == null ||
                heureArriveeField.getValue() == null ||
                typeTrajetCombo.getValue() == null ||
                statutCombo.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
    }

    private vol creerVol() throws Exception {
        LocalDate dateDepart = heureDepartField.getValue();
        LocalDate dateArrivee = heureArriveeField.getValue();
        LocalTime timeDepart = LocalTime.parse(heureDepartTimeField.getText());
        LocalTime timeArrivee = LocalTime.parse(heureArriveeTimeField.getText());

        java.util.Date fullDepart = new java.util.Date(
                Date.valueOf(dateDepart).getTime() + Time.valueOf(timeDepart).getTime());
        java.util.Date fullArrivee = new java.util.Date(
                Date.valueOf(dateArrivee).getTime() + Time.valueOf(timeArrivee).getTime());

        return new vol(
                numVolField.getText(),
                destinationField.getText(),
                fullDepart,
                fullArrivee,
                typeTrajetCombo.getValue(),
                statutCombo.getValue(),
                avionCombo.getValue(),
                equipageCombo.getValue()
        );
    }

    private void chargerAvionsDisponibles() {
        try {
            List<Avion> avions = new DAOAvion().getAvionsDisponibles();
            Platform.runLater(() -> avionCombo.setItems(FXCollections.observableArrayList(avions)));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement avions", e.getMessage()));
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