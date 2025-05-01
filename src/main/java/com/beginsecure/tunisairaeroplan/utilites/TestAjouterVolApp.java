package com.beginsecure.tunisairaeroplan.utilites;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestAjouterVolApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Chargement du fichier FXML avec le chemin correct
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/ajouter-vol.fxml"));
            Parent root = loader.load();

            // Création de la scène et affichage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter Vol");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
