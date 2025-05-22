package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.dao.UserDao;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminController {
    @FXML private TableView<User> pendingUsersTable;
    @FXML private TableColumn<User, String> nomCol, prenomCol, emailCol, cinCol;
    @FXML private TableView<vol> pendingFlightsTable;
    @FXML private TableColumn<vol, String> flightNumVolCol, flightOrigineCol, flightDestinationCol, flightDepartCol, flightStatutCol;

    private UserDao userDao;
    private volDao flightDao;
    private ObservableList<User> pendingUsers;
    private ObservableList<vol> pendingFlights;

    @FXML
    public void initialize() {
        userDao = new UserDao();
        flightDao = new volDao();
        pendingUsers = FXCollections.observableArrayList();
        pendingFlights = FXCollections.observableArrayList();

        // Setup user columns
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        cinCol.setCellValueFactory(new PropertyValueFactory<>("cin"));
        loadPendingUsers();

        // Setup flight columns
        flightNumVolCol.setCellValueFactory(new PropertyValueFactory<>("numVol"));
        flightOrigineCol.setCellValueFactory(new PropertyValueFactory<>("origine"));
        flightDestinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        flightDepartCol.setCellValueFactory(data -> {
            LocalDateTime dateTime = data.getValue().getHeureDepart().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            return new SimpleStringProperty(dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });
        flightStatutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        loadPendingFlights();
    }

    private void loadPendingUsers() {
        pendingUsers.setAll(userDao.getPendingApprovals());
        pendingUsersTable.setItems(pendingUsers);
    }

    private void loadPendingFlights() {
        try {
            pendingFlights.setAll(flightDao.getPendingFlights());
            pendingFlightsTable.setItems(pendingFlights);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des vols en attente: " + e.getMessage());
        }
    }

    @FXML
    private void handleApprove() {
        User selectedUser = pendingUsersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userDao.approveUser(selectedUser.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur approuvé avec succès.");
                loadPendingUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'approbation de l'utilisateur.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à approuver.");
        }
    }

    @FXML
    private void handleReject() {
        User selectedUser = pendingUsersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userDao.deleteUser(selectedUser.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur rejeté et supprimé.");
                loadPendingUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du rejet de l'utilisateur.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à rejeter.");
        }
    }

    @FXML
    private void handleFlightApprove() {
        vol selectedFlight = pendingFlightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                selectedFlight.setStatut(StatutVol.Planifié);
                flightDao.updateVol(selectedFlight);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol approuvé avec succès.");
                loadPendingFlights();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'approbation du vol: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun vol sélectionné", "Veuillez sélectionner un vol à approuver.");
        }
    }

    @FXML
    private void handleFlightReject() {
        vol selectedFlight = pendingFlightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                flightDao.deleteVol(selectedFlight.getIdVol());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Vol rejeté et supprimé.");
                loadPendingFlights();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec du rejet du vol: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun vol sélectionné", "Veuillez sélectionner un vol à rejeter.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}