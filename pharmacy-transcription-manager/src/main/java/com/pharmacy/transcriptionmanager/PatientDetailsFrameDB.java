package com.pharmacy.transcriptionmanager;

import java.sql.*;
import java.util.*;

public class PatientDetailsFrameDB {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "NewStrongPassword1!";

    public static List<String[]> getAllMedicines() {
        List<String[]> medicines = new ArrayList<>();
        String sql = "SELECT name, brand, price, stock_quantity, status FROM medicines";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medicines.add(new String[] {
                        rs.getString("name"),
                        rs.getString("brand"),
                        String.valueOf(rs.getInt("price")),
                        String.valueOf(rs.getInt("stock_quantity")),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return medicines;
    }
}
