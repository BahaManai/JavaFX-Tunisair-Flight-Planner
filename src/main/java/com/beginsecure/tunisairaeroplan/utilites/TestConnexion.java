package com.beginsecure.tunisairaeroplan.utilites;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            // 1. Tester la connexion
            System.out.println("Tentative de connexion...");
            Connection conn = LaConnexion.seConnecter();

            // 2. Vérifier si la connexion est valide
            if(conn != null && !conn.isClosed()) {
                System.out.println("✅ Connexion réussie!");
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("User: " + conn.getMetaData().getUserName());
            }
        } catch (SQLException e) {
            System.err.println("❌ Échec de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

}