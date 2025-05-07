package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModifierAvionController {

    @FXML private TextField tfModele;
    @FXML private TextField tfCapacite;
    @FXML private CheckBox cbDisponible;
    @FXML private ComboBox<TypeTrajet> cbTypeTrajet;
    @FXML private Button btnValider;

    private Avion avionToEdit;
    private DAOAvion daoAvion = new DAOAvion();

    public void setAvionData(Avion avion) {
        this.avionToEdit = avion;
        tfModele.setText(avion.getModele());
        tfCapacite.setText(String.valueOf(avion.getCapacite()));
        cbDisponible.setSelected(avion.isEstDisponible());
        cbTypeTrajet.setValue(avion.getTypeTrajet());
    }

    @FXML
    public void initialize() {
        cbTypeTrajet.getItems().setAll(TypeTrajet.values());
    }

    @FXML
    private void handleValider() {
        try {
            avionToEdit.setModele(tfModele.getText());
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