package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PatientDetailsFrame extends JFrame {

    private Map<String, Integer> medicineStockSnapshot = new HashMap<>();
    private JTable prescriptionTable;
    private JTable stockTable;
    private DefaultTableModel prescriptionModel;
    private DefaultTableModel stockModel;
    private JComboBox<String> filterBox;
    private JTextField searchField;
    private JTabbedPane tabs;
    private String patientName, address, age;
    // Add timing label for running time
    private JLabel timingLabel = new JLabel("Execution Time: ");

    private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "NewStrongPassword1!";

    public PatientDetailsFrame(String patientName, String address, String age, int tabIndex) {
        medicineStockSnapshot.put("Paracetamol", 100);
        medicineStockSnapshot.put("Hydrocodone", 50);
        this.patientName = patientName;
        this.address = address;
        this.age = age;

        setTitle("Patient: " + patientName);
        setSize(1040, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(new EmptyBorder(15,15,15,15));
        add(main);

        JLabel header = new JLabel("Patient: " + patientName + " | Age: " + age + " | Address: " + address,
            SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(new Color(200,0,0));
        main.add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        main.add(tabs, BorderLayout.CENTER);

        // Prescription Tab
        String[] pCols = {"Medicine", "Brand", "Price", "Quantity", "Amount per 1 pad", "Total"};
        prescriptionModel = new DefaultTableModel(pCols, 0){
            public boolean isCellEditable(int row, int col) { return col == 3; }
            public Class<?> getColumnClass(int col){ return (col >= 2) ? Integer.class : String.class; }
        };
        prescriptionTable = new JTable(prescriptionModel);
        prescriptionTable.setRowHeight(36);
        JScrollPane pScroll = new JScrollPane(prescriptionTable);

        prescriptionTable.getColumnModel().getColumn(3)
            .setCellEditor(new QuantityButtonEditor(prescriptionModel));
        prescriptionTable.getColumnModel().getColumn(3)
            .setCellRenderer(new QuantityButtonRenderer());

        JPanel prescriptionPanel = new JPanel(new BorderLayout());
        prescriptionPanel.add(pScroll, BorderLayout.CENTER);
        tabs.addTab("Prescription View", prescriptionPanel);

        // Stock View Tab
        String[] sCols = {"Medicine", "Brand", "Price", "Stock Left", "Status"};
        stockModel = new DefaultTableModel(sCols, 0){
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int col){ return (col == 2 || col == 3) ? Integer.class : String.class; }
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
        JScrollPane stockScroll = new JScrollPane(stockTable);

        JPanel stockPanel = new JPanel(new BorderLayout());
        stockPanel.add(stockScroll, BorderLayout.CENTER);

        // Search & filter
        JPanel stockTop = new JPanel(new BorderLayout());
        searchField = new JTextField("Search medicines...");
        searchField.setForeground(Color.GRAY);
        stockTop.add(searchField, BorderLayout.CENTER);
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

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterBox = new JComboBox<>(new String[]{"All", "Available Stock", "No Available Stock"});
        filterBox.addActionListener(e -> applySearchRules());
        filterPanel.add(new JLabel("Filter: "));
        filterPanel.add(filterBox);

        stockPanel.add(stockTop, BorderLayout.NORTH);
        stockPanel.add(filterPanel, BorderLayout.SOUTH);

        // Add timing label to Stock tab panel
        JPanel timingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timingPanel.add(timingLabel);
        stockPanel.add(timingPanel, BorderLayout.NORTH);

        tabs.addTab("Stock View", stockPanel);

        refreshStockTable("All");
        tabs.setSelectedIndex(tabIndex);

        stockTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2 && stockTable.getSelectedRow() != -1){
                    int row = stockTable.getSelectedRow();
                    String med = stockModel.getValueAt(row, 0).toString();
                    if ("No medicines found".equalsIgnoreCase(med)) return;
                    String brand = stockModel.getValueAt(row, 1).toString();
                    int price = Integer.parseInt(stockModel.getValueAt(row, 2).toString());
                    String status = stockModel.getValueAt(row, 4).toString();
                    if ("Not Available".equals(status)) {
                        JOptionPane.showMessageDialog(PatientDetailsFrame.this,
                            med + " is currently ❌ Out of Stock.",
                            "Out of Stock", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int choice = JOptionPane.showConfirmDialog(PatientDetailsFrame.this,
                        "Do you want to add " + med + " to your prescription inventory?",
                        "Add to Prescription", JOptionPane.YES_NO_OPTION);
                    if(choice == JOptionPane.YES_OPTION){
                        addOrIncrementPrescriptionItem(med, brand, price);
                    }
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton proceedPaymentBtn = new JButton("Proceed to Payment");
        proceedPaymentBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        proceedPaymentBtn.setBackground(new Color(0, 150, 0));
        proceedPaymentBtn.setForeground(Color.WHITE);
        proceedPaymentBtn.setPreferredSize(new Dimension(180, 40));
        proceedPaymentBtn.addActionListener(e -> {
            if (prescriptionModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "⚠ No medicines added to prescription.",
                    "Empty Prescription", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int total = 0;
            for (int i = 0; i < prescriptionModel.getRowCount(); i++) {
                int qty = Integer.parseInt(prescriptionModel.getValueAt(i, 3).toString());
                if (qty > 6) {
                    String medName = prescriptionModel.getValueAt(i, 0).toString();
                    JOptionPane.showMessageDialog(this, "⚠ " + medName +
                        " exceeds the maximum limit of 6 pads.\nPlease adjust the quantity.",
                        "Quantity Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Object val = prescriptionModel.getValueAt(i, 5);
                if (val instanceof Integer) total += (Integer)val;
                else if (val != null) total += Integer.parseInt(val.toString());
            }
            try {
                PaymentFrame paymentFrame = new PaymentFrame(patientName, address, age, total, prescriptionModel);
                paymentFrame.setVisible(true);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Payment Panel\n" +
                    "Patient: " + patientName + "\n" +
                    "Total Amount: ₱" + total + "\n\n" +
                    "Please create PaymentFrame class or update constructor.",
                    "Payment", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        bottomPanel.add(proceedPaymentBtn);
        main.add(bottomPanel, BorderLayout.SOUTH);
    }

    // DB RELOAD AND FILTER LOGIC
    private void refreshStockTable(String filter) {
        long startTime = System.nanoTime();
        stockModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT name, brand, price, stock_quantity, " +
                 "CASE WHEN stock_quantity > 0 THEN 'Available' ELSE 'Not Available' END AS status " +
                 "FROM medicines"
             );
             ResultSet rs = ps.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                String med = rs.getString("name");
                String brand = rs.getString("brand");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock_quantity");
                String availability = rs.getString("status");
                if ("Available Stock".equals(filter) && !"Available".equals(availability)) continue;
                if ("No Available Stock".equals(filter) && !"Not Available".equals(availability)) continue;
                stockModel.addRow(new Object[]{med, brand, price, stock, availability});
                found = true;
            }
            if (!found) {
                stockModel.addRow(new Object[]{"No medicines found", "-", "-", "-", "-"});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }

    // Search logic for stock table -- always queries DB
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
             ResultSet rs = ps.executeQuery()) {
            boolean found = false;
            while (rs.next()) {
                String med = rs.getString("name");
                String brand = rs.getString("brand");
                int price = rs.getInt("price");
                int stock = rs.getInt("stock_quantity");
                String status = rs.getString("status");
                if ("Available Stock".equals(filter) && !"Available".equals(status)) continue;
                if ("No Available Stock".equals(filter) && !"Not Available".equals(status)) continue;
                if (!query.isEmpty() && !med.toLowerCase().contains(query) && !brand.toLowerCase().contains(query)) continue;
                stockModel.addRow(new Object[]{med, brand, price, stock, status});
                found = true;
            }
            if (!found) {
                stockModel.addRow(new Object[]{"No medicines found", "-", "-", "-", "-"});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }

    // Rest of your unchanged code below...
    private void addOrIncrementPrescriptionItem(String med, String brand, int price) {
        int existingRow = findPrescriptionRow(med);
        if (existingRow >= 0) {
            JOptionPane.showMessageDialog(this,
                med + " is already in your prescription inventory.",
                "Duplicate Medicine", JOptionPane.WARNING_MESSAGE);
            return;
        }
        prescriptionModel.addRow(new Object[]{med, brand, price, 1, price, price});
    }

    private int findPrescriptionRow(String med) {
        for (int i = 0; i < prescriptionModel.getRowCount(); i++) {
            if (prescriptionModel.getValueAt(i, 0).equals(med)) return i;
        }
        return -1;
    }

    public void updateRowTotal(int row) {
        try {
            int qty = Integer.parseInt(prescriptionModel.getValueAt(row, 3).toString());
            int pricePerPad = Integer.parseInt(prescriptionModel.getValueAt(row, 4).toString());
            int total = qty * pricePerPad;
            prescriptionModel.setValueAt(total, row, 5);
            prescriptionTable.repaint();
        } catch (Exception e) {
            System.err.println("Error updating row total: " + e.getMessage());
        }
    }

    public PatientDetailsFrame(String patientName, String address, String age) {
        this(patientName, address, age, 0);
    }
}
