package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.Model.enums.RoleMembre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EquipageDao {
    private Connection connection;

    public EquipageDao(Connection connection) {
        this.connection = connection;
    }

    public int creerEquipageAvecMembres(String nomEquipage, List<Membre> membres) throws SQLException {
        int equipageId = -1;
        String insertEquipage = "INSERT INTO Equipage (nomEquipage) VALUES (?)";
        String insertLien = "INSERT INTO Equipage_Membre (equipage_id, membre_id) VALUES (?, ?)";

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connexion invalide.");
        }

        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try (PreparedStatement pstEquipage = connection.prepareStatement(insertEquipage, Statement.RETURN_GENERATED_KEYS)) {
            pstEquipage.setString(1, nomEquipage);
            int n = pstEquipage.executeUpdate();
            if (n == 0) throw new SQLException("Échec de l’insertion de l’équipage.");

            try (ResultSet rs = pstEquipage.getGeneratedKeys()) {
                if (rs.next()) {
                    equipageId = rs.getInt(1);
                } else {
                    throw new SQLException("Aucun ID généré pour l’équipage.");
                }
            }

            // Filter out null members
            List<Membre> nonNullMembres = membres.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (nonNullMembres.isEmpty()) {
                throw new SQLException("Aucun membre valide fourni pour l’équipage.");
            }

            try (PreparedStatement pstLien = connection.prepareStatement(insertLien)) {
                for (Membre m : nonNullMembres) {
                    pstLien.setInt(1, equipageId);
                    pstLien.setInt(2, m.getId());
                    pstLien.addBatch();
                }
                pstLien.executeBatch();
            }

            connection.commit();
            return equipageId;
        } catch (SQLException e) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                throw new SQLException("Erreur de rollback : " + ex.getMessage(), ex);
            }
            throw new SQLException("Erreur création équipage : " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    public Equipage getEquipageById(int id) throws SQLException {
        String sql = "SELECT * FROM Equipage WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Equipage(id, rs.getString("nomEquipage"));
            }
            return null;
        }
    }

    public List<Equipage> getAllEquipagesAvecVol() throws SQLException {
        List<Equipage> equipages = new ArrayList<>();
        String sql = """
            SELECT e.id AS equipage_id, e.nomEquipage,
                   v.numVol, v.heure_depart, v.heure_arrivee
            FROM Equipage e
            JOIN Vol v ON v.equipage_id = e.id
        """;

        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Equipage e = new Equipage(
                        rs.getInt("equipage_id"),
                        rs.getString("nomEquipage")
                );
                e.setNumeroVol(rs.getString("numVol"));
                e.setHeureDepart(rs.getTimestamp("heure_depart").toLocalDateTime());
                e.setHeureArrivee(rs.getTimestamp("heure_arrivee").toLocalDateTime());
                equipages.add(e);
            }
            return equipages;
        }
    }

    public List<Membre> getMembresByEquipageId(int equipageId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = """
        SELECT m.* FROM Membre m
        JOIN Equipage_Membre em ON m.id = em.membre_id
        WHERE em.equipage_id = ?
    """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, equipageId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                RoleMembre role = RoleMembre.valueOf(rs.getString("role"));
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        role,
                        rs.getBoolean("estDisponible")
                );
                membres.add(m);
            }
            return membres;
        }
    }

    public void deleteEquipage(int equipageId) throws SQLException {
        String deleteLien = "DELETE FROM Equipage_Membre WHERE equipage_id = ?";
        String deleteEquipage = "DELETE FROM Equipage WHERE id = ?";

        try (PreparedStatement pstLien = connection.prepareStatement(deleteLien);
             PreparedStatement pstEquipage = connection.prepareStatement(deleteEquipage)) {
            connection.setAutoCommit(false);
            pstLien.setInt(1, equipageId);
            pstLien.executeUpdate();

            pstEquipage.setInt(1, equipageId);
            pstEquipage.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                throw new SQLException("Erreur de rollback : " + ex.getMessage(), ex);
            }
            throw new SQLException("Erreur lors de la suppression de l’équipage : " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
}