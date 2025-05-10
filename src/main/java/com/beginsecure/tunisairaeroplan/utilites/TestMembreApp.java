package com.beginsecure.tunisairaeroplan.utilites;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class TestMembreApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/FXMLMembre.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion des membres");
        stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
