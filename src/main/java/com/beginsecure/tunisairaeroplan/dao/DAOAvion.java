package com.beginsecure.tunisairaeroplan.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DAOAvion {
    private Connection connection;

    public DAOAvion() {
        this.connection = LaConnexion.seConnecter();
    }

    public List<Avion> getAllAvions() {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM Avion";

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

    public boolean addAvion(Avion avion) {
        String query = "INSERT INTO Avion (modele, capacite, estDisponible, type_trajet) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, avion.getModele());
            pstmt.setInt(2, avion.getCapacite());
            pstmt.setBoolean(3, avion.isEstDisponible());
            pstmt.setString(4, avion.getTypeTrajet().name());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAvion(Avion avion) {
        String query = "UPDATE Avion SET modele = ?, capacite = ?, estDisponible = ?, type_trajet = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, avion.getModele());
            pstmt.setInt(2, avion.getCapacite());
            pstmt.setBoolean(3, avion.isEstDisponible());
            pstmt.setString(4, avion.getTypeTrajet().name());
            pstmt.setInt(5, avion.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean archiverAvion(Avion avion) {
        String archiveQuery = "INSERT INTO ArchiveAvion (id, modele, capacite, estDisponible, type_trajet) VALUES (?, ?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM Avion WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            // Archiver
            try (PreparedStatement pstmt = connection.prepareStatement(archiveQuery)) {
                pstmt.setInt(1, avion.getId());
                pstmt.setString(2, avion.getModele());
                pstmt.setInt(3, avion.getCapacite());
                pstmt.setBoolean(4, avion.isEstDisponible());
                pstmt.setString(5, avion.getTypeTrajet().name());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, avion.getId());
                pstmt.executeUpdate();
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
    public List<Avion> getAvionsDisponibles() {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM Avion WHERE estDisponible = true";

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