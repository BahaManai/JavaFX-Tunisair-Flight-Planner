package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.Model.enums.RoleMembre;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class membreDao {
    private Connection connection;

    public membreDao(Connection connection) {
        this.connection = connection;
    }

    public int ajouter(Membre m) throws SQLException {
        String requete = "INSERT INTO membre (cin, nom, prenom, role, estDisponible) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, m.getCin());
            pst.setString(2, m.getNom());
            pst.setString(3, m.getPrenom());
            pst.setString(4, m.getRole().name());
            pst.setBoolean(5, m.isDisponible());
            int n = pst.executeUpdate();
            if (n >= 1) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int idGenere = rs.getInt(1);
                    m.setId(idGenere);
                    return idGenere;
                }
            }
            throw new SQLException("Ajout du membre réussi, mais impossible de récupérer l'ID généré.");
        } catch (SQLException ex) {
            throw new SQLException("Problème d'ajout du membre : " + ex.getMessage(), ex);
        }
    }

    public ArrayList<Membre> lister() throws SQLException {
        ArrayList<Membre> membres = new ArrayList<>();
        String requete = "SELECT * FROM membre";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(requete)) {
            while (rs.next()) {
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        RoleMembre.valueOf(rs.getString("role")),
                        rs.getBoolean("estDisponible")
                );
                membres.add(m);
            }
            System.out.println("Liste des membres récupérée");
        } catch (SQLException ex) {
            throw new SQLException("Erreur lors du listing des membres : " + ex.getMessage(), ex);
        }
        return membres;
    }

    public boolean modifier(Membre m) throws SQLException {
        String requete = "UPDATE membre SET cin = ?, nom = ?, prenom = ?, role = ?, estDisponible = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(requete)) {
            pst.setString(1, m.getCin());
            pst.setString(2, m.getNom());
            pst.setString(3, m.getPrenom());
            pst.setString(4, m.getRole().name());
            pst.setBoolean(5, m.isDisponible());
            pst.setInt(6, m.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Membre modifié !");
                return true;
            }
            return false;
        } catch (SQLException ex) {
            throw new SQLException("Problème de modification : " + ex.getMessage(), ex);
        }
    }

    public boolean supprimer(Membre m) throws SQLException {
        String requete = "DELETE FROM membre WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(requete)) {
            pst.setInt(1, m.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Membre supprimé !");
                return true;
            }
            return false;
        } catch (SQLException ex) {
            throw new SQLException("Problème de suppression : " + ex.getMessage(), ex);
        }
    }

    public ArrayList<Membre> getMembresDisponiblesParRole(RoleMembre role) throws SQLException {
        ArrayList<Membre> membres = new ArrayList<>();
        String requete = "SELECT * FROM membre WHERE estDisponible = TRUE AND role = ?";
        try (PreparedStatement pst = connection.prepareStatement(requete)) {
            pst.setString(1, role.name());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        RoleMembre.valueOf(rs.getString("role")),
                        rs.getBoolean("estDisponible")
                );
                membres.add(m);
            }
        } catch (SQLException ex) {
            throw new SQLException("Erreur lors de la récupération des membres par rôle : " + ex.getMessage(), ex);
        }
        return membres;
    }

    public List<Membre> getMembresDisponiblesParRolePourPeriode(RoleMembre role, LocalDateTime debut, LocalDateTime fin) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String requete = "SELECT m.* FROM membre m " +
                "WHERE m.estDisponible = TRUE AND m.role = ? " +
                "AND m.id NOT IN (" +
                "    SELECT em.membre_id FROM equipage_membre em " +
                "    JOIN vol v ON v.equipage_id = em.equipage_id " +
                "    WHERE v.statutVol != 'Annulé' " +
                "    AND (? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "         OR ? BETWEEN v.heure_depart AND v.heure_arrivee " +
                "         OR v.heure_depart BETWEEN ? AND ? " +
                "         OR v.heure_arrivee BETWEEN ? AND ?)" +
                ")";
        try (PreparedStatement pst = connection.prepareStatement(requete)) {
            pst.setString(1, role.name());
            pst.setTimestamp(2, Timestamp.valueOf(debut));
            pst.setTimestamp(3, Timestamp.valueOf(fin));
            pst.setTimestamp(4, Timestamp.valueOf(debut));
            pst.setTimestamp(5, Timestamp.valueOf(fin));
            pst.setTimestamp(6, Timestamp.valueOf(debut));
            pst.setTimestamp(7, Timestamp.valueOf(fin));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        RoleMembre.valueOf(rs.getString("role")),
                        rs.getBoolean("estDisponible")
                );
                membres.add(m);
            }
        } catch (SQLException ex) {
            throw new SQLException("Erreur lors de la récupération des membres disponibles : " + ex.getMessage(), ex);
        }
        return membres;
    }
}