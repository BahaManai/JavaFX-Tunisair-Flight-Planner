package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class equipageDao {

    public static boolean ajouter(Equipage e) {
        Connection cn = LaConnexion.seConnecter();
        String req = "INSERT INTO equipage (nomEquipage) VALUES (?)";
        try {
            PreparedStatement pst = cn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, e.getNomEquipage());
            int n = pst.executeUpdate();
            if (n >= 1) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int idGenere = rs.getInt(1);
                    e.getMembres().forEach(m -> associerMembre(idGenere, m.getId()));
                }
                System.out.println("Équipage ajouté.");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur d'ajout d’équipage : " + ex.getMessage());
        }
        return false;
    }

    public static ArrayList<Equipage> lister() {
        ArrayList<Equipage> equipages = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String req = "SELECT * FROM equipage";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nomEquipage");
                Equipage e = new Equipage(id, nom);
                // récupérer les membres associés
                e.getMembres().addAll(recupererMembres(id));
                equipages.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de listing des équipages : " + ex.getMessage());
        }
        return equipages;
    }

    public static boolean modifier(Equipage e) {
        Connection cn = LaConnexion.seConnecter();
        String req = "UPDATE equipage SET nomEquipage = ? WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(req);
            pst.setString(1, e.getNomEquipage());
            pst.setInt(2, e.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Équipage modifié.");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de modification : " + ex.getMessage());
        }
        return false;
    }

    public static boolean supprimer(Equipage e) {
        Connection cn = LaConnexion.seConnecter();
        String req = "DELETE FROM equipage WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(req);
            pst.setInt(1, e.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Équipage supprimé.");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de suppression : " + ex.getMessage());
        }
        return false;
    }

    // --- Gestion Equipage_Membre ---

    // Associer un membre à un équipage
    public static void associerMembre(int idEquipage, int idMembre) {
        Connection cn = LaConnexion.seConnecter();
        String req = "INSERT IGNORE INTO equipage_membre (equipage_id, membre_id) VALUES (?, ?)";
        try {
            PreparedStatement pst = cn.prepareStatement(req);
            pst.setInt(1, idEquipage);
            pst.setInt(2, idMembre);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Erreur d'association membre/équipage : " + ex.getMessage());
        }
    }

    // Récupérer les membres d’un équipage
    public static HashSet<Membre> recupererMembres(int idEquipage) {
        HashSet<Membre> membres = new HashSet<>();
        Connection cn = LaConnexion.seConnecter();
        String req = "SELECT m.* FROM membre m INNER JOIN equipage_membre em ON m.id = em.membre_id WHERE em.equipage_id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(req);
            pst.setInt(1, idEquipage);
            ResultSet rs = pst.executeQuery();
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
        } catch (SQLException ex) {
            System.out.println("Erreur récupération membres : " + ex.getMessage());
        }
        return membres;
    }
}

