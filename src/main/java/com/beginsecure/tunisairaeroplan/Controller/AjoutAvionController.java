package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AjoutAvionController {

    @FXML private TextField tfCapacite;
    @FXML private CheckBox cbDisponible;
    @FXML private ComboBox<String> cbMarque;
    @FXML private ComboBox<String> cbModele;
    @FXML private ComboBox<TypeTrajet> cbTypeTrajet;
    private Map<String, Map<String, Integer>> avionsDisponibles = new HashMap<>();

    private DAOAvion daoAvion = new DAOAvion();

    @FXML
    public void initialize() {
        cbTypeTrajet.getItems().setAll(TypeTrajet.values());
        Map<String, Integer> airbus = new HashMap<>();
        airbus.put("A320", 180);
        airbus.put("A330", 277);
        airbus.put("A350", 300);

// 2. Boeing
        Map<String, Integer> boeing = new HashMap<>();
        boeing.put("737", 160);
        boeing.put("787", 242);
        boeing.put("777", 396);

// 3. Embraer
        Map<String, Integer> embraer = new HashMap<>();
        embraer.put("E170", 76);
        embraer.put("E190", 100);
        embraer.put("E195", 124);

// 4. Bombardier
        Map<String, Integer> bombardier = new HashMap<>();
        bombardier.put("CRJ700", 78);
        bombardier.put("CRJ900", 90);
        bombardier.put("CS300", 130); // aujourd’hui appelé Airbus A220-300

// 5. ATR
        Map<String, Integer> atr = new HashMap<>();
        atr.put("ATR 42", 50);
        atr.put("ATR 72", 70);
        atr.put("ATR 72-600", 78);

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
        try {
            Avion avion = new Avion();
            avion.setMarque(cbMarque.getValue());
            avion.setModele(cbModele.getValue());
            avion.setCapacite(Integer.parseInt(tfCapacite.getText()));
            avion.setEstDisponible(cbDisponible.isSelected());
            avion.setTypeTrajet(cbTypeTrajet.getValue());

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
        cbTypeTrajet.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}