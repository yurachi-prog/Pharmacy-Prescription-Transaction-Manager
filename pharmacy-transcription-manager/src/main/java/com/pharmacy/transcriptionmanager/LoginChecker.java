package com.pharmacy.transcriptionmanager;

import java.sql.*;

public class LoginChecker {
    // Database credentials, adjust as needed
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "YourPasswordHere"; // change this!

    public static boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=? AND status='active'";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if found

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // Simple test
        String user = "admin";
        String pass = "password123";
        if (authenticate(user, pass)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed.");
        }
    }
}
