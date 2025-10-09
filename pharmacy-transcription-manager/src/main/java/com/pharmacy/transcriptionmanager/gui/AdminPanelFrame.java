package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminPanelFrame extends JFrame {

    // DATA STRUCTURE: Tabs for this admin panel  
    private List<String> adminTabs = new ArrayList<>();

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "NewStrongPassword1!";

    private DefaultTableModel medicineTableModel;
    private JTable medicineTable;
    private JTextField searchField;
    // Add this for running time
    private JLabel timingLabel = new JLabel("Execution Time: ");

    public AdminPanelFrame() {
        // Initialize data structure
        adminTabs.add("Medicine Management");
        adminTabs.add("User Management");
        adminTabs.add("Transaction History");
        adminTabs.add("Reports & Analytics");

        setTitle("Admin Panel - Pharmacy Management System");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel headerLabel = new JLabel("Administrator Panel", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("Medicine Management", createMedicinePanel());
        // ACTUAL DATABASE USER MANAGEMENT PANEL
        tabs.addTab("User Management", createUserManagementPanel());
        tabs.addTab("Transaction History", createTransactionHistoryPanel());
        tabs.addTab("Reports & Analytics", createReportsPanel());
        mainPanel.add(tabs, BorderLayout.CENTER);

        // Place timingLabel directly above the close button
        JPanel timingPanel = new JPanel(new BorderLayout());
        timingPanel.add(timingLabel, BorderLayout.WEST);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(closeBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(timingPanel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        loadMedicineData("");
    }

    // --------- USER MANAGEMENT DB PANEL ----------
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Username", "Role", "Status", "Created At"};
        DefaultTableModel userModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(userModel);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        // Load users from DB
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT id, username, role, status, created_at FROM users";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                userModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            panel.add(new JLabel("Error loading users: " + ex.getMessage()), BorderLayout.NORTH);
        }
        return panel;
    }
    // ----------- END USER MANAGEMENT PANEL -------

    // ------------- UNCHANGED EXISTING CODE BELOW -------------

    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> loadMedicineData(searchField.getText().trim()));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadMedicineData(searchField.getText().trim());
            }
        });
        topPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchBtn, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Medicine Name", "Brand", "Price", "Stock Left", "Status"};
        medicineTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Medicine");
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> showAddMedicineDialog());
        updateBtn.addActionListener(e -> showUpdateMedicineDialog());
        deleteBtn.addActionListener(e -> deleteSelectedMedicine());
        refreshBtn.addActionListener(e -> loadMedicineData(""));

        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Patient Name", "Address", "Age", "Medicine", "Quantity", "Total", "Payment Method", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT patient_name, address, age, medicine, quantity, total, payment_method, created_at FROM transactions";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("patient_name"),
                    rs.getString("address"),
                    rs.getInt("age"),
                    rs.getString("medicine"),
                    rs.getInt("quantity"),
                    rs.getInt("total"),
                    rs.getString("payment_method"),
                    rs.getTimestamp("created_at")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + ex.getMessage());
        }
        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton lowStockBtn = new JButton("Show Low Stock");
        JTextArea reportArea = new JTextArea(10, 38);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        reportArea.setEditable(false);

        lowStockBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("Low Stock Medicines:\n\n");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT name, stock_quantity FROM medicines WHERE stock_quantity < 10";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next())
                    sb.append(String.format("%-20s %d left\n", rs.getString(1), rs.getInt(2)));
            } catch (SQLException ex) {
                sb.append("DB Error: ").append(ex.getMessage());
            }
            reportArea.setText(sb.toString());
        });

        panel.add(lowStockBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        return panel;
    }

    private void loadMedicineData(String keyword) {
        long startTime = System.nanoTime();
        medicineTableModel.setRowCount(0);
        String sql = "SELECT * FROM medicines"
                + (keyword == null || keyword.isEmpty()
                ? ""
                : " WHERE name LIKE ? OR brand LIKE ?");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getInt("stock_quantity") > 0 ? "Available" : "Not Available";
                medicineTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getInt("price"),
                        rs.getInt("stock_quantity"),
                        status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }

    private void showAddMedicineDialog() {
        JTextField nameF = new JTextField(), brandF = new JTextField(), priceF = new JTextField(), stockF = new JTextField();
        JPanel grid = new JPanel(new GridLayout(0, 2, 4, 4));
        grid.add(new JLabel("Medicine Name:")); grid.add(nameF);
        grid.add(new JLabel("Brand:")); grid.add(brandF);
        grid.add(new JLabel("Price:")); grid.add(priceF);
        grid.add(new JLabel("Stock:")); grid.add(stockF);
        int ok = JOptionPane.showConfirmDialog(this, grid, "Add Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim(), brand = brandF.getText().trim(),
                    priceStr = priceF.getText().trim(), stockStr = stockF.getText().trim();
            if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.");
                return;
            }
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO medicines(name, brand, price, stock_quantity, status) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, brand);
                ps.setInt(3, Integer.parseInt(priceStr));
                int stock = Integer.parseInt(stockStr);
                ps.setInt(4, stock);
                ps.setString(5, (stock > 0 ? "Available" : "Not Available"));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Medicine added!");
                loadMedicineData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    private void showUpdateMedicineDialog() {
        int row = medicineTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row!");
            return;
        }
        int id = (int) medicineTableModel.getValueAt(row, 0);
        String currentName = (String) medicineTableModel.getValueAt(row, 1);
        String currentBrand = (String) medicineTableModel.getValueAt(row, 2);
        int currentPrice = (int) medicineTableModel.getValueAt(row, 3);
        int currentStock = (int) medicineTableModel.getValueAt(row, 4);
        String currStatus = (String) medicineTableModel.getValueAt(row, 5);

        JTextField nameF = new JTextField(currentName);
        JTextField brandF = new JTextField(currentBrand);
        JTextField priceF = new JTextField(String.valueOf(currentPrice));
        JTextField stockF = new JTextField(String.valueOf(currentStock));

        String[] statusOptions = {"Available", "Not Available"};
        JComboBox<String> statusBox = new JComboBox<>(statusOptions);
        statusBox.setSelectedItem(currStatus);

        JPanel grid = new JPanel(new GridLayout(0, 2, 4, 4));
        grid.add(new JLabel("Medicine Name:")); grid.add(nameF);
        grid.add(new JLabel("Brand:")); grid.add(brandF);
        grid.add(new JLabel("Price:")); grid.add(priceF);
        grid.add(new JLabel("Stock:")); grid.add(stockF);
        grid.add(new JLabel("Status:")); grid.add(statusBox);

        int ok = JOptionPane.showConfirmDialog(this, grid, "Update Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "UPDATE medicines SET name=?, brand=?, price=?, stock_quantity=?, status=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameF.getText().trim());
                ps.setString(2, brandF.getText().trim());
                ps.setInt(3, Integer.parseInt(priceF.getText().trim()));
                ps.setInt(4, Integer.parseInt(stockF.getText().trim()));
                ps.setString(5, (String)statusBox.getSelectedItem());
                ps.setInt(6, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Medicine updated!");
                loadMedicineData(""); // reload table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row!"); return; }
        int id = (int) medicineTableModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Delete selected medicine?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "DELETE FROM medicines WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted!");
                loadMedicineData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }
}
