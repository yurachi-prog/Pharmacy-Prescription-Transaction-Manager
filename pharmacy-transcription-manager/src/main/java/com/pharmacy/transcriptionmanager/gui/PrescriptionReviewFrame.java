package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionReviewFrame extends JFrame {

    private static final Color RED = new Color(200, 0, 0);
    private static final Color GRAY_BORDER = new Color(180, 180, 180);
    private static final Color TEXT_GRAY = new Color(60, 60, 60);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JLabel timingLabel = new JLabel("Execution Time: "); // Added

    // Master list of patients
    private final List<String[]> allPatients = new ArrayList<>();

    public PrescriptionReviewFrame() {
        setTitle("UM Pharmacy Dashboard - Prescription Review");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(main);

        // Top bar with greeting, search, and create profile
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(Color.WHITE);

        JLabel greeting = new JLabel("Good day, Pharmacist!");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 22));
        greeting.setForeground(RED);
        topBar.add(greeting, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        // Search bar with icon
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new LineBorder(GRAY_BORDER, 1, true));
        JLabel searchIcon = new JLabel("\uD83D\uDD0D "); // Unicode magnifying glass
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchIcon.setForeground(TEXT_GRAY);
        searchPanel.add(searchIcon, BorderLayout.WEST);

        searchField = new JTextField("Search patients", 18);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBackground(LIGHT_GRAY);
        searchField.setBorder(null);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Placeholder behavior
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search patients")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_GRAY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search patients");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchRules();
            }
        });

        JLabel createProfileLink = new JLabel("Create a new profile");
        createProfileLink.setFont(new Font("SansSerif", Font.PLAIN, 14));
        createProfileLink.setForeground(new Color(0, 102, 204));
        createProfileLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createProfileLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                PatientPrescriptionFrame newFrame = new PatientPrescriptionFrame(
                    null,
                    (name, age, gender, address, regDate) -> {
                        addPatient(name, age, address);
                        refreshTable(allPatients);
                        PatientDetailsFrame detailsFrame = new PatientDetailsFrame(name, address, age, 0);
                        detailsFrame.setVisible(true);
                    }
                );
                newFrame.setVisible(true);
            }
        });

        rightPanel.add(searchPanel);
        rightPanel.add(createProfileLink);
        topBar.add(rightPanel, BorderLayout.EAST);

        // Add timing label at the top of main panel
        JPanel timingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timingPanel.add(timingLabel);
        main.add(timingPanel, BorderLayout.SOUTH);

        main.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Name", "Age", "Address"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(TEXT_GRAY);
        table.setGridColor(GRAY_BORDER);
        table.setShowGrid(true);

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(RED);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(GRAY_BORDER, 1));
        main.add(scroll, BorderLayout.CENTER);

        addPatient("Juan Dela Cruz", "45", "Davao City");
        addPatient("Maria Santos", "38", "Quezon City");
        addPatient("Pedro Reyes", "50", "Cebu City");
        addPatient("Khang", "29", "Manila");
        refreshTable(allPatients);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    String name = (String) model.getValueAt(row, 0);
                    String age = (String) model.getValueAt(row, 1);
                    String address = (String) model.getValueAt(row, 2);
                    PatientDetailsFrame detailsFrame = new PatientDetailsFrame(name, address, age, 0);
                    detailsFrame.setVisible(true);
                }
            }
        });
    }

    private void applySearchRules() {
        long startTime = System.nanoTime();
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty() || query.equals("search patients")) {
            refreshTable(allPatients);
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
            return;
        }
        if (query.length() < 2) {
            refreshTable(allPatients);
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
            return;
        }
        List<String[]> matches = new ArrayList<>();
        for (String[] p : allPatients) {
            if (p[0].toLowerCase().contains(query) || p[2].toLowerCase().contains(query)) {
                matches.add(p);
            }
        }
        if (matches.isEmpty()) {
            model.setRowCount(0);
            model.addRow(new Object[]{"No patients found", "-", "-"});
        } else {
            refreshTable(matches);
        }
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }

    private void refreshTable(List<String[]> patients) {
        model.setRowCount(0);
        for (String[] p : patients) {
            model.addRow(p);
        }
    }

    private void addPatient(String name, String age, String address) {
        allPatients.add(new String[]{name, age, address});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrescriptionReviewFrame::new);
    }
}
