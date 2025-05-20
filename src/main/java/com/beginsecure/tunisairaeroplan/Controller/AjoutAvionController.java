package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class AjoutAvionController {

    @FXML private TextField tfCapacite;
    @FXML private CheckBox cbDisponible;
    @FXML private ComboBox<String> cbMarque;
    @FXML private ComboBox<String> cbModele;

    private Map<String, Map<String, Integer>> avionsDisponibles = new HashMap<>();
    private DAOAvion daoAvion;

    @FXML
    public void initialize() {
        try {
            Connection connection = LaConnexion.seConnecter();
            daoAvion = new DAOAvion(connection);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de se connecter à la base de données.");
            return;
        }

        Map<String, Integer> airbus = Map.of("A320", 180, "A330", 277, "A350", 300);
        Map<String, Integer> boeing = Map.of("737", 160, "787", 242, "777", 396);
        Map<String, Integer> embraer = Map.of("E170", 76, "E190", 100, "E195", 124);
        Map<String, Integer> bombardier = Map.of("CRJ700", 78, "CRJ900", 90, "CS300", 130);
        Map<String, Integer> atr = Map.of("ATR 42", 50, "ATR 72", 70, "ATR 72-600", 78);

        avionsDisponibles.put("Airbus", airbus);
        avionsDisponibles.put("Boeing", boeing);
        avionsDisponibles.put("Embraer", embraer);
        avionsDisponibles.put("Bombardier", bombardier);
        avionsDisponibles.put("ATR", atr);

        cbMarque.getItems().addAll(avionsDisponibles.keySet());

        cbMarque.setOnAction(e -> {
            String selectedMarque = cbMarque.getValue();
            if (selectedMarque != null) {
                cbModele.getItems().setAll(avionsDisponibles.get(selectedMarque).keySet());
            }
        });

        cbModele.setOnAction(e -> {
            String selectedMarque = cbMarque.getValue();
            String selectedModele = cbModele.getValue();
            if (selectedMarque != null && selectedModele != null) {
                Integer capacite = avionsDisponibles.get(selectedMarque).get(selectedModele);
                tfCapacite.setText(capacite.toString());
            }
        });
    }

    @FXML
    private void handleAjouter() {
        if (cbMarque.getValue() == null || cbMarque.getValue().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une marque");
            return;
        }

        if (cbModele.getValue() == null || cbModele.getValue().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un modèle");
            return;
        }

        if (tfCapacite.getText() == null || tfCapacite.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une capacité");
            return;
        }

        try {
            // Vérification que la capacité est un nombre valide
            int capacite = Integer.parseInt(tfCapacite.getText());
            if (capacite <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La capacité doit être un nombre positif");
                return;
            }

            Avion avion = new Avion();
            avion.setMarque(cbMarque.getValue());
            avion.setModele(cbModele.getValue());
            avion.setCapacite(capacite);
            avion.setEstDisponible(cbDisponible.isSelected());

            if (daoAvion.addAvion(avion)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Avion ajouté avec succès");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de l'avion");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La capacité doit être un nombre valide");
        }
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) cbModele.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        cbMarque.getSelectionModel().clearSelection();
        cbModele.getItems().clear();
        tfCapacite.clear();
        cbDisponible.setSelected(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}