package com.beginsecure.tunisairaeroplan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestAjouterVolApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Ajouter_vol.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Test Ajout Vol");
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
