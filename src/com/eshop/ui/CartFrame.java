package com.eshop.ui;

import com.eshop.models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class CartFrame extends JFrame {
    private final ShoppingCart cart;
    private final User currentUser;
    private final DecimalFormat df = new DecimalFormat("$#,##0.00");
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;

    public CartFrame(JFrame parent, ShoppingCart cart, User currentUser) {
        this.cart = cart;
        this.currentUser = currentUser;
        initComponents();
    }

    private void initComponents() {
        setTitle("Your Shopping Cart");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Table setup
        String[] columns = {"Product", "Price", "Quantity", "Subtotal", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        
        // Set custom editors
        table.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor());
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());

        // Populate table
        refreshTable();

        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: " + df.format(cart.getTotal()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

        // Checkout button
        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.addActionListener(e -> checkout());
        btnCheckout.setPreferredSize(new Dimension(120, 40));

        // Layout
        totalPanel.add(lblTotal);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalPanel, BorderLayout.CENTER);
        bottomPanel.add(btnCheckout, BorderLayout.EAST);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshTable() {
        model.setRowCount(0);
        
        for (CartItem item : cart.getItems()) {
            JButton btnRemove = new JButton("Remove");
            btnRemove.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    cart.removeItem(item.getProduct().getId());
                    refreshTable();
                    updateTotal();
                }
            });

            model.addRow(new Object[]{
                item.getProduct().getName(),
                df.format(item.getProduct().getPrice()),
                item.getQuantity(),
                df.format(item.getSubtotal()),
                btnRemove
            });
        }
    }

    private void updateTotal() {
        lblTotal.setText("Total: " + df.format(cart.getTotal()));
    }

    private void checkout() {
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Your cart is empty!", "Checkout", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Implement checkout logic
        JOptionPane.showMessageDialog(this, 
            "Order placed successfully!", "Checkout", JOptionPane.INFORMATION_MESSAGE);
        cart.clear();
        dispose();
    }

    // Custom Spinner Editor
    private class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner = new JSpinner();

        public SpinnerEditor() {
            spinner.addChangeListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            spinner.setValue(value);
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }

    // Button Renderer
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                JButton btn = (JButton) value;
                setText(btn.getText());
                setBackground(btn.getBackground());
                setForeground(btn.getForeground());
            }
            return this;
        }
    }

    // Button Editor
    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private Object value;

        public ButtonEditor() {
            button = new JButton();
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.value = value;
            if (value instanceof JButton) {
                button = (JButton) value;
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }
    }
}