package com.beginsecure.tunisairaeroplan.utilites;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LaConnexion {
    public static Connection seConnecter() throws SQLException {
        try (InputStream input = LaConnexion.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Utilise `cj` et non `com.mysql.jdbc.Driver` (deprecated)
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver non trouvé", e);
            }

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new SQLException("Erreur de connexion à la base", e);
        }
    }
}
