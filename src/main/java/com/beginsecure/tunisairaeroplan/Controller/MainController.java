package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.Services.AuthService;
import com.beginsecure.tunisairaeroplan.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;
    @FXML
    private VBox menuVBox;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label userEmailLabel;

    @FXML
    public void initialize() {
        // Check user role and restrict menu items
        restrictMenuBasedOnRole();
        showHome();
    }

    public StackPane getContentPane() {
        return contentPane;
    }

    @FXML
    public void showHome() {
        loadPage("Dashboard.fxml");
    }

    @FXML
    public void showAvions() {
        if (isAdmin()) {
            loadPage("ListeAvion.fxml");
        }
    }

    @FXML
    public void showPlanificationVols() {
        loadPage("liste_vol.fxml");
    }

    @FXML
    public void showArchives() {
        if (isAdmin()) {
            loadPage("ArchivVolView.fxml");
        }
    }

    @FXML
    public void showListeAttente() {
        if (isAdmin()) {
            loadPage("PendingApprovals.fxml");
        }
    }

    @FXML
    public void showEquipages() {
        if (isAdmin()) {
            loadPage("ListeEquipages.fxml");
        }
    }

    @FXML
    public void showMembres() {
        if (isAdmin()) {
            loadPage("FXMLMembre.fxml");
        }
    }

    @FXML
    public void showCalendrier() {
        loadPage("CalendrierVol.fxml");
    }

    @FXML
    public void showParametres() {
        System.out.println("Section paramètres en cours de développement.");
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
            userRoleLabel.setText(user.isAdmin() ? "Admin" : "Utilisateur");
            userEmailLabel.setText(user.getEmail());

            // Hide admin-specific buttons for non-admins
            if (!user.isAdmin()) {
                menuVBox.getChildren().removeIf(node -> {
                    if (node instanceof Button) {
                        String text = ((Button) node).getText();
                        return "Équipages".equals(text) ||
                                "Pilotes - Membres".equals(text) ||
                                "Gestion des Avions".equals(text) ||
                                "Vols archivés".equals(text) ||
                                "Liste d'attente".equals(text);
                    }
                    return false;
                });
            }
        }
    }
}