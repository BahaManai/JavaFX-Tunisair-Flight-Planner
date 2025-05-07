package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AjoutAvionController {

    @FXML private TextField tfModele;
    @FXML private TextField tfCapacite;
    @FXML private CheckBox cbDisponible;
    @FXML private ComboBox<TypeTrajet> cbTypeTrajet;

    private DAOAvion daoAvion = new DAOAvion();

    @FXML
    public void initialize() {
        cbTypeTrajet.getItems().setAll(TypeTrajet.values());
    }

    @FXML
    private void handleAjouter() {
        try {
            Avion avion = new Avion();
            avion.setModele(tfModele.getText());
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
        Stage stage = (Stage) tfModele.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        tfModele.clear();
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