package com.company.dao;

import com.company.model.Admins;
import com.company.utils.DBConnection;

import java.sql.*;

public class AdminsDao {

    public Admins findByUsername(String username) {
        String sql = "SELECT * FROM Admins WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admins admin = new Admins();
                    admin.setAdminId(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    admin.setRole(rs.getString("role"));
                    admin.setLastLogin(rs.getTimestamp("last_login"));
                    admin.setCreatedAt(rs.getTimestamp("created_at"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resetDefaultAdmin() {
        // Enforce the requested password '123456'
        String checkSql = "SELECT count(*) FROM Admins WHERE username = 'admin'";
        String insertSql = "INSERT INTO Admins (username, password, role) VALUES ('admin', '123456', 'super_admin')";
        String updateSql = "UPDATE Admins SET password = '123456' WHERE username = 'admin'";

        try (Connection conn = DBConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement stmt = conn.prepareStatement(checkSql);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    exists = true;
                }
            }

            if (exists) {
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.executeUpdate();
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLastLogin(int adminId) {
        String sql = "UPDATE Admins SET last_login = NOW() WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
