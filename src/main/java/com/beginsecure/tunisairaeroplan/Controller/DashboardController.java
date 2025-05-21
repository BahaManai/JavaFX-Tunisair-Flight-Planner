package com.beginsecure.tunisairaeroplan.Controller;

import com.beginsecure.tunisairaeroplan.dao.DAOAvion;
import com.beginsecure.tunisairaeroplan.dao.membreDao;
import com.beginsecure.tunisairaeroplan.dao.volDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML
    private WebView webView;

    private WebEngine webEngine;
    private Connection connection;
    private volDao dao;
    private membreDao membreDao;
    private DAOAvion avionDao;

    @FXML
    public void initialize() {
        try {
            connection = LaConnexion.seConnecter();
            dao = new volDao(connection);
            membreDao = new membreDao(connection);
            avionDao = new DAOAvion(connection);
            webEngine = webView.getEngine();

            String url = getClass().getResource("/com/beginsecure/tunisairaeroplan/web/dashboard.html").toExternalForm();
            webEngine.load(url);

            webEngine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
                if (state == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("java", new JavaBridge());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class JavaBridge {
        public void injectData() {
            Platform.runLater(() -> {
                try {
                    List<Map<String, Object>> destinations = dao.getTopDestinations(5);
                    List<Map<String, Object>> statuses = dao.getVolsByStatus();
                    List<Map<String, Object>> crewRoles = membreDao.getCrewRolesDistribution();
                    List<Map<String, Object>> avionsByMarque = avionDao.getAvionsByMarque();
                    int volsThisWeek = dao.getVolsThisWeek();
                    int totalVols = dao.getTotalVols();
                    int annules = dao.getVolsAnnules();
                    double annulesPercentage = (totalVols > 0) ? (annules * 100.0 / totalVols) : 0.0;
                    int availableAvions = avionDao.getAvailableAvions();
                    int totalAvions = avionDao.getTotalAvions();
                    int availableMembres = membreDao.getAvailableMembres();
                    int totalMembres = membreDao.getTotalMembres();

                    String destJS = toJSArray(destinations, "destination");
                    String statusJS = toJSArray(statuses, "status");
                    String crewRolesJS = toJSArray(crewRoles, "role");
                    String avionsJS = toJSArray(avionsByMarque, "marque");

                    String jsCommand = String.format(
                            "updateCharts(%s, %s, %s, %s, {volsThisWeek: %d, annulesPercentage: %.2f, availableAvions: %d, totalAvions: %d, availableMembres: %d, totalMembres: %d})",
                            destJS, statusJS, crewRolesJS, avionsJS, volsThisWeek, annulesPercentage, availableAvions, totalAvions, availableMembres, totalMembres
                    );

                    webEngine.executeScript(jsCommand);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        private String toJSArray(List<Map<String, Object>> list, String labelKey) {
            if (list == null || list.isEmpty()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> item = list.get(i);
                String label = String.valueOf(item.getOrDefault(labelKey, "")).replace("\"", "\\\"");
                int count = item.containsKey("count") ? ((Number) item.get("count")).intValue() : 0;
                sb.append(String.format("{%s:\"%s\", count:%d}", labelKey, label, count));
                if (i < list.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
}