package com.beginsecure.tunisairaeroplan.utilites;
import com.beginsecure.tunisairaeroplan.dao.UserDao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class testRegistration extends Application {
    private static Stage primaryStage;
    private UserDao userDao;

    @Override
    public void start(Stage primaryStage) throws Exception {
        testRegistration.primaryStage = primaryStage;
       Connection testConn = LaConnexion.seConnecter();
        userDao = new UserDao();

            new UserDao().createAdminIfNotExists();

        userDao.createAdminIfNotExists();
        showLoginView();
        primaryStage.show();
    }
    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(testRegistration.class.getResource("/com/beginsecure/tunisairaeroplan/view/LoginView.fxml"));
        primaryStage.setTitle("Connexion - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 400, 300));
    }

    public static void showRegistrationForm() throws IOException {
        Parent root = FXMLLoader.load(testRegistration.class.getResource("/com/beginsecure/tunisairaeroplan/view/RegistrationForm.fxml"));
        primaryStage.setTitle("Inscription - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 600, 700));
    }

    public static void showAdminDashboard() throws IOException {
        Parent root = FXMLLoader.load(testRegistration.class.getResource("/com/beginsecure/tunisairaeroplan/view/Main.fxml"));
        primaryStage.setTitle("Tableau de bord Admin - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 800, 600));
    }

    public static void showUserDashboard() throws IOException {
    }
    public static void showMainView() throws IOException {
        Parent root = FXMLLoader.load(testRegistration.class.getResource("/com/beginsecure/tunisairaeroplan/view/Main.fxml"));
        primaryStage.setTitle("Tableau de bord - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 800, 600));
    }
    public static void showPendingApprovals() throws IOException {
        Parent root = FXMLLoader.load(testRegistration.class.getResource("com/beginsecure/tunisairaeroplan/view/Main.fxml"));
        primaryStage.setTitle("Approbations en attente - Tunisair Aeroplan");
        primaryStage.setScene(new Scene(root, 800, 600));
    }

    public static void main(String[] args) {
        launch(args);
    }
}