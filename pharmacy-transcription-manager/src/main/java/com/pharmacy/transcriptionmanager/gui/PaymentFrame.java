package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.pharmacy.transcriptionmanager.db.TransactionDB;
import java.sql.SQLException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentFrame extends JFrame {
    // Data structure: Holds available payment methods
    private List<String> paymentMethods = new ArrayList<>();
    // Add this label for running time
    private JLabel timingLabel = new JLabel("Execution Time: ");

    public PaymentFrame(String patientName, String address, String age, int totalAmount,
                        DefaultTableModel prescriptionData) {
        // Start timing for UI build and summary logic
        long startTime = System.nanoTime();

        paymentMethods.add("PayPal");
        paymentMethods.add("Cards");
        paymentMethods.add("Cash");

        setTitle("Payment Panel");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color bg = new Color(245,245,247);
        Color cardBg = Color.WHITE;
        Color border = new Color(223,225,230);
        Color greenBtn = new Color(40, 167, 69);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(bg);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(30,30,30,30));
        cardPanel.setBackground(cardBg);
        cardPanel.setPreferredSize(new Dimension(800,400));

        // Running time label panel at top
        JPanel timingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timingPanel.add(timingLabel);
        root.add(timingPanel, new GridBagConstraints() {{
            gridx = 0; gridy = 0; gridwidth = 2;
        }});

        GridBagConstraints leftC = new GridBagConstraints();
        leftC.gridx = 0; leftC.gridy = 0; leftC.insets = new Insets(0,0,0,40);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(cardBg);
        leftPanel.setPreferredSize(new Dimension(340,340));
        JLabel paymentTitle = new JLabel("How would you like to pay?");
        paymentTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        paymentTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(paymentTitle);
        leftPanel.add(Box.createVerticalStrut(20));

        ButtonGroup paymentGroup = new ButtonGroup();
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);

        JPanel paypalPanel = createPaymentOption("PayPal", "🅿️", true);
        JPanel cardsPanel = createPaymentOption("Cards", "💳", false);
        JPanel cashPanel = createPaymentOption("Cash", "💵", false);

        optionsPanel.add(paypalPanel);
        optionsPanel.add(Box.createVerticalStrut(12));
        optionsPanel.add(cardsPanel);
        optionsPanel.add(Box.createVerticalStrut(12));
        optionsPanel.add(cashPanel);

        leftPanel.add(optionsPanel);
        leftPanel.add(Box.createVerticalStrut(30));

        JLabel securityNote = new JLabel("<html><div style='text-align:center;color:gray'>Payments are processed securely<br/>Your data is protected</div></html>");
        securityNote.setFont(new Font("SansSerif", Font.PLAIN, 11));
        securityNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(securityNote);

        cardPanel.add(leftPanel, leftC);

        GridBagConstraints rightC = new GridBagConstraints();
        rightC.gridx = 1; rightC.gridy = 0;

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(cardBg);
        rightPanel.setPreferredSize(new Dimension(380,340));

        JLabel orderTitle = new JLabel("Order summary");
        orderTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        orderTitle.setForeground(new Color(47,52,69));
        rightPanel.add(orderTitle);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(createInfoRow("Patient:", patientName));
        rightPanel.add(createInfoRow("Address:", address));
        rightPanel.add(createInfoRow("Age:", age));
        rightPanel.add(Box.createVerticalStrut(15));

        for(int i = 0; i < prescriptionData.getRowCount(); i++) {
            String medicine = prescriptionData.getValueAt(i, 0).toString();
            String qty = prescriptionData.getValueAt(i, 3).toString();
            String itemTotal = prescriptionData.getValueAt(i, 5).toString();
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setOpaque(false);
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JLabel itemLabel = new JLabel(medicine + " (x" + qty + ")");
            itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            itemLabel.setForeground(new Color(85,85,85));
            JLabel priceLabel = new JLabel("₱" + itemTotal);
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            priceLabel.setForeground(new Color(47,52,69));
            itemPanel.add(itemLabel, BorderLayout.WEST);
            itemPanel.add(priceLabel, BorderLayout.EAST);
            rightPanel.add(itemPanel);
            rightPanel.add(Box.createVerticalStrut(8));
        }

        rightPanel.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        rightPanel.add(separator);
        rightPanel.add(Box.createVerticalStrut(15));

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(new Color(47,52,69));

        JLabel totalValue = new JLabel("₱" + totalAmount + ".00");
        totalValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalValue.setForeground(new Color(47,52,69));

        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);
        rightPanel.add(totalPanel);

        rightPanel.add(Box.createVerticalStrut(20));

        JButton payBtn = new JButton("Pay securely");
        payBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        payBtn.setBackground(greenBtn);
        payBtn.setForeground(Color.WHITE);
        payBtn.setFocusPainted(false);
        payBtn.setBorder(new EmptyBorder(12,20,12,20));
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        payBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        payBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { payBtn.setBackground(new Color(32, 145, 57)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { payBtn.setBackground(greenBtn); }
        });

        payBtn.addActionListener(e -> {
            try {
                for(int i = 0; i < prescriptionData.getRowCount(); i++) {
                    String medicine = prescriptionData.getValueAt(i, 0).toString();
                    int qty = Integer.parseInt(prescriptionData.getValueAt(i, 3).toString());
                    int itemTotal = Integer.parseInt(prescriptionData.getValueAt(i, 5).toString());
                    if(qty <= 0) continue;
                    TransactionDB.insertTransaction(
                        patientName, address, Integer.parseInt(age),
                        medicine, qty, itemTotal, "PayPal"
                    );
                }
                PaymentSuccessFrame successFrame = new PaymentSuccessFrame();
                successFrame.setVisible(true);
                PaymentFrame.this.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        rightPanel.add(payBtn);

        cardPanel.add(leftPanel, leftC);
        cardPanel.add(rightPanel, rightC);
        root.add(cardPanel);

        setContentPane(root);

        // Show running time after build complete
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        timingLabel.setText("Execution Time: " + String.format("%.2f", elapsedMs) + " ms");
    }

    private JPanel createPaymentOption(String name, String icon, boolean selected) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(selected ? new Color(240,248,255) : Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selected ? new Color(0,123,255) : new Color(220,220,220), 1),
            new EmptyBorder(12,15,12,15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel leftContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftContent.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(new Color(47,52,69));

        leftContent.add(iconLabel);
        leftContent.add(nameLabel);

        if (selected) {
            JLabel checkmark = new JLabel("●");
            checkmark.setFont(new Font("SansSerif", Font.BOLD, 16));
            checkmark.setForeground(new Color(0,123,255));
            panel.add(checkmark, BorderLayout.EAST);
        } else {
            JLabel circle = new JLabel("○");
            circle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            circle.setForeground(new Color(180,180,180));
            panel.add(circle, BorderLayout.EAST);
        }

        panel.add(leftContent, BorderLayout.WEST);
        return panel;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelText.setForeground(new Color(85,85,85));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("SansSerif", Font.BOLD, 14));
        valueText.setForeground(new Color(47,52,69));

        row.add(labelText, BorderLayout.WEST);
        row.add(valueText, BorderLayout.EAST);

        return row;
    }
}
