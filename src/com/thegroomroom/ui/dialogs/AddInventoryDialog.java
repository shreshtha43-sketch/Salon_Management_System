package com.thegroomroom.ui.dialogs;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.InventoryItem;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernTextField;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;

public class AddInventoryDialog extends JDialog {
    private ModernTextField txtProduct;
    private ModernTextField txtCategory;
    private ModernTextField txtQty;
    private ModernTextField txtPrice;
    private ModernTextField txtCost;
    private ModernTextField txtThreshold;
    private ModernTextField txtSupplier;
    private Runnable onSuccess;

    public AddInventoryDialog(Frame parent, Runnable onSuccess) {
        super(parent, "Add Retail / Salon Product - The Groom Room", true);
        this.onSuccess = onSuccess;
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_CANVAS_BG);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("Add Inventory Product");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        card.add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setOpaque(false);

        txtProduct = new ModernTextField("e.g. Keratin Shampoo (250ml)");
        txtCategory = new ModernTextField("Hair Care");
        txtQty = new ModernTextField("15");
        txtPrice = new ModernTextField("650.00");
        txtCost = new ModernTextField("380.00");
        txtThreshold = new ModernTextField("5");
        txtSupplier = new ModernTextField("Salon Care Dist.");

        addFormField(form, "Product Name:", txtProduct);
        addFormField(form, "Category:", txtCategory);
        addFormField(form, "Quantity In Stock:", txtQty);
        addFormField(form, "Selling Price (₹):", txtPrice);
        addFormField(form, "Cost Price (₹):", txtCost);
        addFormField(form, "Alert Threshold:", txtThreshold);
        addFormField(form, "Supplier:", txtSupplier);

        card.add(form, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        ModernButton btnSave = new ModernButton("Save Product", ModernButton.ButtonStyle.ACCENT);
        btnSave.addActionListener(e -> saveProduct());

        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        btnCancel.addActionListener(e -> dispose());

        actions.add(btnSave);
        actions.add(btnCancel);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        add(root);
    }

    private void addFormField(JPanel p, String label, JComponent comp) {
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        p.add(l);
        p.add(comp);
    }

    private void saveProduct() {
        String name = txtProduct.getText().trim();
        String cat = txtCategory.getText().trim();
        String qtyStr = txtQty.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String costStr = txtCost.getText().trim();
        String threshStr = txtThreshold.getText().trim();
        String supplier = txtSupplier.getText().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product name, quantity and price.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);
            double cost = costStr.isEmpty() ? 0.0 : Double.parseDouble(costStr);
            int thresh = threshStr.isEmpty() ? 5 : Integer.parseInt(threshStr);

            InventoryItem item = new InventoryItem(
                    0, name, cat, qty, price, cost, thresh, supplier,
                    new Timestamp(System.currentTimeMillis())
            );

            DataStore.getInstance().addInventoryItem(item);
            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
