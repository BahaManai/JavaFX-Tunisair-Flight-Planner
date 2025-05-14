package com.beginsecure.tunisairaeroplan;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.bootstrapfx.scene.layout.Panel;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Contenu du Panel
        BorderPane panelContent = new BorderPane();
        panelContent.setPadding(new Insets(20));

        Label label = new Label("Bienvenue sur BootstrapFX !");
        label.getStyleClass().addAll("h4", "text-success");

        Button button = new Button("Cliquez ici");
        button.getStyleClass().addAll("btn", "btn-primary");

        VBox centerBox = new VBox(15, label, button);
        panelContent.setCenter(centerBox);

        // Création du Panel
        Panel panel = new Panel("Titre principal");
        panel.getStyleClass().add("panel-success");
        panel.setBody(panelContent);

        // Scène avec le CSS de BootstrapFX
        Scene scene = new Scene(panel, 500, 300);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        primaryStage.setTitle("Test BootstrapFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
