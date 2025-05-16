package com.beginsecure.tunisairaeroplan.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Label bienvenueLabel;

    @FXML
    private Label projetDescriptionLabel;

    @FXML
    private Label developpeursLabel;

    @FXML
    public void initialize() {
        bienvenueLabel.setText("Bienvenue sur le système de gestion Tunisair !");
        projetDescriptionLabel.setText("Projet de gestion des vols, avions, et équipages.");
        developpeursLabel.setText("Ferchichi Molka & Manai Baha Eddine");
    }

}
