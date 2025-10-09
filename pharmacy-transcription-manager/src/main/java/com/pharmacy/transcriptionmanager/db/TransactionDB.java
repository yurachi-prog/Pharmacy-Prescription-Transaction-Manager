package com.pharmacy.transcriptionmanager.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDB {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "NewStrongPassword1!"; // Update this!

    public static void insertTransaction(String patientName, String address, int age,
                                         String medicine, int quantity, int total,	 String paymentMethod) throws SQLException {
        String query = "INSERT INTO transactions (patient_name, address, age, medicine, quantity, total, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patientName);
            stmt.setString(2, address);
            stmt.setInt(3, age);
            stmt.setString(4, medicine);
            stmt.setInt(5, quantity);
            stmt.setInt(6, total);
            stmt.setString(7, paymentMethod);
            stmt.executeUpdate();
        }
    }

    // Example: Get all transactions 
    public static List<String[]> getTransactions() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY created_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("patient_name"),
                    rs.getString("address"),
                    String.valueOf(rs.getInt("age")),
                    rs.getString("medicine"),
                    String.valueOf(rs.getInt("quantity")),
                    String.valueOf(rs.getInt("total")),
                    rs.getString("payment_method"),
                    rs.getString("created_at")
                });
            }
        }
        return list;
    }
}
