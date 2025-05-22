package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.Services.AuthService;
import com.beginsecure.tunisairaeroplan.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PlannerHomeController {

    @FXML
    private StackPane contentPane;
    @FXML
    private VBox sidebar;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label userEmailLabel;

    @FXML
    public void initialize() {
        // Check user role and set labels
        restrictMenuBasedOnRole();
        showCalendrier(); // Default view for planner
    }

    public StackPane getContentPane() {
        return contentPane;
    }

    @FXML
    public void showCalendrier() {
        loadPage("CalendrierVol.fxml");
    }

    @FXML
    public void showPlanificationVols() {
        loadPage("liste_vol.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/" + fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("Le fichier FXML n'a pas été trouvé : " + fxmlFile);
            }
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page : " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.logout();
            Parent root = FXMLLoader.load(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/Accueil.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAdmin() {
        AuthService authService = new AuthService();
        User user = authService.getUserByEmail(SessionManager.getCurrentUser());
        return user != null && user.isAdmin();
    }

    private void restrictMenuBasedOnRole() {
        AuthService authService = new AuthService();
        User user = authService.getUserByEmail(SessionManager.getCurrentUser());
        if (user != null) {
            userRoleLabel.setText(user.isAdmin() ? "Administrateur" : "Planificateur de vols");
            userEmailLabel.setText(user.getEmail());
        }
    }
}