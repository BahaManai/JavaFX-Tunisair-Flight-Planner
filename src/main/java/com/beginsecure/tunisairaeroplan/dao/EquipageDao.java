package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.Model.enums.RoleMembre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipageDao {
    private Connection connection;

    public EquipageDao(Connection connection) {
        this.connection = connection;
    }

    public int creerEquipageAvecMembres(String nomEquipage, List<Membre> membres) {
        int equipageId = -1;
        String insertEquipage = "INSERT INTO Equipage (nomEquipage) VALUES (?)";
        String insertLien = "INSERT INTO Equipage_Membre (equipage_id, membre_id) VALUES (?, ?)";

        try {
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
            }

            try (PreparedStatement pstLien = connection.prepareStatement(insertLien)) {
                for (Membre m : membres) {
                    pstLien.setInt(1, equipageId);
                    pstLien.setInt(2, m.getId());
                    pstLien.addBatch();
                }
                pstLien.executeBatch();
            }

            connection.commit();
            connection.setAutoCommit(autoCommit);

        } catch (SQLException e) {
            try {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Erreur de rollback : " + ex.getMessage());
            }
            System.out.println("Erreur création équipage : " + e.getMessage());
            equipageId = -1;
        }

        return equipageId;
    }

    public Equipage getEquipageById(int id) {
        String sql = "SELECT * FROM Equipage WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Equipage(id, rs.getString("nomEquipage"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur getEquipageById : " + e.getMessage());
        }
        return null;
    }

    public List<Equipage> getAllEquipagesAvecVol() {
        List<Equipage> equipages = new ArrayList<>();
        String sql = """
        SELECT e.id AS equipage_id, e.nomEquipage,
               v.numVol, v.heure_depart, v.heure_arrivee
        FROM equipage e
        JOIN vol v ON v.equipage_id = e.id
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

        } catch (SQLException e) {
            System.out.println("Erreur getAllEquipagesAvecVol : " + e.getMessage());
        }

        return equipages;
    }

    public List<Membre> getMembresByEquipageId(int equipageId) {
        List<Membre> membres = new ArrayList<>();
        String sql = """
        SELECT m.* FROM membre m
        JOIN equipage_membre em ON m.id = em.membre_id
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

        } catch (SQLException e) {
            System.out.println("Erreur getMembresByEquipageId : " + e.getMessage());
        }

        return membres;
    }

}
