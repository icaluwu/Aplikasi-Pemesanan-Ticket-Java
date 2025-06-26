package com.movieticket.controllers;

import com.movieticket.utils.DBConnection;
import java.sql.*;

public class AuthController {
    public static String login(String username, String password) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role");
            }
        }
        return null;
    }
}