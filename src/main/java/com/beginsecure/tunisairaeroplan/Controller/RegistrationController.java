package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.Model.LocationData;
import com.beginsecure.tunisairaeroplan.Services.RegistrationService;
import com.beginsecure.tunisairaeroplan.utilites.testRegistration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class RegistrationController {

    @FXML private StackPane stepContentPane, step1Pane, step2Pane, step3Pane, step4Pane;
    @FXML private TextField nomField, prenomField, cinField, matriculeField, emailField, telephoneField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<String> nationaliteCombo, departementCombo, posteCombo, baseAffectationCombo;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Button prevButton, nextButton, backToLoginButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label stepLabel1, stepLabel2, stepLabel3, stepLabel4;
    @FXML private Label confirmNomComplet, confirmDepartement, confirmCin, confirmPoste, confirmEmail;
    @FXML private CheckBox termsCheckBox;

    private RegistrationService registrationService;
    private int currentStep = 1;

    @FXML
    public void initialize() {
        registrationService = new RegistrationService();
        setupSteps();
        loadInitialData();
        setupListeners();
    }

    private void setupSteps() {
        step1Pane.setVisible(true);
        step2Pane.setVisible(false);
        step3Pane.setVisible(false);
        step4Pane.setVisible(false);
        prevButton.setDisable(true);
        updateStepLabels();
    }

    private void loadInitialData() {
        nationaliteCombo.getItems().addAll(LocationData.getCountries());
        departementCombo.getItems().addAll(LocationData.DEPARTEMENTS);
        posteCombo.getItems().addAll(LocationData.POSTES);
    }

    private void setupListeners() {
        nationaliteCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                baseAffectationCombo.getItems().clear();
                if ("Tunisie".equals(newVal)) {
                    baseAffectationCombo.getItems().addAll(LocationData.BASES_AFFECTATION);
                }
            }
        });
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/beginsecure/tunisairaeroplan/View/LoginView.fxml"));
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleNext() {
        if (!validateCurrentStep()) return;
        if (currentStep == 3) {
            handleRegister();
            return;
        }

        currentStep++;
        updateUI();
    }

    @FXML
    private void handlePrev() {
        currentStep--;
        updateUI();
    }

    private void updateUI() {
        step1Pane.setVisible(currentStep == 1);
        step2Pane.setVisible(currentStep == 2);
        step3Pane.setVisible(currentStep == 3);
        step4Pane.setVisible(currentStep == 4);

        prevButton.setDisable(currentStep == 1);
        nextButton.setText(currentStep == 3 ? "S'inscrire" : "Suivant");
        progressBar.setProgress(currentStep / 4.0);
        updateStepLabels();

        if (currentStep == 4) {
            updateConfirmationData();
        }
    }

    private void updateStepLabels() {
        stepLabel1.getStyleClass().remove("active-step");
        stepLabel2.getStyleClass().remove("active-step");
        stepLabel3.getStyleClass().remove("active-step");
        stepLabel4.getStyleClass().remove("active-step");

        if (currentStep == 1) stepLabel1.getStyleClass().add("active-step");
        else if (currentStep == 2) stepLabel2.getStyleClass().add("active-step");
        else if (currentStep == 3) stepLabel3.getStyleClass().add("active-step");
        else if (currentStep == 4) stepLabel4.getStyleClass().add("active-step");
    }

    private void updateConfirmationData() {
        confirmNomComplet.setText(nomField.getText() + " " + prenomField.getText());
        confirmCin.setText(cinField.getText());
        confirmDepartement.setText(departementCombo.getValue());
        confirmPoste.setText(posteCombo.getValue());
        confirmEmail.setText(emailField.getText() + "@tunisair.com");
    }

    private boolean validateCurrentStep() {
        if (currentStep == 1) {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                    cinField.getText().isEmpty() || dateNaissancePicker.getValue() == null ||
                    nationaliteCombo.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
                return false;
            }

            LocalDate birthDate = dateNaissancePicker.getValue();
            LocalDate today = LocalDate.now();
            if (birthDate.isAfter(today.minusYears(20))) {
                showAlert("Erreur", "Vous devez avoir au moins 20 ans pour vous inscrire");
                return false;
            }

            if (!cinField.getText().matches("\\d{8}")) {
                showAlert("Erreur", "Le CIN doit contenir exactement 8 chiffres");
                return false;
            }
        }
        else if (currentStep == 2) {
            if (departementCombo.getValue() == null || posteCombo.getValue() == null ||
                    baseAffectationCombo.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner tous les champs obligatoires");
                return false;
            }
        }
        else if (currentStep == 3) {
            if (emailField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                    confirmPasswordField.getText().isEmpty() || !termsCheckBox.isSelected()) {
                showAlert("Erreur", "Veuillez remplir tous les champs et accepter les conditions");
                return false;
            }

            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("Erreur", "Les mots de passe ne correspondent pas");
                return false;
            }

            if (!isValidPassword(passwordField.getText())) {
                showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères, 1 majuscule, 1 chiffre et 1 caractère spécial");
                return false;
            }

            if (!telephoneField.getText().isEmpty() && !telephoneField.getText().matches("\\d{8}")) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres");
                return false;
            }

            if (!emailField.getText().matches("^[a-zA-Z0-9._%+-]+@tunisair\\.com$")) {
                String fullEmail = emailField.getText() + "@tunisair.com";
                if (!fullEmail.matches("^[a-zA-Z0-9._%+-]+@tunisair\\.com$")) {
                    showAlert("Erreur", "L'email doit être un email professionnel valide (ex: prenom.nom@tunisair.com)");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }
        return hasUppercase && hasDigit && hasSpecialChar;
    }

    @FXML
    private void handleRegister() {
        User user = createUser();
        System.out.println("Attempting to register user: " + user.getEmail() + ", CIN: " + user.getCin());
        if (registrationService.registerUser(user, passwordField.getText())) {
            showAlert("Succès", "Inscription réussie !\nVotre compte sera activé après vérification.");
            try {
                testRegistration.showLoginView();
            } catch (Exception e) {
                System.err.println("Error showing login view: " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Échec de l'inscription. Vérifiez les données saisies (CIN ou email peut-être déjà utilisé).");
        }
    }

    private User createUser() {
        User user = new User();
        user.setNom(nomField.getText().trim());
        user.setPrenom(prenomField.getText().trim());
        user.setCin(cinField.getText().trim());
        user.setMatricule(matriculeField.getText().trim().isEmpty() ? null : matriculeField.getText().trim());
        user.setDateNaissance(dateNaissancePicker.getValue());
        user.setNationalite(nationaliteCombo.getValue());
        user.setDepartement(departementCombo.getValue());
        user.setPoste(posteCombo.getValue());
        user.setAeroport(baseAffectationCombo.getValue());
        user.setBaseAffectation(baseAffectationCombo.getValue());
        user.setEmail(emailField.getText().trim() + "@tunisair.com");
        user.setTelephone(telephoneField.getText().trim().isEmpty() ? null : telephoneField.getText().trim());
        return user;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}