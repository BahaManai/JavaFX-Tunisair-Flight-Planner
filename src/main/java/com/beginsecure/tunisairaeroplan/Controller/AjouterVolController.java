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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
    @FXML private ComboBox<String> paysOrigineCombo;
    @FXML private ComboBox<String> aeroportOrigineCombo;
    @FXML private ComboBox<String> paysDestinationCombo;
    @FXML private ComboBox<String> aeroportDestinationCombo;
    private Connection connection;
    private DAOAvion daoAvion;
    private volDao daoVol;
    private EquipageDao equipageDao;
    private void genererNumeroVol() {
        try {
            int dernierNumero = daoVol.getDernierNumeroVol();
            int nouveauNumero = dernierNumero + 1;
            numVolField.setText("vol " + nouveauNumero);
            numVolField.setEditable(false);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Numéro de vol", "Impossible de générer le numéro de vol : " + e.getMessage());
            numVolField.setText("vol 1");
            numVolField.setEditable(false);
        }
    }
    @FXML
    public void initialize() {
        try {
            connection = LaConnexion.seConnecter();
            daoAvion = new DAOAvion(connection);
            daoVol = new volDao(connection);
            equipageDao = new EquipageDao(connection);
            genererNumeroVol();
            typeTrajetCombo.setItems(FXCollections.observableArrayList(TypeTrajet.values()));
            statutCombo.setItems(FXCollections.observableArrayList(StatutVol.values()));
<<<<<<< HEAD
            chargerAvionsDisponibles();
            chargerMembresParRole(null, null);
            heureDepartField.valueProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            heureArriveeField.valueProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            heureDepartTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            heureArriveeTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateMembresDisponibles());
            paysOrigineCombo.setItems(FXCollections.observableArrayList(LocationData.getCountries()));
            paysDestinationCombo.setItems(FXCollections.observableArrayList(LocationData.getCountries()));
            paysOrigineCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                aeroportOrigineCombo.getItems().clear();
                if (newVal != null) {
                    aeroportOrigineCombo.setItems(FXCollections.observableArrayList(
                            LocationData.getAirportsForCountry(newVal))
                    );
                }
            });
            paysDestinationCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                aeroportDestinationCombo.getItems().clear();
                if (newVal != null) {
                    aeroportDestinationCombo.setItems(FXCollections.observableArrayList(
                            LocationData.getAirportsForCountry(newVal))
                    );
                }
            });
=======
            chargerAvionsDisponibles(null, null); // Charger tous les avions disponibles par défaut
            chargerMembresParRole(null, null); // Charger les membres par défaut

            // Ajouter des écouteurs pour rafraîchir les avions et membres lorsque les horaires changent
            heureDepartField.valueProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
            heureArriveeField.valueProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
            heureDepartTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());
            heureArriveeTimeField.textProperty().addListener((obs, oldVal, newVal) -> updateDisponibilites());

>>>>>>> 719dc30b59cd1851d2478858b3c8b40d7b675b56
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Connexion", "Impossible d'établir la connexion à la base de données : " + e.getMessage());
        }
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
<<<<<<< HEAD
=======
                chargerAvionsDisponibles(null, null);
>>>>>>> 719dc30b59cd1851d2478858b3c8b40d7b675b56
                chargerMembresParRole(null, null);
            }
        } catch (DateTimeParseException e) {
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Mise à jour des disponibilités", "Erreur lors de la mise à jour des disponibilités : " + e.getMessage());
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

    private void chargerAvionsDisponibles(LocalDateTime debut, LocalDateTime fin) {
        try {
            List<Avion> avions;
            if (debut != null && fin != null) {
                avions = daoAvion.getAvionsDisponiblesPourPeriode(debut, fin);
            } else {
                avions = daoAvion.getAvionsDisponibles();
            }
            Platform.runLater(() -> avionCombo.setItems(FXCollections.observableArrayList(avions)));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement avions", e.getMessage()));
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

            // Vérifier la disponibilité des membres et de l'avion
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
                showAlert(Alert.AlertType.ERROR, "Erreur", "Conflit membres", "Un ou plusieurs membres ne sont pas disponibles pour cette période.");
                return;
            }
            if (!daoVol.canAddVolForAvion(avionCombo.getValue().getId(), heureDepart, heureArrivee)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Conflit avion", "L'avion sélectionné n'est pas disponible pour cette période.");
                return;
            }

            Equipage nouvelEquipage = new Equipage(idEquipage, nomEquipage);
            vol vol = creerVol(nouvelEquipage);
            daoVol.insertVol(vol);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol ajouté", "Le vol a été ajouté avec succès.");
            // Rafraîchir les ComboBox après ajout
            updateDisponibilites();

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
            return null;
        }
    }

    private boolean validerChamps() {
        if (numVolField.getText().isEmpty() || destinationField.getText().isEmpty()
                || heureDepartField.getValue() == null || heureArriveeField.getValue() == null
                || heureDepartTimeField.getText().isEmpty() || heureArriveeTimeField.getText().isEmpty()
                || typeTrajetCombo.getValue() == null || statutCombo.getValue() == null
                || avionCombo.getValue() == null || piloteCombo.getValue() == null  || aeroportOrigineCombo.getValue() == null
                || aeroportDestinationCombo.getValue() == null
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
                aeroportOrigineCombo.getValue(),  // Origine
                aeroportDestinationCombo.getValue(),  // Destination
                Timestamp.valueOf(heureDepart),
                Timestamp.valueOf(heureArrivee),
                typeTrajetCombo.getValue(),
                statutCombo.getValue(),
                avionCombo.getValue(),
                equipage
        );
    }

<<<<<<< HEAD
    private void chargerAvionsDisponibles() {
        try {
            List<Avion> avions = daoAvion.getAvionsDisponibles();
            Platform.runLater(() -> avionCombo.setItems(FXCollections.observableArrayList(avions)));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement avions", e.getMessage()));
        }
    }
    private boolean validerDates(LocalDateTime depart, LocalDateTime arrivee) {
        if (depart == null || arrivee == null) {
            return false;
        }
        if (arrivee.isBefore(depart)) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "Validation des dates",
                    "La date d'arrivée doit être postérieure à la date de départ");
            return false;
        }
        if (depart.isBefore(LocalDateTime.now())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de date", "Validation des dates",
                    "La date de départ ne peut pas être dans le passé");
            return false;
        }
        long dureeHeures = java.time.Duration.between(depart, arrivee).toHours();
        if (dureeHeures > 24) {
            showAlert(Alert.AlertType.ERROR, "Erreur de durée", "Validation de la durée",
                    "La durée du vol ne peut pas dépasser 24 heures");
            return false;
        }

        return true;
    }

  private boolean showAlertError(String header, String content) {
=======
    private boolean showAlertError(String header, String content) {
>>>>>>> 719dc30b59cd1851d2478858b3c8b40d7b675b56
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
    @FXML
    private void retourListeVols() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/liste_vol.fxml"));
            Node listeVolView = loader.load();

            StackPane contentPane = (StackPane) numVolField.getScene().lookup("#contentPane");
            if (contentPane != null) {
                contentPane.getChildren().setAll(listeVolView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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