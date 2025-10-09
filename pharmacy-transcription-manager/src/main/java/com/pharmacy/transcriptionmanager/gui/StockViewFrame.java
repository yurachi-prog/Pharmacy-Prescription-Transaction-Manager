package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockViewFrame extends JFrame {

    // Data structure: Log of all medicine audit interactions
    private List<String> stockAuditLog = new ArrayList<>();
    public void logAudit(String medicine) {
        stockAuditLog.add(medicine);
    }

    private JTable stockTable;
    private DefaultTableModel stockModel;
    private JComboBox<String> filterBox;
    private JTextField searchField;
    // Add timing label for running time
    private JLabel timingLabel = new JLabel("Execution Time: ");

    // DB credentials (change as needed)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "NewStrongPassword1!";

    public StockViewFrame() {
        setTitle("Medicine Stock Audit");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(main);

        JLabel header = new JLabel("Stock View - Check and Update Medicine Inventory", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(new Color(200, 0, 0));
        main.add(header, BorderLayout.NORTH);

        String[] columns = {"Medicine", "Brand", "Price", "Stock Left", "Status"};
        stockModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            public Class<?> getColumnClass(int col) {
                return (col == 2 || col == 3) ? Integer.class : String.class;
            }
        };

        stockTable = new JTable(stockModel);
        stockTable.setRowHeight(30);
        DefaultTableCellRenderer stockRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, false, false, row, col);
                String status = (String) stockModel.getValueAt(row, 4);
                if ("Available".equals(status)) c.setBackground(new Color(200, 255, 200));
                else if ("Not Available".equals(status)) c.setBackground(new Color(255, 200, 200));
                else c.setBackground(Color.WHITE);
                return c;
            }
        };
        for (int i = 0; i < stockTable.getColumnCount(); i++) {
            stockTable.getColumnModel().getColumn(i).setCellRenderer(stockRenderer);
        }

        JScrollPane scroll = new JScrollPane(stockTable);
        main.add(scroll, BorderLayout.CENTER);

        // Search and filter UI
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField("Search medicines...");
        searchField.setForeground(Color.GRAY);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search medicines...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search medicines...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                applySearchRules();
            }
        });
        topPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterBox = new JComboBox<>(new String[]{"All", "Available Stock", "No Available Stock"});
        filterBox.addActionListener(e -> applySearchRules());
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterBox);
        topPanel.add(filterPanel, BorderLayout.EAST);

        main.add(topPanel, BorderLayout.NORTH);

        // Add timing label at bottom
        main.add(timingLabel, BorderLayout.SOUTH);

        // Initial load
        reloadStockTable();

        setVisible(true);
    }

    // Load all data from the database and refresh the table
    private void reloadStockTable() {
        long startTime = System.nanoTime();
        stockModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                "SELECT name, brand, price, stock_quantity, " +
                "CASE WHEN stock_quantity > 0 THEN 'Available' ELSE 'Not Available' END AS status " +
                "FROM medicines"
             );
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                stockModel.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }

    // Apply search and filter rules to the data in the table model
    private void applySearchRules() {
        long startTime = System.nanoTime();
        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("search medicines...")) query = "";
        String filter = (String) filterBox.getSelectedItem();

        stockModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                "SELECT name, brand, price, stock_quantity, " +
                "CASE WHEN stock_quantity > 0 THEN 'Available' ELSE 'Not Available' END AS status " +
                "FROM medicines"
             );
             ResultSet rs = ps.executeQuery()
        ) {
            boolean found = false;
            while (rs.next()) {
                String med = rs.getString("name");
                String brand = rs.getString("brand");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock_quantity");
                String status = rs.getString("status");

                // Filter logic
                if (filter.equals("Available Stock") && !"Available".equals(status)) continue;
                if (filter.equals("No Available Stock") && !"Not Available".equals(status)) continue;
                if (!query.isEmpty() && !med.toLowerCase().contains(query) && !brand.toLowerCase().contains(query)) continue;

                stockModel.addRow(new Object[]{med, brand, price, stock, status});
                found = true;
            }
            if (!found) {
                stockModel.addRow(new Object[]{"No medicines found", "-", "-", "-", "-"});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }
}
