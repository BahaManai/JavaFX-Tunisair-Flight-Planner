package com.beginsecure.tunisairaeroplan.dao;


import com.beginsecure.tunisairaeroplan.Model.vol;
import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class volDao {

    private Connection connection;

    public volDao(Connection connection) {
        this.connection = connection;
    }

    public void insertVol(vol vol) throws SQLException {
        String sql = "INSERT INTO Vol (numVol, destination, heure_depart, heure_arrivee, type_trajet, statutVol) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, vol.getNumeroVol());
        stmt.setString(2, vol.getDestination());
        stmt.setTimestamp(3, new Timestamp(vol.getHeureDepart().getTime()));
        stmt.setTimestamp(4, new Timestamp(vol.getHeureArrivee().getTime()));
        stmt.setString(5, vol.getTypeTrajet().name());
        stmt.setString(6, vol.getStatut().name());
        stmt.executeUpdate();
    }
    public List<vol> getAllVols() throws SQLException {
        List<vol> vols = new ArrayList<>();
        String sql = "SELECT * FROM Vol";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            vol vol = new vol();
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
    public void deleteVol(int id) throws SQLException {
        String sql = "DELETE FROM Vol WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public void updateVol(vol vol) throws SQLException {
        String sql = "UPDATE Vol SET numVol = ?, destination = ?, heure_depart = ?, heure_arrivee = ?, type_trajet = ?, statutVol = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, vol.getNumeroVol());
        stmt.setString(2, vol.getDestination());
        stmt.setTimestamp(3, new Timestamp(vol.getHeureDepart().getTime()));
        stmt.setTimestamp(4, new Timestamp(vol.getHeureArrivee().getTime()));
        stmt.setString(5, vol.getTypeTrajet().name());
        stmt.setString(6, vol.getStatut().name());
        stmt.setInt(7, vol.getIdVol());
        stmt.executeUpdate();
    }
}
