package com.beginsecure.tunisairaeroplan.Controller;
import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class AjouterVolController {

    @FXML
    private TextField numVolField;

    @FXML
    private TextField destinationField;

    @FXML
    private DatePicker heureDepartField;

    @FXML
    private TextField heureDepartTimeField;

    @FXML
    private DatePicker heureArriveeField;

    @FXML
    private TextField heureArriveeTimeField;

    @FXML
    private ComboBox<TypeTrajet> typeTrajetCombo;

    @FXML
    private ComboBox<StatutVol> statutCombo;

    @FXML
    private ComboBox<Avion> avionCombo;

    @FXML
    private ComboBox<Equipage> equipageCombo;

    @FXML
    public void initialize() {
        // Remplir les ComboBox avec les enums
        typeTrajetCombo.setItems(FXCollections.observableArrayList(TypeTrajet.values()));
        statutCombo.setItems(FXCollections.observableArrayList(StatutVol.values()));

        // TODO : remplir avionCombo et equipageCombo depuis la base de données
        avionCombo.setItems(FXCollections.observableArrayList()); // vide temporairement
        equipageCombo.setItems(FXCollections.observableArrayList());
    }

    @FXML
    public void ajouterVol() {
        try {
            String numVol = numVolField.getText();
            String destination = destinationField.getText();
            LocalDate dateDepart = heureDepartField.getValue();
            LocalDate dateArrivee = heureArriveeField.getValue();
            LocalTime timeDepart = LocalTime.parse(heureDepartTimeField.getText());
            LocalTime timeArrivee = LocalTime.parse(heureArriveeTimeField.getText());

            Date heureDepart = Date.valueOf(dateDepart);
            Date heureArrivee = Date.valueOf(dateArrivee);
            Time heureDepartTime = Time.valueOf(timeDepart);
            Time heureArriveeTime = Time.valueOf(timeArrivee);

            // Combine dates + times (java.sql.Timestamp si nécessaire)
            java.util.Date fullDepart = new java.util.Date(heureDepart.getTime() + heureDepartTime.getTime());
            java.util.Date fullArrivee = new java.util.Date(heureArrivee.getTime() + heureArriveeTime.getTime());

            TypeTrajet typeTrajet = typeTrajetCombo.getValue();
            StatutVol statut = statutCombo.getValue();

            Avion avion = avionCombo.getValue(); // à remplacer avec vrai objet
            Equipage equipage = equipageCombo.getValue();

            if (numVol.isEmpty() || destination.isEmpty() || fullDepart == null || fullArrivee == null || typeTrajet == null || statut == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            vol vol = new vol(numVol, destination, fullDepart, fullArrivee, typeTrajet, statut, avion, equipage);

            Connection connection = LaConnexion.seConnecter();
            volDao dao = new volDao(connection);
            dao.insertVol(vol);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté", "Le vol a été ajouté avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Exception", "Une erreur est survenue : " + e.getMessage());
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