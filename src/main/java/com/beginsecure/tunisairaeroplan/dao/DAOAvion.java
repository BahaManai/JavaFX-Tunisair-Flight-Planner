package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DAOAvion {
    private Connection connection;

    public DAOAvion(Connection connection) {
        this.connection = connection;
    }

    public DAOAvion() {
        try {
            this.connection = LaConnexion.seConnecter();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données dans DAOAvion", e);
        }
    }


    public List<Avion> getAllAvions() {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM Avion";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setMarque(rs.getString("marque"));
                avion.setModele(rs.getString("modele"));
                avion.setCapacite(rs.getInt("capacite"));
                avion.setEstDisponible(rs.getBoolean("estDisponible"));
                avions.add(avion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }

    public boolean addAvion(Avion avion) {
        String query = "INSERT INTO Avion (modele, capacite, estDisponible, marque) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, avion.getModele());
            pstmt.setInt(2, avion.getCapacite());
            pstmt.setBoolean(3, avion.isEstDisponible());
            pstmt.setString(4, avion.getMarque());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAvion(Avion avion) {
        String query = "UPDATE Avion SET modele = ?, capacite = ?, estDisponible = ?,  marque = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, avion.getModele());
            pstmt.setInt(2, avion.getCapacite());
            pstmt.setBoolean(3, avion.isEstDisponible());
            pstmt.setString(4, avion.getMarque());
            pstmt.setInt(5, avion.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> archiverAvion(Avion avion) {
        String archiveQuery = "INSERT INTO ArchiveAvion (id, modele, capacite, estDisponible, marque) VALUES (?, ?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM Avion WHERE id = ?";
        String selectVolsQuery = "SELECT id FROM Vol WHERE avion_id = ?";
        List<Integer> archivedFlightIds = new ArrayList<>();

        try {
            connection.setAutoCommit(false);
            volDao volDao = new volDao(connection);

            // Collect IDs of all flights to be archived
            try (PreparedStatement selectStmt = connection.prepareStatement(selectVolsQuery)) {
                selectStmt.setInt(1, avion.getId());
                ResultSet rs = selectStmt.executeQuery();
                while (rs.next()) {
                    int volId = rs.getInt("id");
                    if (volDao.archiver(volId)) {
                        archivedFlightIds.add(volId);
                    }
                }
            }

            // Archive the airplane
            try (PreparedStatement pstmt = connection.prepareStatement(archiveQuery)) {
                pstmt.setInt(1, avion.getId());
                pstmt.setString(2, avion.getModele());
                pstmt.setInt(3, avion.getCapacite());
                pstmt.setBoolean(4, avion.isEstDisponible());
                pstmt.setString(5, avion.getMarque());
                pstmt.executeUpdate();
            }

            // Delete the airplane
            try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, avion.getId());
                pstmt.executeUpdate();
            }

            connection.commit();
            return archivedFlightIds;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return archivedFlightIds; // Return empty or partial list in case of failure
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }    public List<Avion> getAvionsDisponibles() {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM Avion WHERE estDisponible = true";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setMarque(rs.getString("marque"));
                avion.setModele(rs.getString("modele"));
                avion.setCapacite(rs.getInt("capacite"));
                avion.setEstDisponible(rs.getBoolean("estDisponible"));
                avions.add(avion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }

    public Avion getAvionById(int id) {
        String sql = "SELECT * FROM Avion WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Avion a = new Avion();
                a.setId(rs.getInt("id"));
                a.setModele(rs.getString("modele"));
                a.setMarque(rs.getString("marque"));
                a.setCapacite(rs.getInt("capacite"));
                a.setEstDisponible(rs.getBoolean("estDisponible"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Avion> getAvionsDisponiblesPourPeriode(LocalDateTime debut, LocalDateTime fin) {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT a.* FROM Avion a " +
                "WHERE a.estDisponible = TRUE " +
                "AND a.id NOT IN (" +
                "    SELECT v.avion_id FROM Vol v " +
                "    WHERE v.statutVol != 'Annulé' " +
                "    AND (? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "         OR ? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "         OR v.heure_depart BETWEEN ? AND ? " +
                "         OR v.heure_arrivee BETWEEN ? AND ?)" +
                ")";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(debut));
            pstmt.setTimestamp(2, Timestamp.valueOf(fin));
            pstmt.setTimestamp(3, Timestamp.valueOf(debut));
            pstmt.setTimestamp(4, Timestamp.valueOf(fin));
            pstmt.setTimestamp(5, Timestamp.valueOf(debut));
            pstmt.setTimestamp(6, Timestamp.valueOf(fin));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setMarque(rs.getString("marque"));
                avion.setModele(rs.getString("modele"));
                avion.setCapacite(rs.getInt("capacite"));
                avion.setEstDisponible(rs.getBoolean("estDisponible"));
                avions.add(avion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }
    public int getTotalAvions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Avion";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Récupère le nombre d'avions par marque
    public List<Map<String, Object>> getAvionsByMarque() throws SQLException {
        String sql = "SELECT marque, COUNT(*) as count FROM Avion GROUP BY marque";
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(Map.of("marque", rs.getString("marque"), "count", rs.getInt("count")));
            }
        }
        return result;
    }

    public int getAvailableAvions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Avion WHERE estDisponible = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
