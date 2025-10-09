package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuantityButtonRenderer extends JPanel implements TableCellRenderer {

    // Data structure: allowed increments
    private List<Integer> increments = new ArrayList<>();

    private final JButton btnMinus = new JButton("-");
    private final JButton btnPlus = new JButton("+");
    private final JLabel qtyLabel = new JLabel();

    public QuantityButtonRenderer() {
        // Initialize allowed increments (data structure)
        for (int i = 1; i <= 6; i++) increments.add(i);

        setLayout(new BorderLayout());
        add(btnMinus, BorderLayout.WEST);
        add(qtyLabel, BorderLayout.CENTER);
        add(btnPlus, BorderLayout.EAST);

        qtyLabel.setHorizontalAlignment(JLabel.CENTER);

        btnMinus.setFocusable(false);
        btnPlus.setFocusable(false);
        btnMinus.setOpaque(false);
        btnPlus.setOpaque(false);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        qtyLabel.setText(value != null ? value.toString() : "");
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(Color.WHITE);
        }
        return this;
    }
}
