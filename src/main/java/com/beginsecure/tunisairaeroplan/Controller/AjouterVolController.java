package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.*;
import com.beginsecure.tunisairaeroplan.Model.enums.RoleMembre;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.dao.EquipageDao;
import com.beginsecure.tunisairaeroplan.dao.membreDao;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AjouterVolController {

    @FXML private TextField numVolField, destinationField, heureDepartTimeField, heureArriveeTimeField;
    @FXML private DatePicker heureDepartField, heureArriveeField;
    @FXML private ComboBox<TypeTrajet> typeTrajetCombo;
    @FXML private ComboBox<StatutVol> statutCombo;
    @FXML private ComboBox<Avion> avionCombo;
    @FXML private ComboBox<Membre> piloteCombo, copiloteCombo, chefCabineCombo, hotesseCombo, mecanicienCombo;

    @FXML
    public void initialize() {
        typeTrajetCombo.setItems(FXCollections.observableArrayList(TypeTrajet.values()));
        statutCombo.setItems(FXCollections.observableArrayList(StatutVol.values()));
        chargerAvionsDisponibles();
        chargerMembresParRole();
    }

    private void chargerMembresParRole() {
        piloteCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Pilote)));
        copiloteCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Copilote)));
        chefCabineCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Chef_de_cabine)));
        hotesseCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Hôtesse)));
        mecanicienCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Mécanicien)));
    }

    @FXML
    public void ajouterVol() {
        try {
            if (!validerChamps()) return;
            if (!validerMembresEquipage()) return;

            String nomEquipage = "AutoGen" + System.currentTimeMillis();
            EquipageDao equipageDao = new EquipageDao(LaConnexion.seConnecter());

            int idEquipage = equipageDao.creerEquipageAvecMembres(
                    nomEquipage,
                    List.of(
                            piloteCombo.getValue(),
                            copiloteCombo.getValue(),
                            chefCabineCombo.getValue(),
                            hotesseCombo.getValue(),
                            mecanicienCombo.getValue()
                    )
            );

            Equipage nouvelEquipage = new Equipage(idEquipage, nomEquipage);
            vol vol = creerVol(nouvelEquipage);
            new volDao(LaConnexion.seConnecter()).insertVol(vol);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté", "Le vol a été ajouté avec succès.");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Exception", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        if (numVolField.getText().isEmpty() || destinationField.getText().isEmpty()
                || heureDepartField.getValue() == null || heureArriveeField.getValue() == null
                || typeTrajetCombo.getValue() == null || statutCombo.getValue() == null
                || avionCombo.getValue() == null || piloteCombo.getValue() == null
                || copiloteCombo.getValue() == null || chefCabineCombo.getValue() == null
                || hotesseCombo.getValue() == null || mecanicienCombo.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
    }

    private vol creerVol(Equipage equipage) throws Exception {
        LocalDate dateDep = heureDepartField.getValue();
        LocalDate dateArr = heureArriveeField.getValue();
        LocalTime timeDep = LocalTime.parse(heureDepartTimeField.getText());
        LocalTime timeArr = LocalTime.parse(heureArriveeTimeField.getText());

        java.util.Date fullDep = new java.util.Date(Date.valueOf(dateDep).getTime() + Time.valueOf(timeDep).getTime());
        java.util.Date fullArr = new java.util.Date(Date.valueOf(dateArr).getTime() + Time.valueOf(timeArr).getTime());

        return new vol(
                numVolField.getText(),
                destinationField.getText(),
                fullDep,
                fullArr,
                typeTrajetCombo.getValue(),
                statutCombo.getValue(),
                avionCombo.getValue(),
                equipage
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

    private boolean validerMembresEquipage() {
        List<Membre> membres = List.of(
                piloteCombo.getValue(),
                copiloteCombo.getValue(),
                chefCabineCombo.getValue(),
                hotesseCombo.getValue(),
                mecanicienCombo.getValue()
        );

        // Vérifier qu’aucun n’est null
        if (membres.stream().anyMatch(m -> m == null)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Sélection incomplète", "Veuillez sélectionner tous les membres de l’équipage.");
            return false;
        }

        // Vérifier qu’ils sont tous différents
        long distinctCount = membres.stream().map(Membre::getId).distinct().count();
        if (distinctCount < membres.size()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Membres dupliqués", "Chaque membre de l’équipage doit être unique.");
            return false;
        }

        // Vérifier disponibilité
        boolean indisponible = membres.stream().anyMatch(m -> !m.isDisponible());
        if (indisponible) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Disponibilité", "Tous les membres doivent être disponibles.");
            return false;
        }
        // Marquer les membres comme indisponibles
        membreDao.mettreIndisponible(List.of(
                piloteCombo.getValue(),
                copiloteCombo.getValue(),
                chefCabineCombo.getValue(),
                hotesseCombo.getValue(),
                mecanicienCombo.getValue()
        ));
        return true;
    }

}
