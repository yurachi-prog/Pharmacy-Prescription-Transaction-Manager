package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentSuccessFrame extends JFrame {

    // Data structure: list of thank-you messages
    private List<String> thankYouMessages = new ArrayList<>();

    public PaymentSuccessFrame() {
        // Initialize thank-you messages
        thankYouMessages.add("Thank you for your payment.");
        thankYouMessages.add("Your transaction is complete.");

        setTitle("Payment Successful");
        setSize(400, 270);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel();
        root.setBackground(Color.WHITE);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(28, 24, 28, 24));

        JLabel greenCheck = new JLabel("\u2714");
        greenCheck.setFont(new Font("SansSerif", Font.BOLD, 68));
        greenCheck.setForeground(new Color(37, 155, 36));
        greenCheck.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(Box.createVerticalStrut(8));
        root.add(greenCheck);

        JLabel successMsg = new JLabel("Payment Successful!", SwingConstants.CENTER);
        successMsg.setFont(new Font("SansSerif", Font.BOLD, 24));
        successMsg.setForeground(new Color(37, 155, 36));
        successMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(successMsg);

        root.add(Box.createVerticalStrut(18));

        // Use the first message from the list
        JLabel thanks = new JLabel(thankYouMessages.get(0));
        thanks.setFont(new Font("SansSerif", Font.PLAIN, 15));
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(thanks);

        setContentPane(root);
    }
}
