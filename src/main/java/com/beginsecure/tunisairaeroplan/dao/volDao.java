package com.beginsecure.tunisairaeroplan.dao;


import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class volDao{

    private Connection connection;

    public volDao(Connection connection) {
        this.connection = connection;
    }

    public void insertVol(vol vol) throws SQLException {
        String sql = "INSERT INTO Vol (numVol, destination, heure_depart, heure_arrivee, type_trajet, statutVol) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, vol.getNumVol());
        stmt.setString(2, vol.getDestination());
        stmt.setTimestamp(3, new Timestamp(vol.getHeureDepart().getTime()));
        stmt.setTimestamp(4, new Timestamp(vol.getHeureArrivee().getTime()));
        stmt.setString(5, vol.getTypeTrajet().name());
        stmt.setString(6, vol.getStatut().name());
        stmt.executeUpdate();
    }
    public List<com.beginsecure.tunisairaeroplan.Model.vol> getAllVols() throws SQLException {
        List<com.beginsecure.tunisairaeroplan.Model.vol> vols = new ArrayList<>();
        String sql = "SELECT * FROM Vol";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            com.beginsecure.tunisairaeroplan.Model.vol vol = new com.beginsecure.tunisairaeroplan.Model.vol();
            vol.setIdVol(rs.getInt("id"));
            vol.setNumeroVol(rs.getString("numVol"));
            vol.setDestination(rs.getString("destination"));
            vol.setHeureDepart(rs.getTimestamp("heure_depart"));
            vol.setHeureArrivee(rs.getTimestamp("heure_arrivee"));
            vol.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
            vol.setStatut(StatutVol.valueOf(rs.getString("statutVol")));

            vols.add(vol);
        }
        return vols;
    }
    public boolean archiver(int idVol) throws SQLException {
        String selectQuery = "SELECT * FROM Vol WHERE id = ?";
        vol volToArchive = null;

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, idVol);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                volToArchive = new vol();
                volToArchive.setIdVol(rs.getInt("id"));
                volToArchive.setNumeroVol (rs.getString("numVol"));
                volToArchive.setDestination(rs.getString("destination"));
                volToArchive.setHeureDepart(rs.getTimestamp("heure_depart"));
                volToArchive.setHeureArrivee(rs.getTimestamp("heure_arrivee"));
                volToArchive.setTypeTrajet(TypeTrajet.valueOf(rs.getString("type_trajet")));
                volToArchive.setStatut(StatutVol.valueOf(rs.getString("statutVol")));
            }
        }

        if (volToArchive == null) {
            return false;
        }
        String insertQuery = "INSERT INTO archiveVol (idVol, numeroVol, destination, heureDepart, heureArrivee, typeTrajet, statut, dateArchivage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, volToArchive.getIdVol());
            insertStmt.setString(2, volToArchive.getNumVol());
            insertStmt.setString(3, volToArchive.getDestination());
            insertStmt.setTimestamp(4, new Timestamp(volToArchive.getHeureDepart().getTime()));
            insertStmt.setTimestamp(5, new Timestamp(volToArchive.getHeureArrivee().getTime()));
            insertStmt.setString(6, volToArchive.getTypeTrajet().toString());
            insertStmt.setString(7, volToArchive.getStatut().toString());

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
        String sql = "UPDATE Vol SET numVol = ?, destination = ?, heure_depart = ?, heure_arrivee = ?, type_trajet = ?, statutVol = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, vol.getNumVol());
        stmt.setString(2, vol.getDestination());
        stmt.setTimestamp(3, new Timestamp(vol.getHeureDepart().getTime()));
        stmt.setTimestamp(4, new Timestamp(vol.getHeureArrivee().getTime()));
        stmt.setString(5, vol.getTypeTrajet().name());
        stmt.setString(6, vol.getStatut().name());
        stmt.setInt(7, vol.getIdVol());
        stmt.executeUpdate();
    }
    public List<vol> getAllVolsArchives() {
        List<vol> volsArchives = new ArrayList<>();
        String query = "SELECT * FROM archiveVol";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vol v = new vol();
                v.setIdVol(rs.getInt("idVol"));
                v.setNumeroVol(rs.getString("numeroVol"));
                v.setDestination(rs.getString("destination"));
                v.setHeureDepart(rs.getDate("heureDepart"));
                v.setHeureArrivee(rs.getDate("heureArrivee"));
                v.setTypeTrajet(TypeTrajet.valueOf(rs.getString("typeTrajet"))); // Assumes enum
                v.setStatut(StatutVol.valueOf(rs.getString("statut"))); // Assumes enum
                volsArchives.add(v);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return volsArchives;
    }
    public void restaurerVol(int idVol) throws SQLException {
        String selectSql = "SELECT * FROM archiveVol WHERE idVol = ?";
        PreparedStatement selectStmt = connection.prepareStatement(selectSql);
        selectStmt.setInt(1, idVol);
        ResultSet rs = selectStmt.executeQuery();
        if (rs.next()) {
            String insertSql = "INSERT INTO Vol (id, numeroVol, destination, heureDepart, heureArrivee, typeTrajet, statut) " +
                    "SELECT id, numeroVol, destination, heureDepart, heureArrivee, typeTrajet, statut FROM archive WHERE idVol = ?";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setInt(1, idVol);
            insertStmt.executeUpdate();
            String deleteSql = "DELETE FROM archiveVol WHERE idVol = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
            deleteStmt.setInt(1, idVol);
            deleteStmt.executeUpdate();
        }
    }
    public void supprimerArchive(int idVol) throws SQLException {
        String deleteSql = "DELETE FROM archiveVol WHERE idVol = ?";
        PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
        deleteStmt.setInt(1, idVol);
        deleteStmt.executeUpdate();
    }


}