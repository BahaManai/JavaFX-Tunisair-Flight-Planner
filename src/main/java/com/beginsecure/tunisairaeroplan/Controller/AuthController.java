// AuthController.java
package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.Services.AuthService;
import com.beginsecure.tunisairaeroplan.utilites.MainApp;
import com.beginsecure.tunisairaeroplan.utilites.testRegistration;
import com.beginsecure.tunisairaeroplan.utilites.testRegistration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML private VBox rootPane;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button forgotPasswordBtn;
    @FXML private StackPane rootVBox;
    @FXML private Pane particlePane;
    @FXML
    private StackPane contentPane;
    private final List<Stop[]> gradientPhases = List.of(
            new Stop[]{new Stop(0, Color.web("#3498db")), new Stop(1, Color.web("#8e44ad"))},
            new Stop[]{new Stop(0, Color.web("#8e44ad")), new Stop(1, Color.web("#1abc9c"))},
            new Stop[]{new Stop(0, Color.web("#1abc9c")), new Stop(1, Color.web("#f39c12"))},
            new Stop[]{new Stop(0, Color.web("#f39c12")), new Stop(1, Color.web("#3498db"))}
    );

    @FXML
    private Button backToHomeButton;


    @FXML
    private void handleBackToHome() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/Accueil.fxml"));
        Stage stage = (Stage) backToHomeButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private int currentIndex = 0;
    private AuthService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthService();
        setupEventHandlers();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateBackground()),
                new KeyFrame(Duration.seconds(5))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        particlePane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            particlePane.getChildren().clear();
            generateParticles(40);
        });
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        User user = authService.authenticate(email, password);
        if (user != null) {
            try {
                if (user.isAdmin()) {
                    MainApp.showAdminDashboard();
                } else {
                    MainApp.showUserDashboard();
                }
            } catch (IOException e) {
                showError("Erreur lors de la navigation");
                e.printStackTrace();
            }
        } else {
            showError("Email ou mot de passe incorrect, ou compte non approuvé");
        }
    }


    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> {
            try {
                this.showRegistrationForm();
            } catch (Exception e) {
                showError("Erreur lors de l'ouverture du formulaire d'inscription");
                e.printStackTrace();
            }
        });
        forgotPasswordBtn.setOnAction(event -> showError("Fonctionnalité non implémentée"));
    }
    @FXML
    public void showRegistrationForm() {
        try {
            Stage currentStage = (Stage) registerButton.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/beginsecure/tunisairaeroplan/View/RegistrationForm.fxml")
            );

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass().getResource("/com/beginsecure/tunisairaeroplan/View/registration.css").toExternalForm()
            );
            currentStage.setScene(scene);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Erreur critique : " + e.getMessage());
            showError("Impossible d'ouvrir le formulaire");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void updateBackground() {
        Stop[] stops = gradientPhases.get(currentIndex);
        BackgroundFill bgFill = new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops),
                CornerRadii.EMPTY,
                Insets.EMPTY
        );
        rootVBox.setBackground(new Background(bgFill));
        currentIndex = (currentIndex + 1) % gradientPhases.size();
    }

    private void generateParticles(int count) {
        for (int i = 0; i < count; i++) {
            Circle circle = new Circle(Math.random() * 3 + 1, Color.rgb(255, 255, 255, 0.2));
            circle.setLayoutX(Math.random() * particlePane.getWidth());
            circle.setLayoutY(particlePane.getHeight() + Math.random() * 100);

            particlePane.getChildren().add(circle);

            TranslateTransition animation = new TranslateTransition(Duration.seconds(10 + Math.random() * 10), circle);
            animation.setFromY(0);
            animation.setToY(-particlePane.getHeight() - 100);
            animation.setCycleCount(Animation.INDEFINITE);
            animation.setDelay(Duration.seconds(Math.random() * 5));
            animation.play();
        }
    }
}
