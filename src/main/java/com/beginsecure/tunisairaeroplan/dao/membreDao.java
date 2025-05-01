package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.sql.*;
import java.util.ArrayList;

public class membreDao {

    public static int ajouter(Membre m) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "INSERT INTO membre (cin, nom, prenom, role, estDisponible) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = cn.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, m.getCin());
            pst.setString(2, m.getNom());
            pst.setString(3, m.getPrenom());
            pst.setString(4, m.getRole());
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
        } catch (SQLException ex) {
            System.out.println("Problème d'ajout du membre : " + ex.getMessage());
        }
        return -1;
    }


    public static ArrayList<Membre> lister() {
        ArrayList<Membre> membres = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM membre";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("role"),
                        rs.getBoolean("estDisponible")
                );
                membres.add(m);
            }
            System.out.println("Liste des membres récupérée");
        } catch (SQLException ex) {
            System.out.println("Erreur lors du listing des membres : " + ex.getMessage());
        }
        return membres;
    }

    public static boolean modifier(Membre m) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE membre SET nom = ?, prenom = ?, role = ?, estDisponible = ? WHERE cin = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, m.getNom());
            pst.setString(2, m.getPrenom());
            pst.setString(3, m.getRole());
            pst.setBoolean(4, m.isDisponible());
            pst.setString(5, m.getCin());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Membre modifié !");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de modification : " + ex.getMessage());
        }
        return false;
    }

    public static boolean supprimer(Membre m) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "DELETE FROM membre WHERE cin = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, m.getCin());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Membre supprimé !");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de suppression : " + ex.getMessage());
        }
        return false;
    }
}

