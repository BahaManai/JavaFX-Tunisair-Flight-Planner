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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AjouterVolController {

    @FXML private TextField numVolField, destinationField, heureDepartTimeField, heureArriveeTimeField;
    @FXML private DatePicker heureDepartField, heureArriveeField;
    @FXML private ComboBox<TypeTrajet> typeTrajetCombo;
    @FXML private ComboBox<StatutVol> statutCombo;
    @FXML private ComboBox<Avion> avionCombo;
    @FXML private ComboBox<Membre> piloteCombo, copiloteCombo, chefCabineCombo, hotesseCombo, mecanicienCombo;

    private Connection connection;
    private DAOAvion daoAvion;
    private volDao daoVol;
    private EquipageDao equipageDao;

    @FXML
    public void initialize() {
        try {
            connection = LaConnexion.seConnecter();
            daoAvion = new DAOAvion(connection);
            daoVol = new volDao(connection);
            equipageDao = new EquipageDao(connection);

            typeTrajetCombo.setItems(FXCollections.observableArrayList(TypeTrajet.values()));
            statutCombo.setItems(FXCollections.observableArrayList(StatutVol.values()));
            chargerAvionsDisponibles();
            // Charger les membres par défaut (sans période spécifiée)
            chargerMembresParRole(null, null);

            // Ajouter des écouteurs pour rafraîchir les membres lorsque les horaires changent
            heureDepartField.valueProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            heureArriveeField.valueProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            heureDepartTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            heureArriveeTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Connexion", "Impossible d'établir la connexion à la base de données : " + e.getMessage());
        }
    }

    private void updateMembresDisponibles() {
        try {
            LocalDateTime heureDepart = getHeureDepart();
            LocalDateTime heureArrivee = getHeureArrivee();
            if (heureDepart != null && heureArrivee != null) {
                if (heureArrivee.isBefore(heureDepart)) {
                    showAlert(Alert.AlertType.WARNING, "Avertissement", "Horaires invalides", "L'heure d'arrivée doit être postérieure à l'heure de départ.");
                    return;
                }
                chargerMembresParRole(heureDepart, heureArrivee);
            } else {
                chargerMembresParRole(null, null); // Charger tous les membres si les horaires ne sont pas encore saisis
            }
        } catch (DateTimeParseException e) {
            // Ignorer les erreurs de format d'heure temporairement
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Mise à jour des membres", "Erreur lors de la mise à jour des membres : " + e.getMessage());
        }
    }

    private void chargerMembresParRole(LocalDateTime debut, LocalDateTime fin) {
        if (debut != null && fin != null) {
            piloteCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Pilote, debut, fin)));
            copiloteCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Copilote, debut, fin)));
            chefCabineCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Chef_de_cabine, debut, fin)));
            hotesseCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Hôtesse, debut, fin)));
            mecanicienCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRolePourPeriode(RoleMembre.Mécanicien, debut, fin)));
        } else {
            piloteCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Pilote)));
            copiloteCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Copilote)));
            chefCabineCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Chef_de_cabine)));
            hotesseCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Hôtesse)));
            mecanicienCombo.setItems(FXCollections.observableArrayList(membreDao.getMembresDisponiblesParRole(RoleMembre.Mécanicien)));
        }
    }

    @FXML
    public void ajouterVol() {
        try {
            if (!validerChamps() || !validerMembresEquipage()) return;

            String nomEquipage = "AutoGen" + System.currentTimeMillis();
            int idEquipage = equipageDao.creerEquipageAvecMembres(nomEquipage, List.of(
                    piloteCombo.getValue(),
                    copiloteCombo.getValue(),
                    chefCabineCombo.getValue(),
                    hotesseCombo.getValue(),
                    mecanicienCombo.getValue()
            ));

            // Vérifier la disponibilité des membres pour la période
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
            if (!daoVol.canAddVolForMembres(idEquipage, heureDepart, heureArrivee)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Conflit", "Un ou plusieurs membres ne sont pas disponibles pour cette période.");
                return;
            }

            Equipage nouvelEquipage = new Equipage(idEquipage, nomEquipage);
            vol vol = creerVol(nouvelEquipage);
            daoVol.insertVol(vol);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté", "Le vol a été ajouté avec succès.");
            // Rafraîchir les ComboBox après ajout
            updateMembresDisponibles();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Exception", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private LocalDateTime getHeureDepart() {
        if (heureDepartField.getValue() == null || heureDepartTimeField.getText().isEmpty()) return null;
        try {
            LocalDate dateDep = heureDepartField.getValue();
            LocalTime timeDep = LocalTime.parse(heureDepartTimeField.getText());
            return dateDep.atTime(timeDep);
        } catch (DateTimeParseException e) {
            return null; // Retourner null en cas de format invalide
        }
    }

    private LocalDateTime getHeureArrivee() {
        if (heureArriveeField.getValue() == null || heureArriveeTimeField.getText().isEmpty()) return null;
        try {
            LocalDate dateArr = heureArriveeField.getValue();
            LocalTime timeArr = LocalTime.parse(heureArriveeTimeField.getText());
            return dateArr.atTime(timeArr);
        } catch (DateTimeParseException e) {
            return null; // Retourner null en cas de format invalide
        }
    }

    private boolean validerChamps() {
        if (numVolField.getText().isEmpty() || destinationField.getText().isEmpty()
                || heureDepartField.getValue() == null || heureArriveeField.getValue() == null
                || heureDepartTimeField.getText().isEmpty() || heureArriveeTimeField.getText().isEmpty()
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
        LocalDateTime heureDepart = getHeureDepart();
        LocalDateTime heureArrivee = getHeureArrivee();
        if (heureDepart == null || heureArrivee == null) {
            throw new IllegalArgumentException("Horaires de départ ou d'arrivée invalides");
        }
        return new vol(
                numVolField.getText(),
                destinationField.getText(),
                Timestamp.valueOf(heureDepart),
                Timestamp.valueOf(heureArrivee),
                typeTrajetCombo.getValue(),
                statutCombo.getValue(),
                avionCombo.getValue(),
                equipage
        );
    }

    private void chargerAvionsDisponibles() {
        try {
            List<Avion> avions = daoAvion.getAvionsDisponibles();
            Platform.runLater(() -> avionCombo.setItems(FXCollections.observableArrayList(avions)));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement avions", e.getMessage()));
        }
    }

    private boolean showAlertError(String header, String content) {
        showAlert(Alert.AlertType.ERROR, "Erreur", header, content);
        return false;
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

        if (membres.stream().anyMatch(m -> m == null)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Sélection incomplète", "Veuillez sélectionner tous les membres.");
            return false;
        }
        if (membres.stream().map(Membre::getId).distinct().count() < membres.size()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Membres dupliqués", "Chaque membre doit être unique.");
            return false;
        }
        return true;
    }
}