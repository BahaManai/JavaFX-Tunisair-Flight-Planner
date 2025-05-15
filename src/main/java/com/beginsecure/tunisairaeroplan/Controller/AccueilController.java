package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AccueilController {
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Button btnLogout;
    @FXML private Label lblWelcome;
    @FXML private VBox navbar;

    private boolean isLoggedIn = false; // À remplacer par votre logique d'authentification réelle

    @FXML
    public void initialize() {
        updateNavbar();
    }


    private String getUserName() {
        return "Utilisateur";
    }

    @FXML
    private void handleLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/LoginView.fxml"));
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleRegister() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/RegistrationForm.fxml"));
        Stage stage = (Stage) btnRegister.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleLogout() {
        isLoggedIn = false;
        updateNavbar();
    }
    private void updateNavbar() {
        if (SessionManager.isLoggedIn()) {
            btnLogin.setVisible(false);
            btnRegister.setVisible(false);
            btnLogout.setVisible(true);
            lblWelcome.setText("Bienvenue, " + SessionManager.getCurrentUser());
        } else {
            btnLogin.setVisible(true);
            btnRegister.setVisible(true);
            btnLogout.setVisible(false);
            lblWelcome.setText("Bienvenue sur Tunisair - Gestion des Vols");
        }
    }
}