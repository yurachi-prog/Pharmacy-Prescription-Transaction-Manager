package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardFrame extends JFrame {

    // Data structure: map doctor names to their specialties
    private Map<String, String> doctorSpecialties = new HashMap<>();

    private JPanel mainContent;   // holds the CardLayout
    private CardLayout cardLayout;

    public DashboardFrame() {
        // Initialize the data structure
        doctorSpecialties.put("Dr. Maria Santos", "Cardiologist");
        doctorSpecialties.put("Dr. John Reyes", "Pediatrician");
        doctorSpecialties.put("Dr. Angela Cruz", "Dermatologist");
        doctorSpecialties.put("Dr. Mark Villanueva", "General Practitioner");

        setTitle("UM Pharmacy Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

     // ===== SIDEBAR =====
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(new Color(200, 0, 0));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(30, 10, 30, 10));

        // Only the top buttons (excluding "Log Out")
        String[] topMenuItems = {"Dashboard", "Overview", "Admin"};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 10, 0); // spacing between buttons

        // 1. Add vertical glue at the top to enable centering
        gbc.gridy = 0;
        gbc.weighty = 1;
        sidebar.add(Box.createVerticalGlue(), gbc);

        // 2. Add the menu buttons, incrementing y each time
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER; // center buttons vertically
        for (int i = 0; i < topMenuItems.length; i++) {
            String item = topMenuItems[i];
            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(200, 0, 0));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if (item.equals("Dashboard")) {
                    cardLayout.show(mainContent, "dashboard");
                } else if (item.equals("Overview")) {
                    cardLayout.show(mainContent, "overview");
                } else if (item.equals("Admin")) {
                    new AdminPanelFrame().setVisible(true);
                }
            });
            gbc.gridy = i + 1;
            sidebar.add(btn, gbc);
        }

        // 3. Add another vertical glue after the buttons
        gbc.gridy = topMenuItems.length + 1;
        gbc.weighty = 1;
        sidebar.add(Box.createVerticalGlue(), gbc);

        // 4. Log out button at the bottom
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(new Color(200, 0, 0));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> dispose());
        gbc.gridy = topMenuItems.length + 2;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0); // reset
        sidebar.add(logoutBtn, gbc);

        add(sidebar, BorderLayout.WEST);


        add(sidebar, BorderLayout.WEST);

        // ===== MAIN CONTENT with CardLayout =====
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        mainContent.add(createDashboardPage(), "dashboard");
        mainContent.add(createOverviewPage(), "overview");

        add(mainContent, BorderLayout.CENTER);

        // ===== RIGHT PANEL (Doctors List) =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(350, getHeight()));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel docHeader = new JPanel(new BorderLayout());
        docHeader.setOpaque(false);
        JLabel docTitle = new JLabel("Available Doctors");
        docTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        docTitle.setForeground(new Color(200, 0, 0));

        JLabel viewStaffs = new JLabel("Click to view staffs");
        viewStaffs.setFont(new Font("SansSerif", Font.PLAIN, 12));
        viewStaffs.setForeground(new Color(0, 102, 204));
        viewStaffs.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewStaffs.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showStaffWindow();
            }
        });
        docHeader.add(docTitle, BorderLayout.WEST);
        docHeader.add(viewStaffs, BorderLayout.EAST);
        rightPanel.add(docHeader, BorderLayout.NORTH);

        JPanel doctorsList = new JPanel();
        doctorsList.setLayout(new BoxLayout(doctorsList, BoxLayout.Y_AXIS));
        doctorsList.setBackground(Color.WHITE);

        // Use the data structure for doctor data!
        for (Map.Entry<String, String> entry : doctorSpecialties.entrySet()) {
            doctorsList.add(createDoctorItem(entry.getKey(), entry.getValue()));
        }

        JScrollPane docScroll = new JScrollPane(doctorsList);
        docScroll.setBorder(null);
        docScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(docScroll, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        setVisible(true);
    }

    // ===== Dashboard Page =====
    private JPanel createDashboardPage() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        JLabel header = new JLabel("UM Pharmacy Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        header.setForeground(new Color(200, 0, 0));
        dashboard.add(header, BorderLayout.NORTH);

        JPanel cardsWrapper = new JPanel(new GridBagLayout());
        cardsWrapper.setBackground(Color.WHITE);

        JPanel cardsPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setPreferredSize(new Dimension(500, 550));

        cardsPanel.add(createStyledCard("Prescription Review", "Review pending prescriptions from patients", "Pharmacy", "20 Sept"));
        cardsPanel.add(createStyledCard("Stock Audit", "Check and update medicine stock levels", "Inventory", "22 Sept"));
        cardsPanel.add(createStyledCard("Payment Processing", "Handle and verify recent transactions", "Finance", "25 Sept"));

        cardsWrapper.add(cardsPanel);
        dashboard.add(cardsWrapper, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createOverviewPage() {
        JPanel overview = new JPanel(new BorderLayout());
        overview.setBackground(Color.WHITE);

        JLabel mainHeader = new JLabel("UM Pharmacy Dashboard", SwingConstants.CENTER);
        mainHeader.setFont(new Font("SansSerif", Font.BOLD, 26));
        mainHeader.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainHeader.setForeground(new Color(200, 0, 0));
        overview.add(mainHeader, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        JTextArea info = new JTextArea(
            "Welcome to the Overview!\n\nHere you can display summary stats, charts, or quick links."
        );
        info.setEditable(false);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setBackground(Color.WHITE);
        content.add(info, BorderLayout.CENTER);

        overview.add(content, BorderLayout.CENTER);

        return overview;
    }

    private void showStaffWindow() {
        JDialog staffDialog = new JDialog(this, "Staff List", true);
        staffDialog.setSize(350, 400);
        staffDialog.setResizable(false);
        staffDialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Use the doctorSpecialties map here too!
        for (Map.Entry<String, String> entry : doctorSpecialties.entrySet()) {
            content.add(createDoctorItem(entry.getKey(), entry.getValue()));
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        staffDialog.add(scrollPane);

        staffDialog.setVisible(true);
    }

    private JPanel createStyledCard(String title, String description, String category, String date) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setPreferredSize(new Dimension(500, 160));

        JPanel headerStrip = new JPanel(new BorderLayout());
        headerStrip.setBackground(new Color(200, 0, 0));
        headerStrip.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerStrip.add(titleLabel, BorderLayout.WEST);

        JLabel ratingLabel = new JLabel("★★★★★ ");
        ratingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        ratingLabel.setForeground(Color.WHITE);
        headerStrip.add(ratingLabel, BorderLayout.EAST);

        card.add(headerStrip, BorderLayout.NORTH);

        JLabel descLabel = new JLabel("<html><div style='width:100%;'>" + description + "</div></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descLabel.setForeground(new Color(60, 60, 60));
        descLabel.setBorder(new EmptyBorder(10, 12, 5, 12));
        card.add(descLabel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(5, 12, 10, 12));

        JLabel infoLabel = new JLabel(category + "  |  📅 " + date);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(120, 120, 120));
        footer.add(infoLabel, BorderLayout.WEST);

        JButton viewBtn = new JButton("Click here");
        viewBtn.setBackground(new Color(255, 215, 0));
        viewBtn.setForeground(Color.BLACK);
        viewBtn.setFocusPainted(false);
        viewBtn.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Routing logic
        viewBtn.addActionListener(e -> {
            if (title.equals("Prescription Review")) {
                new PrescriptionReviewFrame().setVisible(true);
            } else if (title.equals("Stock Audit")) {
                new StockViewFrame().setVisible(true);
            } else if (title.equals("Payment Processing")) {
                JOptionPane.showMessageDialog(this, "Payment Processing page coming soon!");
            }
        });

        footer.add(viewBtn, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDoctorItem(String name, String specialty) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel imgLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        imgLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel specLabel = new JLabel(specialty);
        specLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        specLabel.setForeground(new Color(120, 120, 120));

        infoPanel.add(nameLabel);
        infoPanel.add(specLabel);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(imgLabel, BorderLayout.WEST);
        left.add(infoPanel, BorderLayout.CENTER);

        item.add(left, BorderLayout.CENTER);

        return item;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DashboardFrame::new);
    }
}
