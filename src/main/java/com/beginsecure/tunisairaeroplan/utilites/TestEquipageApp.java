package com.beginsecure.tunisairaeroplan.utilites;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestEquipageApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/beginsecure/tunisairaeroplan/view/FXMLEquipage.fxml")));
        stage.setScene(scene);
        stage.setTitle("Gestion des Équipages");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
