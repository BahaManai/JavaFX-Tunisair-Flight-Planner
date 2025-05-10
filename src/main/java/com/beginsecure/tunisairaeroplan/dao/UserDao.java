package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.User;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;
import com.beginsecure.tunisairaeroplan.utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private Connection connection;

    public UserDao() {
        this.connection = LaConnexion.seConnecter();
    }

    public void createAdminIfNotExists() {
        String checkSql = "SELECT COUNT(*) FROM users WHERE is_admin = TRUE";
        String insertSql = "INSERT INTO users (nom, prenom, cin, matricule, date_naissance, nationalite, departement, poste, base_affectation, aeroport, email, telephone, encrypted_password, salt, is_approved, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String salt = PasswordUtils.generateSalt();
                String encryptedPassword = PasswordUtils.hashPassword("molkabaha2025", salt);

                try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                    pstmt.setString(1, "Admin");
                    pstmt.setString(2, "System");
                    pstmt.setString(3, "00000000");
                    pstmt.setString(4, "ADMIN001");
                    pstmt.setDate(5, Date.valueOf("2000-01-01"));
                    pstmt.setString(6, "Tunisienne");
                    pstmt.setString(7, "IT");
                    pstmt.setString(8, "Administrateur");
                    pstmt.setString(9, "Tunis");
                    pstmt.setString(10, "Tunis-Carthage");
                    pstmt.setString(11, "molkabaha778@tunisair.com");
                    pstmt.setString(12, "00000000");
                    pstmt.setString(13, encryptedPassword);
                    pstmt.setString(14, salt);
                    pstmt.setBoolean(15, true);
                    pstmt.setBoolean(16, true);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (nom, prenom, cin, matricule, date_naissance, nationalite, departement, poste, base_affectation, aeroport, email, telephone, encrypted_password, salt, is_approved, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getCin());
            stmt.setString(4, user.getMatricule());
            stmt.setDate(5, Date.valueOf(user.getDateNaissance()));
            stmt.setString(6, user.getNationalite());
            stmt.setString(7, user.getDepartement());
            stmt.setString(8, user.getPoste());
            stmt.setString(9, user.getBaseAffectation());
            stmt.setString(10, user.getAeroport());
            stmt.setString(11, user.getEmail());
            stmt.setString(12, user.getTelephone());
            stmt.setString(13, user.getEncryptedPassword());
            stmt.setString(14, user.getSalt());
            stmt.setBoolean(15, user.isApproved());
            stmt.setBoolean(16, user.isAdmin());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getPendingApprovals() {
        List<User> pendingUsers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_approved = false";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pendingUsers.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingUsers;
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET is_approved = true WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByCin(String cin) {
        String sql = "SELECT * FROM users WHERE cin = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByMatricule(String matricule) {
        String sql = "SELECT * FROM users WHERE matricule = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, matricule);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setCin(rs.getString("cin"));
        user.setMatricule(rs.getString("matricule"));
        user.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
        user.setNationalite(rs.getString("nationalite"));
        user.setDepartement(rs.getString("departement"));
        user.setPoste(rs.getString("poste"));
        user.setBaseAffectation(rs.getString("base_affectation"));
        user.setAeroport(rs.getString("aeroport"));
        user.setEmail(rs.getString("email"));
        user.setTelephone(rs.getString("telephone"));
        user.setEncryptedPassword(rs.getString("encrypted_password"));
        user.setSalt(rs.getString("salt"));
        user.setApproved(rs.getBoolean("is_approved"));
        user.setAdmin(rs.getBoolean("is_admin"));
        user.setDateInscription(rs.getTimestamp("date_inscription").toLocalDateTime());
        return user;
    }
}
