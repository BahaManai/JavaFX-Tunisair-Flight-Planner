package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class ModifierVolController {

    @FXML private TextField numVolField;
    @FXML private TextField destinationField;
    @FXML private DatePicker dateDepartPicker;
    @FXML private DatePicker dateArriveePicker;
    @FXML private TextField heureDepartField;
    @FXML private TextField heureArriveeField;
    @FXML private ComboBox<TypeTrajet> typeTrajetComboBox;
    @FXML private ComboBox<StatutVol> statutComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private vol selectedVol;

    @FXML
    public void initialize() {
        typeTrajetComboBox.getItems().setAll(TypeTrajet.values());
        statutComboBox.getItems().setAll(StatutVol.values());
    }

    public void initData(vol selectedVol) {
        this.selectedVol = selectedVol;

        Date heureDepartDate = selectedVol.getHeureDepart();
        Date heureArriveeDate = selectedVol.getHeureArrivee();

        LocalDateTime heureDepart = heureDepartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime heureArrivee = heureArriveeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        numVolField.setText(selectedVol.getNumVol());
        destinationField.setText(selectedVol.getDestination());
        dateDepartPicker.setValue(heureDepart.toLocalDate());
        dateArriveePicker.setValue(heureArrivee.toLocalDate());
        heureDepartField.setText(heureDepart.toLocalTime().toString());
        heureArriveeField.setText(heureArrivee.toLocalTime().toString());
        typeTrajetComboBox.setValue(selectedVol.getTypeTrajet());
        statutComboBox.setValue(selectedVol.getStatut());
    }

    @FXML
    public void saveVol() {
        try {
            String numVol = numVolField.getText();
            String destination = destinationField.getText();
            LocalDate dateDepart = dateDepartPicker.getValue();
            LocalDate dateArrivee = dateArriveePicker.getValue();
            LocalTime heureDepart = LocalTime.parse(heureDepartField.getText());
            LocalTime heureArrivee = LocalTime.parse(heureArriveeField.getText());
            TypeTrajet typeTrajet = typeTrajetComboBox.getValue();
            StatutVol statut = statutComboBox.getValue();

            if (numVol.isEmpty() || destination.isEmpty() || typeTrajet == null || statut == null || dateDepart == null || dateArrivee == null || heureDepart == null || heureArrivee == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.", null);
                return;
            }

            Connection conn = null;
            volDao dao = null;
            try {
                conn = LaConnexion.seConnecter();
                dao = new volDao(conn);

                LocalDateTime heureDepartDateTime = LocalDateTime.of(dateDepart, heureDepart);
                LocalDateTime heureArriveeDateTime = LocalDateTime.of(dateArrivee, heureArrivee);

                Date heureDepartDateFinal = Date.from(heureDepartDateTime.atZone(ZoneId.systemDefault()).toInstant());
                Date heureArriveeDateFinal = Date.from(heureArriveeDateTime.atZone(ZoneId.systemDefault()).toInstant());

                selectedVol.setNumeroVol(numVol);
                selectedVol.setDestination(destination);
                selectedVol.setHeureDepart(heureDepartDateFinal);
                selectedVol.setHeureArrivee(heureArriveeDateFinal);
                selectedVol.setTypeTrajet(typeTrajet);
                selectedVol.setStatut(statut);

                dao.updateVol(selectedVol);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol modifié avec succès.", null);
                closeStage();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Erreur lors de la mise à jour du vol : " + e.getMessage(), null);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez entrer un horaire valide : " + e.getMessage(), null);
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

    private void showAlert(Alert.AlertType type, String title, String content, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
