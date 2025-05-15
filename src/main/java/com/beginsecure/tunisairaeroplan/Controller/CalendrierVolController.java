package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CalendrierVolController {

    @FXML
    private WebView calendarWebView;

    private WebEngine webEngine;
    private volDao daoVol;
    private Connection connection;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @FXML
    public void initialize() {
        try {
            // Initialiser la connexion et le DAO
            connection = LaConnexion.seConnecter();
            daoVol = new volDao(connection);

            // Configurer WebView
            webEngine = calendarWebView.getEngine();
            String htmlPath = getClass().getResource("/com/beginsecure/tunisairaeroplan/web/calendar.html") != null
                    ? getClass().getResource("/com/beginsecure/tunisairaeroplan/web/calendar.html").toExternalForm()
                    : null;
            if (htmlPath == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement", "Impossible de trouver calendar.html");
                return;
            }
            System.out.println("Loading HTML: " + htmlPath);
            webEngine.load(htmlPath);

            // Log JavaScript errors
            webEngine.setOnError(event -> {
                System.err.println("WebView JavaScript Error: " + event.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erreur WebView", "Erreur JavaScript", event.getMessage());
            });

            // Charger les événements une fois la page prête
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                System.out.println("WebView State: " + newState);
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    loadEvents();
                } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement", "Échec du chargement de calendar.html");
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Connexion", "Impossible de se connecter à la base de données : " + e.getMessage());
        }
    }

    private void loadEvents() {
        try {
            List<vol> vols = daoVol.getAllVols();
            StringBuilder eventsJson = new StringBuilder("[");
            for (int i = 0; i < vols.size(); i++) {
                vol v = vols.get(i);
                String color = getEventColor(v.getStatut());
                String equipageName = daoVol.getNomEquipageParId(v.getEquipageId()); // à créer
                String avionName = daoVol.getNomAvionParId(v.getAvionId());
                String fullTitle = v.getNumVol();
                eventsJson.append("{")
                        .append("\"title\":\"").append(fullTitle).append("\",")
                        .append("\"start\":\"").append(dateFormat.format(v.getHeureDepart())).append("\",")
                        .append("\"end\":\"").append(dateFormat.format(v.getHeureArrivee())).append("\",")
                        .append("\"backgroundColor\":\"").append(color).append("\",")
                        .append("\"extendedProps\": {")
                        .append("\"avion\":\"").append(avionName).append("\",")
                        .append("\"equipage\":\"").append(equipageName).append("\"")
                        .append("}")
                        .append("}");
                if (i < vols.size() - 1) {
                    eventsJson.append(",");
                }
            }
            eventsJson.append("]");
            System.out.println("Events JSON: " + eventsJson);

            // Injecter les événements dans FullCalendar
            Platform.runLater(() -> {
                try {
                    webEngine.executeScript("if (typeof updateEvents === 'function') { updateEvents(" + eventsJson.toString() + "); } else { console.error('updateEvents not defined'); }");
                } catch (Exception e) {
                    System.err.println("Error executing updateEvents: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Injection des événements", "Erreur lors de l'injection des événements : " + e.getMessage());
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement des vols", "Erreur lors du chargement des vols : " + e.getMessage());
        }
    }

    private String getEventColor(StatutVol statut) {
        switch (statut) {
            case Planifié:
                return "#007bff"; // Vert
            case Annulé:
                return "#6c757d"; // Rouge
            case Terminé:
                return "#28a745"; // Gris
            default:
                return "#007bff"; // Bleu par défaut
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    public void finalize() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}