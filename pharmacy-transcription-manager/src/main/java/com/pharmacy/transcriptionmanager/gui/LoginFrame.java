package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame implements ActionListener {

    // Data structure: map of sample users and their passwords
    private Map<String, String> sampleUsers = new HashMap<>();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private final String USERNAME = "admin";
    private final String PASSWORD = "password123";

    public LoginFrame() {
        // Initialize data structure
        sampleUsers.put("admin", "password123");
        sampleUsers.put("pharmacist1", "rxpharma");

        setTitle("UM Pharmacy Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);
        setType(Type.UTILITY);

        setLayout(new BorderLayout());

        // ===== LEFT PANEL with gradient =====
        JPanel leftPanel = new GradientPanel(new Color(200, 0, 0), new Color(255, 215, 0));
        leftPanel.setPreferredSize(new Dimension(420, 600));
        leftPanel.setLayout(new GridBagLayout()); // center morphic glass

        // Morphic glass panel
        JPanel glassPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Semi-transparent white background
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Light gradient overlay
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 100),
                        0, getHeight(), new Color(255, 255, 255, 30));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // White border
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);

                g2.dispose();
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setPreferredSize(new Dimension(260, 260));
        glassPanel.setLayout(new BorderLayout());

        // Logo inside glass
        JLabel logoLabel = new JLabel(new ImageIcon("src/main/resources/icons/UM.png"));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        glassPanel.add(logoLabel, BorderLayout.CENTER);

        leftPanel.add(glassPanel, new GridBagConstraints());

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Headings
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel welcome = new JLabel("Hello! Welcome Back");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcome.setForeground(new Color(200, 0, 0));
        rightPanel.add(welcome, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 20, 0);
        JLabel title = new JLabel("Sign in to continue");
        title.setFont(new Font("SansSerif", Font.PLAIN, 16));
        title.setForeground(new Color(120, 120, 120));
        rightPanel.add(title, gbc);

        // Username label + field
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(new Color(50, 50, 50));
        rightPanel.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField();
        styleTextField(usernameField);
        rightPanel.add(usernameField, gbc);

        // Password label + field (closer to Username)
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 0, 0); // reduced gap above password label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passLabel.setForeground(new Color(50, 50, 50));
        rightPanel.add(passLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0); // small gap below password field
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        rightPanel.add(passwordField, gbc);

        // Message label
        gbc.gridy++;
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(new Color(200, 0, 0));
        rightPanel.add(messageLabel, gbc);

        // Login button (no yellow border)
        gbc.gridy++;
        loginButton = new JButton("Sign In");
        stylePrimaryButton(loginButton);
        loginButton.addActionListener(this);
        rightPanel.add(loginButton, gbc);

        // Add panels
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(150, 150, 150), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(30, 30, 30));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(200, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());
        // You may use sampleUsers here for demo login
        if (sampleUsers.containsKey(user) && sampleUsers.get(user).equals(pass)) {
            SwingUtilities.invokeLater(() -> {
                new DashboardFrame();
                dispose();
            });
        } else {
            messageLabel.setForeground(new Color(200, 0, 0));
            messageLabel.setText("Invalid username or password");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }

    // Gradient panel for left side
    static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;
        public GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, start, w, h, end);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }
}
