package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Avion;
import com.beginsecure.tunisairaeroplan.Model.vol;
import java.sql.*;

public class ArchivVolDao {

    private Connection connection;
    public void restaurerVol(int idVol) throws SQLException {
        String selectSql = "SELECT * FROM archiveVol WHERE idVol = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setInt(1, idVol);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String insertSql = "INSERT INTO Vol (id, numVol, destination, heure_depart, heure_arrivee, type_trajet, statutVol, avion_id, equipage_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

                String deleteSql = "DELETE FROM archiveVol WHERE idVol = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, idVol);
                    deleteStmt.executeUpdate();
                }
            }
        }
    }


    public void supprimerArchive(int idVol) throws SQLException {
        String deleteSql = "DELETE FROM archiveVol WHERE idVol = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, idVol);
            deleteStmt.executeUpdate();
        }
    }
    public boolean archiverAvion(Avion avion) {
        String archiveQuery = "INSERT INTO ArchiveAvion (id, modele, capacite, estDisponible, type_trajet) VALUES (?, ?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM Avion WHERE id = ?";

        try {
            connection.setAutoCommit(false); // Démarrer une transaction

            try (PreparedStatement archiveStmt = connection.prepareStatement(archiveQuery)) {
                archiveStmt.setInt(1, avion.getId());
                archiveStmt.setString(2, avion.getModele());
                archiveStmt.setInt(3, avion.getCapacite());
                archiveStmt.setBoolean(4, avion.isEstDisponible());
                archiveStmt.setString(5, avion.getTypeTrajet().name());
                archiveStmt.executeUpdate();
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
    public ArchivVolDao(Connection connection) {
        this.connection = connection;
    }
    public void archiverVol(vol vol) throws SQLException {
        String sql = "INSERT INTO archiveVol (idVol, numeroVol, destination, heureDepart, heureArrivee, typeTrajet, statut, avion_id, equipage_id, dateArchivage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vol.getIdVol());
            stmt.setString(2, vol.getNumVol());
            stmt.setString(3, vol.getDestination());
            stmt.setTimestamp(4, new Timestamp(vol.getHeureDepart().getTime()));
            stmt.setTimestamp(5, new Timestamp(vol.getHeureArrivee().getTime()));
            stmt.setString(6, vol.getTypeTrajet().name());
            stmt.setString(7, vol.getStatut().name());
            stmt.setInt(8, vol.getAvionId());
            stmt.setInt(9, vol.getEquipageId());
            stmt.executeUpdate();
        }

        String deleteSql = "DELETE FROM Vol WHERE id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, vol.getIdVol());
            deleteStmt.executeUpdate();
        }
    }

}
