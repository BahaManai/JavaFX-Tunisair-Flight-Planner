package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.sql.*;
        import java.util.ArrayList;
import java.util.List;

public class ArchiveAvionDao {
    private Connection connection;
    public boolean supprimerDefinitivement(int idAvion) {
        String query = "DELETE FROM ArchiveAvion WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idAvion);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restaurerAvion(Avion avion) {
        String insertQuery = "INSERT INTO Avion (id, modele, capacite, estDisponible, type_trajet) VALUES (?, ?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM ArchiveAvion WHERE id = ?";

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, avion.getId());
                insertStmt.setString(2, avion.getModele());
                insertStmt.setInt(3, avion.getCapacite());
                insertStmt.setBoolean(4, avion.isEstDisponible());
                insertStmt.setString(5, avion.getTypeTrajet().name());
                insertStmt.executeUpdate();
            }

            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, avion.getId());
                deleteStmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public ArchiveAvionDao() {
        this.connection = LaConnexion.seConnecter();
    }

    public List<Avion> getAllArchivedAvions() {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM ArchiveAvion ORDER BY date_archivage DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setModele(rs.getString("modele"));
                avion.setCapacite(rs.getInt("capacite"));
                avion.setEstDisponible(rs.getBoolean("estDisponible"));
                avion.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                avions.add(avion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }
}