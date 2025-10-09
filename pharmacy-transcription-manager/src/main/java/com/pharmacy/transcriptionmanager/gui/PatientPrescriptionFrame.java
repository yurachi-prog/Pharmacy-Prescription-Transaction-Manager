package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class PatientPrescriptionFrame extends JFrame {

    // Data structure: set of gender options
    private Set<String> genderSet = new HashSet<>();

    private JTextField nameField;
    private JTextField addressField;
    private JTextField ageField;
    private JComboBox<String> genderBox;
    private JTextField registrationDateField;

    public interface PatientCreatedListener {
        void onPatientCreated(String name, String age, String gender, String address, String regDate);
    }

    public PatientPrescriptionFrame(String patientName, PatientCreatedListener listener) {
        // add gender options to the set
        genderSet.add("Male");
        genderSet.add("Female");
        genderSet.add("Other");

        setTitle("New Patient Profile");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel header = new JLabel("Create New Patient Profile", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(200, 0, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Patient Name:"));
        nameField = new JTextField(patientName != null ? patientName : "");
        formPanel.add(nameField);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        formPanel.add(ageField);

        formPanel.add(new JLabel("Gender:"));
        // Use genderSet for gender options
        genderBox = new JComboBox<>(genderSet.toArray(new String[0]));
        formPanel.add(genderBox);

        formPanel.add(new JLabel("Registration Date:"));
        registrationDateField = new JTextField(LocalDate.now().toString());
        registrationDateField.setEditable(false);
        formPanel.add(registrationDateField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String age = ageField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            String regDate = registrationDateField.getText().trim();

            if (name.isEmpty() || address.isEmpty() || age.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠ Please fill in all fields.");
                return;
            }

            if (listener != null) {
                listener.onPatientCreated(name, age, gender, address, regDate);
            }

            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
