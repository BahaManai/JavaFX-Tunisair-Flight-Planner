package com.beginsecure.tunisairaeroplan.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        showHome();
    }

    @FXML
    public void showHome() {
        loadPage("Home.fxml");
    }

    @FXML
    public void showAvions() {
        loadPage("ListeAvion.fxml");
    }

    @FXML
    public void showPlanificationVols() {
        loadPage("liste_vol.fxml");
    }

    @FXML
    public void showArchives() {
        loadPage("ArchivVolView.fxml");
    }
    @FXML
    public void showListeAttente() {
        loadPage("PendingApprovals.fxml");

    }

    @FXML
    private void showMembres() {
        loadPage("FXMLMembre.fxml");
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
}
