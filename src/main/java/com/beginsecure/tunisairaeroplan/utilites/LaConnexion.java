package com.beginsecure.tunisairaeroplan.utilites;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LaConnexion {
    private static Connection con;

    public static Connection seConnecter() {
        if (con == null) {
            try (InputStream input = LaConnexion.class.getClassLoader()
                    .getResourceAsStream("config.properties")) {

                Properties prop = new Properties();
                prop.load(input);

                // Chargement dynamique du driver
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new SQLException("MySQL JDBC Driver non trouvé", e);
                }

                // Paramètres de connexion
                String url = prop.getProperty("db.url");
                String user = prop.getProperty("db.user");
                String password = prop.getProperty("db.password");

                // Validation des paramètres
                if (url == null || user == null || password == null) {
                    throw new SQLException("Configuration DB incomplète");
                }

                con = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion établie avec succès");

            } catch (SQLException e) {
                System.err.println("Erreur SQL: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Erreur inattendue: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return con;
    }
}