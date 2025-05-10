package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.UserDao;
import com.beginsecure.tunisairaeroplan.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
public class AdminController {
    @FXML private TableView<User> pendingUsersTable;
    @FXML private TableColumn<User, String> nomCol, prenomCol, emailCol, cinCol;

    private UserDao userDao;

    @FXML
    public void initialize() {
        userDao = new UserDao();

        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        cinCol.setCellValueFactory(new PropertyValueFactory<>("cin"));
        cinCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        loadPendingUsers();
    }

    private void loadPendingUsers() {
        pendingUsersTable.getItems().setAll(userDao.getPendingApprovals());
    }

    @FXML
    private void handleApprove() {
        User selectedUser = pendingUsersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userDao.approveUser(selectedUser.getId())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Utilisateur approuvé avec succès.");
                alert.showAndWait();
                loadPendingUsers();
            }
        }
    }

    @FXML
    private void handleReject() {
        User selectedUser = pendingUsersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userDao.deleteUser(selectedUser.getId())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Utilisateur rejeté et supprimé.");
                alert.showAndWait();
                loadPendingUsers();
            }
        }
    }
}