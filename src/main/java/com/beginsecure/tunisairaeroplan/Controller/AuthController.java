package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.Services.AuthService;
import com.beginsecure.tunisairaeroplan.utilites.MainApp;
import com.beginsecure.tunisairaeroplan.utilites.testRegistration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML private VBox rootPane;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button forgotPasswordBtn;
    private AuthService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthService();
        setupEventHandlers();
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        User user = authService.authenticate(email, password);
        if (user != null) {
            try {
                if (user.isAdmin()) {
                    MainApp.showAdminDashboard();
                } else {
                    MainApp.showUserDashboard();
                }
            } catch (IOException e) {
                showError("Erreur lors de la navigation");
                e.printStackTrace();
            }
        } else {
            showError("Email ou mot de passe incorrect, ou compte non approuvé");
        }
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(event -> {
            handleLogin();
        });
        registerButton.setOnAction(event -> {
            try {
                testRegistration.showRegistrationForm();
            } catch (IOException e) {
                showError("Erreur lors de l'ouverture du formulaire d'inscription");
                e.printStackTrace();
            }
        });
        forgotPasswordBtn.setOnAction(event -> {
            showError("Fonctionnalité non implémentée");
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}