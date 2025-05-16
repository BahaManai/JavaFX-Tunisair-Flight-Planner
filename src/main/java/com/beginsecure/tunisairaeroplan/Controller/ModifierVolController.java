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
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ModifierVolController {

    @FXML private TextField numVolField;
    @FXML private DatePicker heureDepartField;
    @FXML private DatePicker heureArriveeField;
    @FXML private TextField heureDepartTimeField;
    @FXML private TextField heureArriveeTimeField;
    @FXML private ComboBox<TypeTrajet> typeTrajetCombo;
    @FXML private ComboBox<StatutVol> statutCombo;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ComboBox<Avion> avionCombo;
    @FXML private ComboBox<Membre> piloteCombo, copiloteCombo, chefCabineCombo, hotesseCombo, mecanicienCombo, agentSecuriteCombo;
    @FXML private ComboBox<String> paysOrigineCombo;
    @FXML private ComboBox<String> aeroportOrigineCombo;
    @FXML private ComboBox<String> paysDestinationCombo;
    @FXML private ComboBox<String> aeroportDestinationCombo;

    private vol selectedVol;
    private volDao daoVol;
    private DAOAvion daoAvion;
    private EquipageDao equipageDao;
    private membreDao membreDao;
    private Connection connection;
    private int originalEquipageId;

    @FXML
    public void initialize() {
        try {
            connection = LaConnexion.seConnecter();
            daoVol = new volDao(connection);
            daoAvion = new DAOAvion(connection);
            equipageDao = new EquipageDao(connection);
            membreDao = new membreDao(connection);
            typeTrajetCombo.setItems(FXCollections.observableArrayList(TypeTrajet.values()));
            statutCombo.setItems(FXCollections.observableArrayList(StatutVol.values()));
            chargerAvionsDisponibles(null, null);
            chargerMembresParRole(null, null);

            // Populate country and airport combos
            paysOrigineCombo.setItems(FXCollections.observableArrayList(LocationData.getCountries()));
            paysDestinationCombo.setItems(FXCollections.observableArrayList(LocationData.getCountries()));
            paysOrigineCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                aeroportOrigineCombo.getItems().clear();
                if (newVal != null) {
                    aeroportOrigineCombo.setItems(FXCollections.observableArrayList(LocationData.getAirportsForCountry(newVal)));
                }
                verifierPaysIdentiques();
            });
            paysDestinationCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                aeroportDestinationCombo.getItems().clear();
                if (newVal != null) {
                    aeroportDestinationCombo.setItems(FXCollections.observableArrayList(LocationData.getAirportsForCountry(newVal)));
                }
                verifierPaysIdentiques();
            });

            // Add listeners to refresh availability
            heureDepartField.valueProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
            heureArriveeField.valueProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
            heureDepartTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
            heureArriveeTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Connexion", "Impossible d'établir la connexion à la base de données : " + e.getMessage());
        }
    }

    public void initData(vol selectedVol) {
        this.selectedVol = selectedVol;
        originalEquipageId = selectedVol.getEquipage().getId();

        Date heureDepartDate = selectedVol.getHeureDepart();
        Date heureArriveeDate = selectedVol.getHeureArrivee();

        LocalDateTime heureDepart = heureDepartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime heureArrivee = heureArriveeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        numVolField.setText(selectedVol.getNumVol());
        // Use aeroportDestinationCombo instead of destinationField
        aeroportDestinationCombo.setValue(selectedVol.getDestination());
        heureDepartField.setValue(heureDepart.toLocalDate());
        heureArriveeField.setValue(heureArrivee.toLocalDate());
        heureDepartTimeField.setText(heureDepart.toLocalTime().toString());
        heureArriveeTimeField.setText(heureArrivee.toLocalTime().toString());
        typeTrajetCombo.setValue(selectedVol.getTypeTrajet());
        statutCombo.setValue(selectedVol.getStatut());
        avionCombo.setValue(selectedVol.getAvion());
        paysOrigineCombo.setValue(selectedVol.getOrigine());
        paysDestinationCombo.setValue(selectedVol.getDestination());
        aeroportOrigineCombo.setValue(selectedVol.getOrigine());

        // Pre-populate crew members
        try {
            List<Membre> membres = equipageDao.getMembresByEquipageId(originalEquipageId);
            for (Membre m : membres) {
                switch (m.getRole()) {
                    case Pilote -> piloteCombo.setValue(m);
                    case Copilote -> copiloteCombo.setValue(m);
                    case Chef_de_cabine -> chefCabineCombo.setValue(m);
                    case Hôtesse -> hotesseCombo.setValue(m);
                    case Mécanicien -> mecanicienCombo.setValue(m);
                    case Agent_de_sécurité -> agentSecuriteCombo.setValue(m);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement de l'équipage", "Erreur lors du chargement des membres de l'équipage : " + e.getMessage());
        }
    }

    @FXML
    public void saveVol() {
        try {
            if (!validerChamps() || !validerMembresEquipage()) return;

            LocalDateTime heureDepart = getHeureDepart();
            LocalDateTime heureArrivee = getHeureArrivee();
            if (heureDepart == null || heureArrivee == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Horaires manquants", "Veuillez spécifier les horaires de départ et d'arrivée.");
                return;
            }
            if (heureArrivee.isBefore(heureDepart)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Horaires invalides", "L'heure d'arrivée doit être postérieure à l'heure de départ.");
                return;
            }

            // Collect member IDs for availability check
            List<Membre> newMembres = new ArrayList<>();
            newMembres.add(piloteCombo.getValue());
            newMembres.add(copiloteCombo.getValue());
            if (chefCabineCombo.getValue() != null) newMembres.add(chefCabineCombo.getValue());
            if (hotesseCombo.getValue() != null) newMembres.add(hotesseCombo.getValue());
            if (mecanicienCombo.getValue() != null) newMembres.add(mecanicienCombo.getValue());
            if (agentSecuriteCombo.getValue() != null) newMembres.add(agentSecuriteCombo.getValue());

            List<Integer> membreIds = newMembres.stream()
                    .filter(Objects::nonNull)
                    .map(Membre::getId)
                    .toList();

            // Vérifier la disponibilité des membres en excluant le vol en cours
            if (!daoVol.canAddVolForMembres(membreIds, heureDepart, heureArrivee, selectedVol.getIdVol())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Conflit membres", "Un ou plusieurs membres ne sont pas disponibles pour cette période.");
                return;
            }

            // Vérifier la disponibilité de l'avion en excluant le vol en cours (nécessite une mise à jour dans volDao)
            if (!daoVol.canAddVolForAvion(avionCombo.getValue().getId(), heureDepart, heureArrivee, selectedVol.getIdVol())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Conflit avion", "L'avion sélectionné n'est pas disponible pour cette période.");
                return;
            }

            // Mettre à jour le vol avec le nouvel equipage_id avant de supprimer l'ancien
            int newEquipageId = originalEquipageId;
            boolean membersChanged = hasMembersChanged(newMembres);
            if (membersChanged) {
                String nomEquipage = "AutoGen" + System.currentTimeMillis();
                newEquipageId = equipageDao.creerEquipageAvecMembres(nomEquipage, newMembres);
                selectedVol.getEquipage().setId(newEquipageId); // Mettre à jour l'ID avant la suppression
                daoVol.updateVol(selectedVol); // Mettre à jour le vol avec le nouvel equipage_id
                equipageDao.deleteEquipage(originalEquipageId); // Supprimer l'ancien équipage après mise à jour
            }

            // Mettre à jour les autres champs du vol
            selectedVol.setNumeroVol(numVolField.getText());
            selectedVol.setDestination(aeroportDestinationCombo.getValue());
            selectedVol.setHeureDepart(Date.from(heureDepart.atZone(ZoneId.systemDefault()).toInstant()));
            selectedVol.setHeureArrivee(Date.from(heureArrivee.atZone(ZoneId.systemDefault()).toInstant()));
            selectedVol.setTypeTrajet(typeTrajetCombo.getValue());
            selectedVol.setStatut(statutCombo.getValue());
            selectedVol.setAvion(avionCombo.getValue());
            selectedVol.setOrigine(aeroportOrigineCombo.getValue());
            selectedVol.setDestination(aeroportDestinationCombo.getValue());

            daoVol.updateVol(selectedVol);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol modifié avec succès.", null);
            closeStage();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Modification du vol", "Erreur lors de la modification du vol : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Exception", "Une erreur inattendue est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean hasMembersChanged(List<Membre> newMembres) {
        try {
            List<Membre> originalMembres = equipageDao.getMembresByEquipageId(originalEquipageId);
            if (originalMembres.size() != newMembres.size()) return true;
            List<Integer> originalIds = originalMembres.stream().map(Membre::getId).sorted().toList();
            List<Integer> newIds = newMembres.stream().filter(Objects::nonNull).map(Membre::getId).sorted().toList();
            return !originalIds.equals(newIds);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Comparaison des membres", "Erreur lors de la comparaison des membres de l'équipage : " + e.getMessage());
            return true; // Assume members changed to proceed with creating a new equipage
        }
    }

    @FXML
    public void cancelModification() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private LocalDateTime getHeureDepart() {
        if (heureDepartField.getValue() == null || heureDepartTimeField.getText().isEmpty()) return null;
        try {
            LocalDate dateDep = heureDepartField.getValue();
            LocalTime timeDep = LocalTime.parse(heureDepartTimeField.getText());
            return dateDep.atTime(timeDep);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalDateTime getHeureArrivee() {
        if (heureArriveeField.getValue() == null || heureArriveeTimeField.getText().isEmpty()) return null;
        try {
            LocalDate dateArr = heureArriveeField.getValue();
            LocalTime timeArr = LocalTime.parse(heureArriveeTimeField.getText());
            return dateArr.atTime(timeArr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean validerChamps() {
        if (numVolField.getText().isEmpty()
                || heureDepartField.getValue() == null || heureArriveeField.getValue() == null
                || heureDepartTimeField.getText().isEmpty() || heureArriveeTimeField.getText().isEmpty()
                || typeTrajetCombo.getValue() == null || statutCombo.getValue() == null
                || avionCombo.getValue() == null || piloteCombo.getValue() == null
                || copiloteCombo.getValue() == null
                || aeroportOrigineCombo.getValue() == null || aeroportDestinationCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Champs manquants", "Veuillez remplir tous les champs obligatoires (Pilote et Copilote sont requis).");
            return false;
        }
        return true;
    }

    private boolean validerMembresEquipage() {
        List<Membre> membres = new ArrayList<>();
        membres.add(piloteCombo.getValue());
        membres.add(copiloteCombo.getValue());
        if (chefCabineCombo.getValue() != null) membres.add(chefCabineCombo.getValue());
        if (hotesseCombo.getValue() != null) membres.add(hotesseCombo.getValue());
        if (mecanicienCombo.getValue() != null) membres.add(mecanicienCombo.getValue());
        if (agentSecuriteCombo.getValue() != null) membres.add(agentSecuriteCombo.getValue());

        long distinctCount = membres.stream()
                .filter(Objects::nonNull)
                .map(Membre::getId)
                .distinct()
                .count();
        long nonNullCount = membres.stream().filter(Objects::nonNull).count();

        if (distinctCount < nonNullCount) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Membres dupliqués", "Chaque membre doit être unique.");
            return false;
        }
        return true;
    }

    private void updateDisponibilites() {
        try {
            LocalDateTime heureDepart = getHeureDepart();
            LocalDateTime heureArrivee = getHeureArrivee();
            if (heureDepart != null && heureArrivee != null) {
                if (heureArrivee.isBefore(heureDepart)) {
                    showAlert(Alert.AlertType.WARNING, "Avertissement", "Horaires invalides", "L'heure d'arrivée doit être postérieure à l'heure de départ.");
                    return;
                }
                chargerAvionsDisponibles(heureDepart, heureArrivee);
                chargerMembresParRole(heureDepart, heureArrivee);
            } else {
                chargerAvionsDisponibles(null, null);
                chargerMembresParRole(null, null);
            }
        } catch (DateTimeParseException e) {
            // Silently ignore invalid date/time format
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Mise à jour des disponibilités", "Erreur lors de la mise à jour des disponibilités : " + e.getMessage());
        }
    }

    private void chargerMembresParRole(LocalDateTime debut, LocalDateTime fin) {
        try {
            if (debut != null && fin != null) {
                piloteCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Pilote, debut, fin)));
                copiloteCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Copilote, debut, fin)));
                chefCabineCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Chef_de_cabine, debut, fin)));
                hotesseCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Hôtesse, debut, fin)));
                mecanicienCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Mécanicien, debut, fin)));
                agentSecuriteCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Agent_de_sécurité, debut, fin)));
            } else {
                piloteCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRole(RoleMembre.Pilote)));
                copiloteCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRole(RoleMembre.Copilote)));
                chefCabineCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRole(RoleMembre.Chef_de_cabine)));
                hotesseCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRole(RoleMembre.Hôtesse)));
                mecanicienCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRole(RoleMembre.Mécanicien)));
                agentSecuriteCombo.setItems(FXCollections.observableArrayList(
                        membreDao.getMembresDisponiblesParRole(RoleMembre.Agent_de_sécurité)));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement des membres", "Erreur lors du chargement des membres : " + e.getMessage());
        }
    }

    private void chargerAvionsDisponibles(LocalDateTime debut, LocalDateTime fin) {
        List<Avion> avions;
        if (debut != null && fin != null) {
            avions = daoAvion.getAvionsDisponiblesPourPeriode(debut, fin);
        } else {
            avions = daoAvion.getAvionsDisponibles();
        }
        Platform.runLater(() -> avionCombo.setItems(FXCollections.observableArrayList(avions)));
    }

    private void verifierPaysIdentiques() {
        String paysOrigine = paysOrigineCombo.getValue();
        String paysDestination = paysDestinationCombo.getValue();

        if (paysOrigine != null && paysDestination != null && paysOrigine.equals(paysDestination)) {
            aeroportDestinationCombo.setDisable(true);
            if (aeroportDestinationCombo.getValue() != null && aeroportDestinationCombo.getValue().equals(aeroportOrigineCombo.getValue())) {
                aeroportDestinationCombo.setValue(null);
            }
        } else {
            aeroportDestinationCombo.setDisable(false);
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