package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ModifierAvionController {

    @FXML private ComboBox<String> cbMarque;
    @FXML private ComboBox<String> cbModele;
    @FXML private TextField tfCapacite;
    @FXML private CheckBox cbDisponible;
    @FXML private ComboBox<TypeTrajet> cbTypeTrajet;
    @FXML private Button btnValider;
    private Map<String, Map<String, Integer>> avionsDisponibles = new HashMap<>();

    private Avion avionToEdit;
    private DAOAvion daoAvion = new DAOAvion();

    public void setAvionData(Avion avion) {
        this.avionToEdit = avion;
        cbMarque.setValue(avion.getMarque());
        cbModele.setValue(avion.getModele());
        tfCapacite.setText(String.valueOf(avion.getCapacite()));
        cbDisponible.setSelected(avion.isEstDisponible());
        cbTypeTrajet.setValue(avion.getTypeTrajet());
    }

    @FXML
    public void initialize() {
        cbTypeTrajet.getItems().setAll(TypeTrajet.values());
        Map<String, Integer> airbus = new HashMap<>();
        airbus.put("A320", 180);
        airbus.put("A330", 277);

        Map<String, Integer> boeing = new HashMap<>();
        boeing.put("737", 160);
        boeing.put("787", 242);

        avionsDisponibles.put("Airbus", airbus);
        avionsDisponibles.put("Boeing", boeing);

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
    private void handleValider() {
        try {
            avionToEdit.setMarque(cbMarque.getValue());
            avionToEdit.setModele(cbModele.getValue());
            avionToEdit.setCapacite(Integer.parseInt(tfCapacite.getText()));
            avionToEdit.setEstDisponible(cbDisponible.isSelected());
            avionToEdit.setTypeTrajet(cbTypeTrajet.getValue());

            if (daoAvion.updateAvion(avionToEdit)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Avion modifié avec succès");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la modification de l'avion");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La capacité doit être un nombre valide");
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnValider.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}