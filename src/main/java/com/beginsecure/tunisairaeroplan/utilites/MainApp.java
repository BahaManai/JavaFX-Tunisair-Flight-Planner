package com.beginsecure.tunisairaeroplan.utilites;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainApp.primaryStage = primaryStage;
        primaryStage.setMinWidth(800); // Largeur minimale pour éviter les problèmes de responsivité
        primaryStage.setMinHeight(600); // Hauteur minimale
        showAccueil();
        primaryStage.show();
    }

    public static void showAccueil() throws IOException {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/beginsecure/tunisairaeroplan/view/Accueil.fxml"));
        primaryStage.setTitle("Accueil - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 800, 600));
    }

    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/beginsecure/tunisairaeroplan/view/LoginView.fxml"));
        primaryStage.setTitle("Connexion - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 400, 300));
    }

    public static void showAdminDashboard() throws IOException {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/beginsecure/tunisairaeroplan/view/Main.fxml"));
        primaryStage.setTitle("Tableau de bord Admin - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 1200, 800));
    }

    public static void showUserDashboard() throws IOException {
        Parent root = FXMLLoader.load(MainApp.class.getResource("/com/beginsecure/tunisairaeroplan/view/Home.fxml"));
        primaryStage.setTitle("Tableau de bord Utilisateur - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 1000, 700));
    }

    public static void main(String[] args) {
        launch(args);
    }
}