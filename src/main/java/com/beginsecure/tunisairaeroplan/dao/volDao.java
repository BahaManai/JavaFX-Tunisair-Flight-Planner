package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class volDao {

    private final Connection connection;

    public volDao(Connection connection) {
        this.connection = connection;
    }

    public volDao() {
        try {
            this.connection = LaConnexion.seConnecter();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    public void insertVol(vol vol) throws SQLException {
        String sql = "INSERT INTO Vol (numVol, origine, destination, heure_depart, heure_arrivee, type_trajet, statutVol, avion_id, equipage_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vol.getNumVol());
            stmt.setString(2, vol.getOrigine());
            stmt.setString(3, vol.getDestination());
            stmt.setTimestamp(4, new Timestamp(vol.getHeureDepart().getTime()));
            stmt.setTimestamp(5, new Timestamp(vol.getHeureArrivee().getTime()));
            stmt.setString(6, vol.getTypeTrajet().name());
            stmt.setString(7, vol.getStatut().name());
            stmt.setInt(8, vol.getAvion().getId());
            stmt.setInt(9, vol.getEquipage().getId());
            stmt.executeUpdate();
        }
    }

    public int getDernierNumeroVol() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(numVol, 5) AS UNSIGNED)) FROM vol WHERE numVol LIKE 'vol %'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public List<vol> getAllVols() throws SQLException {
        List<vol> vols = new ArrayList<>();
        String sql = "SELECT * FROM Vol";

        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            DAOAvion daoAvion = new DAOAvion(connection);
            EquipageDao daoEquipage = new EquipageDao(connection);

            while (rs.next()) {
                vol v = new vol();
                v.setIdVol(rs.getInt("id"));
                v.setNumeroVol(rs.getString("numVol"));
                v.setOrigine(rs.getString("origine"));
                v.setDestination(rs.getString("destination"));
                v.setHeureDepart(rs.getTimestamp("heure_depart"));
                v.setHeureArrivee(rs.getTimestamp("heure_arrivee"));
                v.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                v.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                StatutVol statut = StatutVol.valueOf(rs.getString("statutVol"));
                LocalDateTime depart = rs.getTimestamp("heure_depart").toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();
                if (statut != StatutVol.Annulé && depart.isBefore(now)) {
                    statut = StatutVol.Terminé;
                }
                v.setStatut(statut);
                int avionId = rs.getInt("avion_id");
                if (avionId > 0) {
                    v.setAvion(daoAvion.getAvionById(avionId));
                }

                int equipageId = rs.getInt("equipage_id");
                if (equipageId > 0) {
                    v.setEquipage(daoEquipage.getEquipageById(equipageId));
                }

                vols.add(v);
            }
        }

        return vols;
    }
    public void updatePastFlightsStatus() throws SQLException {
        String sql = "UPDATE Vol SET statutVol = ? WHERE statutVol != 'Annulé' AND heure_depart < NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, StatutVol.Terminé.name());
            stmt.executeUpdate();
        }
    }
    public boolean archiver(int idVol) throws SQLException {
        vol volToArchive = null;
        int avionId = -1;
        int equipageId = -1;

        String selectQuery = "SELECT * FROM Vol WHERE id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, idVol);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                volToArchive = new vol();
                volToArchive.setIdVol(rs.getInt("id"));
                volToArchive.setNumeroVol(rs.getString("numVol"));
                volToArchive.setDestination(rs.getString("destination"));
                volToArchive.setHeureDepart(rs.getTimestamp("heure_depart"));
                volToArchive.setHeureArrivee(rs.getTimestamp("heure_arrivee"));
                volToArchive.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                volToArchive.setStatut(StatutVol.valueOf(rs.getString("statutVol")));
                avionId = rs.getInt("avion_id");
                equipageId = rs.getInt("equipage_id");
            }
        }

        if (volToArchive == null) return false;

        String insertQuery = "INSERT INTO archiveVol (idVol, numeroVol, destination, heureDepart, heureArrivee, typeTrajet, statut, avion_id, equipage_id, dateArchivage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, volToArchive.getIdVol());
            insertStmt.setString(2, volToArchive.getNumVol());
            insertStmt.setString(3, volToArchive.getDestination());
            insertStmt.setTimestamp(4, new Timestamp(volToArchive.getHeureDepart().getTime()));
            insertStmt.setTimestamp(5, new Timestamp(volToArchive.getHeureArrivee().getTime()));
            insertStmt.setString(6, volToArchive.getTypeTrajet().name());
            insertStmt.setString(7, volToArchive.getStatut().name());
            insertStmt.setInt(8, avionId);
            insertStmt.setInt(9, equipageId);

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                String deleteQuery = "DELETE FROM Vol WHERE id = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, idVol);
                    return deleteStmt.executeUpdate() > 0;
                }
            }
        }

        return false;
    }

    public void updateVol(vol vol) throws SQLException {
        String sql = "UPDATE Vol SET numVol = ?, destination = ?, heure_depart = ?, heure_arrivee = ?, type_trajet = ?, statutVol = ?, avion_id = ?, equipage_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vol.getNumVol());
            stmt.setString(2, vol.getDestination());
            stmt.setTimestamp(3, new Timestamp(vol.getHeureDepart().getTime()));
            stmt.setTimestamp(4, new Timestamp(vol.getHeureArrivee().getTime()));
            stmt.setString(5, vol.getTypeTrajet().name());
            stmt.setString(6, vol.getStatut().name());
            stmt.setInt(7, vol.getAvion().getId());
            stmt.setInt(8, vol.getEquipage().getId());
            stmt.setInt(9, vol.getIdVol());
            stmt.executeUpdate();
        }
    }

    public List<vol> getAllVolsArchives() {
        List<vol> volsArchives = new ArrayList<>();
        String query = "SELECT * FROM archiveVol";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            DAOAvion daoAvion = new DAOAvion(connection);
            EquipageDao daoEquipage = new EquipageDao(connection);
            while (rs.next()) {
                vol v = new vol();
                v.setIdVol(rs.getInt("idVol"));
                v.setNumeroVol(rs.getString("numeroVol"));
                v.setDestination(rs.getString("destination"));
                v.setHeureDepart(rs.getTimestamp("heureDepart"));
                v.setHeureArrivee(rs.getTimestamp("heureArrivee"));
                v.setTypeTrajet(TypeTrajet.valueOf(rs.getString("typeTrajet")));
                v.setStatut(StatutVol.valueOf(rs.getString("statut")));

                // Populate avion and equipage
                int avionId = rs.getInt("avion_id");
                if (avionId > 0) {
                    Avion avion = daoAvion.getAvionById(avionId);
                    v.setAvion(avion);
                }

                int equipageId = rs.getInt("equipage_id");
                if (equipageId > 0) {
                    Equipage equipage = daoEquipage.getEquipageById(equipageId);
                    v.setEquipage(equipage);
                }

                volsArchives.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return volsArchives;
    }
    public boolean isAvionArchived(int avionId) throws SQLException {
        String query = "SELECT COUNT(*) FROM ArchiveAvion WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, avionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if the airplane is archived
            }
            return false; // No record found, airplane is not archived
        }
    }    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public boolean isAvionExists(int avionId) throws SQLException {
        String query = "SELECT COUNT(*) FROM avion WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, avionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if the airplane exists
            }
            return false; // No record found, airplane does not exist
        }
    }
    public void restaurerVol(int idVol) throws SQLException {
        String selectSql = "SELECT * FROM archiveVol WHERE idVol = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setInt(1, idVol);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String insertSql = "INSERT INTO Vol (id, numVol, destination, heure_depart, heure_arrivee, type_trajet, statutVol, avion_id, equipage_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, rs.getInt("idVol"));
                    insertStmt.setString(2, rs.getString("numeroVol"));
                    insertStmt.setString(3, rs.getString("destination"));
                    insertStmt.setTimestamp(4, rs.getTimestamp("heureDepart"));
                    insertStmt.setTimestamp(5, rs.getTimestamp("heureArrivee"));
                    insertStmt.setString(6, rs.getString("typeTrajet"));
                    insertStmt.setString(7, rs.getString("statut"));
                    insertStmt.setInt(8, rs.getInt("avion_id"));
                    insertStmt.setInt(9, rs.getInt("equipage_id"));
                    insertStmt.executeUpdate();
                }

                try (PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM archiveVol WHERE idVol = ?")) {
                    deleteStmt.setInt(1, idVol);
                    deleteStmt.executeUpdate();
                }
            }
        }
    }

    public void supprimerArchive(int idVol) throws SQLException {
        try (PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM archiveVol WHERE idVol = ?")) {
            deleteStmt.setInt(1, idVol);
            deleteStmt.executeUpdate();
        }
    }

    public boolean canAddVolForMembres(List<Integer> membreIds, LocalDateTime heureDepart, LocalDateTime heureArrivee, int volIdToExclude) {
        String sql = "SELECT COUNT(*) FROM vol v " +
                "JOIN equipage_membre em ON v.equipage_id = em.equipage_id " +
                "JOIN membre m ON em.membre_id = m.id " +
                "WHERE em.membre_id IN (" + String.join(",", Collections.nCopies(membreIds.size(), "?")) + ") " +
                "AND v.id != ? " + // Exclure le vol en cours de modification
                "AND v.statutVol != 'Annulé' " +
                "AND m.estDisponible = TRUE " +
                "AND (? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR ? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR v.heure_depart BETWEEN ? AND ? " +
                "     OR v.heure_arrivee BETWEEN ? AND ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set the member IDs
            int index = 1;
            for (Integer membreId : membreIds) {
                stmt.setInt(index++, membreId);
            }
            // Set the volIdToExclude
            stmt.setInt(index++, volIdToExclude);
            // Set the timestamps
            stmt.setTimestamp(index++, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(index++, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(index++, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(index++, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(index++, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(index, Timestamp.valueOf(heureArrivee));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Returns true if no conflicts
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Deprecated method (kept for backward compatibility, but should be phased out)
    @Deprecated
    public boolean canAddVolForMembres(int equipageId, LocalDateTime heureDepart, LocalDateTime heureArrivee) {
        String sql = "SELECT COUNT(*) FROM vol v " +
                "JOIN equipage_membre em ON v.equipage_id = em.equipage_id " +
                "JOIN membre m ON em.membre_id = m.id " +
                "WHERE em.equipage_id = ? AND v.statutVol != 'Annulé' " +
                "AND m.estDisponible = TRUE " +
                "AND (? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR ? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR v.heure_depart BETWEEN ? AND ? " +
                "     OR v.heure_arrivee BETWEEN ? AND ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipageId);
            stmt.setTimestamp(2, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(3, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(4, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(5, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(6, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(7, Timestamp.valueOf(heureArrivee));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Returns true if no conflicts
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean canAddVolForAvion(int avionId, LocalDateTime heureDepart, LocalDateTime heureArrivee) {
        String sql = "SELECT COUNT(*) FROM Vol v " +
                "JOIN Avion a ON v.avion_id = a.id " +
                "WHERE v.avion_id = ? AND v.statutVol != 'Annulé' " +
                "AND a.estDisponible = TRUE " +
                "AND (? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR ? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR v.heure_depart BETWEEN ? AND ? " +
                "     OR v.heure_arrivee BETWEEN ? AND ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, avionId);
            stmt.setTimestamp(2, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(3, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(4, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(5, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(6, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(7, Timestamp.valueOf(heureArrivee));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Returns true if no conflicts
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean canAddVolForAvion(int avionId, LocalDateTime heureDepart, LocalDateTime heureArrivee, int volIdToExclude) {
        String sql = "SELECT COUNT(*) FROM Vol v " +
                "JOIN Avion a ON v.avion_id = a.id " +
                "WHERE v.avion_id = ? AND v.id != ? AND v.statutVol != 'Annulé' " +
                "AND a.estDisponible = TRUE " +
                "AND (? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR ? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "     OR v.heure_depart BETWEEN ? AND ? " +
                "     OR v.heure_arrivee BETWEEN ? AND ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, avionId);
            stmt.setInt(2, volIdToExclude);
            stmt.setTimestamp(3, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(4, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(5, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(6, Timestamp.valueOf(heureArrivee));
            stmt.setTimestamp(7, Timestamp.valueOf(heureDepart));
            stmt.setTimestamp(8, Timestamp.valueOf(heureArrivee));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Returns true if no conflicts
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getNomEquipageParId(int id) throws SQLException {
        String sql = "SELECT nomEquipage FROM equipage WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("nomEquipage");
            }
        }
        return "Équipage inconnu";
    }

    public String getNomAvionParId(int id) throws SQLException {
        String sql = "SELECT marque, modele FROM avion WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("marque") + " " + rs.getString("modele");
            }
        }
        return "Avion inconnu";
    }

    public List<vol> getVolsByWeek(int weekOfYear, int year) throws SQLException {
        List<vol> vols = new ArrayList<>();
        // Calculate the start and end of the week
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Start of the week (Monday)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp startOfWeek = new Timestamp(cal.getTimeInMillis());

        cal.add(Calendar.DAY_OF_WEEK, 6); // End of the week (Sunday)
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Timestamp endOfWeek = new Timestamp(cal.getTimeInMillis());

        String sql = "SELECT * FROM vol WHERE heure_depart BETWEEN ? AND ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setTimestamp(1, startOfWeek);
            pst.setTimestamp(2, endOfWeek);
            ResultSet rs = pst.executeQuery();
            DAOAvion daoAvion = new DAOAvion(connection);
            EquipageDao daoEquipage = new EquipageDao(connection);
            while (rs.next()) {
                vol v = new vol();
                v.setIdVol(rs.getInt("id"));
                v.setNumeroVol(rs.getString("numVol"));
                v.setOrigine(rs.getString("origine"));
                v.setDestination(rs.getString("destination"));
                v.setHeureDepart(rs.getTimestamp("heure_depart"));
                v.setHeureArrivee(rs.getTimestamp("heure_arrivee"));
                v.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                v.setStatut(StatutVol.valueOf(rs.getString("statutVol")));

                int avionId = rs.getInt("avion_id");
                v.setAvion(avionId > 0 ? daoAvion.getAvionById(avionId) : null);

                int equipageId = rs.getInt("equipage_id");
                v.setEquipage(equipageId > 0 ? daoEquipage.getEquipageById(equipageId) : null);

                vols.add(v);
            }
        }
        return vols;
    }

    public List<Map<String, Object>> getTopDestinations(int limit) throws SQLException {
        String sql = "SELECT destination, COUNT(*) as count FROM vol GROUP BY destination ORDER BY count DESC LIMIT ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(Map.of("destination", rs.getString("destination"), "count", rs.getInt("count")));
            }
        }
        return result;
    }

    public List<Map<String, Object>> getVolsByStatus() throws SQLException {
        String sql = "SELECT statutVol as status, COUNT(*) as count FROM vol GROUP BY statutVol";
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(Map.of("status", rs.getString("status"), "count", rs.getInt("count")));
            }
        }
        return result;
    }

    public int getTotalVols() throws SQLException {
        String sql = "SELECT COUNT(*) FROM vol";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getVolsAnnules() throws SQLException {
        String sql = "SELECT COUNT(*) FROM vol WHERE statutVol = 'Annulé'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTauxOccupationMoyen() throws SQLException {
        // Placeholder : À adapter selon votre modèle de données (ex. table de réservations)
        return 75.0; // Valeur fictive pour l'exemple
    }

    public Map<StatutVol, Integer> countVolsByStatut() throws SQLException {
        Map<StatutVol, Integer> counts = new HashMap<>();
        String sql = "SELECT statutVol, COUNT(*) as total FROM vol GROUP BY statutVol";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String statutStr = rs.getString("statutVol");
                int count = rs.getInt("total");
                StatutVol statut = StatutVol.valueOf(statutStr);
                counts.put(statut, count);
            }
        }

        // Assurer que tous les statuts soient présents avec 0 si manquants
        for (StatutVol statut : StatutVol.values()) {
            counts.putIfAbsent(statut, 0);
        }

        return counts;
    }

    public int getVolsThisWeek() throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        java.sql.Date startOfWeek = new java.sql.Date(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        java.sql.Date endOfWeek = new java.sql.Date(cal.getTimeInMillis());

        String sql = "SELECT COUNT(*) FROM Vol WHERE heure_depart BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startOfWeek);
            stmt.setDate(2, endOfWeek);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<vol> getPendingFlights() throws SQLException {
        List<vol> pendingFlights = new ArrayList<>();
        String query = "SELECT * FROM Vol WHERE statutVol = 'En_attente'";
        try (Connection conn = LaConnexion.seConnecter();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vol flight = new vol();
                flight.setIdVol(rs.getInt("id"));
                flight.setNumeroVol(rs.getString("numVol"));
                flight.setOrigine(rs.getString("origine")); // Ensure the column exists in the database
                flight.setDestination(rs.getString("destination"));
                flight.setHeureDepart(rs.getTimestamp("heure_depart"));
                flight.setHeureArrivee(rs.getTimestamp("heure_arrivee"));
                flight.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                flight.setStatut(StatutVol.valueOf(rs.getString("statutVol")));
                // Load avion and equipage if needed
                int avionId = rs.getInt("avion_id");
                if (!rs.wasNull()) {
                    DAOAvion daoAvion = new DAOAvion(conn);
                    Avion avion = daoAvion.getAvionById(avionId);
                    flight.setAvion(avion);
                }
                int equipageId = rs.getInt("equipage_id");
                if (!rs.wasNull()) {
                    EquipageDao equipageDao = new EquipageDao(conn);
                    Equipage equipage = equipageDao.getEquipageById(equipageId);
                    flight.setEquipage(equipage);
                }
                pendingFlights.add(flight);
            }
        }
        return pendingFlights;
    }

    public void deleteVol(int volId) throws SQLException {
        String query = "DELETE FROM Vol WHERE id = ?";
        try (Connection conn = LaConnexion.seConnecter();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, volId);
            stmt.executeUpdate();
        }
    }
}