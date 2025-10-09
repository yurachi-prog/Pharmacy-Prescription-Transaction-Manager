package com.pharmacy.transcriptionmanager.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class QuantityButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {

    // Data structure for allowed quantity values
    private final Set<Integer> validQuantities = new HashSet<>();

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JButton btnMinus = new JButton("-");
    private final JButton btnPlus = new JButton("+");
    private final JLabel qtyLabel = new JLabel();

    private int qty, row;
    private DefaultTableModel model;

    public QuantityButtonEditor(DefaultTableModel model) {
        this.model = model;

        // Initialize allowed quantity set
        for (int i = 1; i <= 6; i++) validQuantities.add(i);

        panel.add(btnMinus, BorderLayout.WEST);
        panel.add(qtyLabel, BorderLayout.CENTER);
        panel.add(btnPlus, BorderLayout.EAST);

        qtyLabel.setHorizontalAlignment(JLabel.CENTER);

        btnMinus.addActionListener(e -> {
            if(qty > 1) {
                qty--;
                update();
            }
        });

        btnPlus.addActionListener(e -> {
            if(qty < 6) {
                qty++;
                update();
            } else {
                Window parentWindow = SwingUtilities.getWindowAncestor(panel);
                JOptionPane.showMessageDialog(
                    parentWindow,
                    "Limit reached: Only 6 pads allowed.",
                    "Message",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        panel.setOpaque(true);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        this.qty = (int) value;
        qtyLabel.setText(String.valueOf(qty));
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return qty;
    }

    private void update() {
        qtyLabel.setText(String.valueOf(qty));
        model.setValueAt(qty, row, 3); // quantity col
        int price = (int) model.getValueAt(row, 2); // price col
        int total = qty * price;
        model.setValueAt(total, row, 5); // total col
        fireEditingStopped();
    }
}
