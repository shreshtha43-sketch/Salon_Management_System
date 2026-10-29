package com.thegroomroom.ui.dialogs;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.ServiceItem;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernTextField;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

public class AddServiceDialog extends JDialog {
    private ModernTextField txtName;
    private JComboBox<String> comboCategory;
    private ModernTextField txtDuration;
    private ModernTextField txtPrice;
    private ModernTextField txtDesc;
    private Runnable onSuccess;

    public AddServiceDialog(Frame parent, Runnable onSuccess) {
        super(parent, "Add New Salon Service - The Groom Room", true);
        this.onSuccess = onSuccess;
        setSize(460, 480);
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
        JLabel lblTitle = new JLabel("Create Salon Service");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        card.add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 14));
        form.setOpaque(false);

        txtName = new ModernTextField("e.g. Keratin Hair Spa");
        comboCategory = new JComboBox<>(new String[]{"HAIR", "BEARD", "SPA_DETOX", "FACIAL_CLEANUP", "BRIDAL_GROOM"});
        txtDuration = new ModernTextField("30");
        txtPrice = new ModernTextField("550.00");
        txtDesc = new ModernTextField("Brief service summary");

        addFormField(form, "Service Name:", txtName);
        addFormField(form, "Category:", comboCategory);
        addFormField(form, "Duration (Minutes):", txtDuration);
        addFormField(form, "Price (₹ INR):", txtPrice);
        addFormField(form, "Description:", txtDesc);

        card.add(form, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        ModernButton btnSave = new ModernButton("Save Service", ModernButton.ButtonStyle.ACCENT);
        btnSave.addActionListener(e -> saveService());

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

    private void saveService() {
        String name = txtName.getText().trim();
        String cat = (String) comboCategory.getSelectedItem();
        String durStr = txtDuration.getText().trim();
        String priceStr = txtPrice.getText().trim();

        if (name.isEmpty() || durStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all service details.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int dur = Integer.parseInt(durStr);
            double price = Double.parseDouble(priceStr);

            ServiceItem s = new ServiceItem();
            s.setName(name);
            s.setCategory(cat);
            s.setDurationMinutes(dur);
            s.setPrice(price);
            s.setDescription(txtDesc.getText().trim());
            s.setActive(true);

            DataStore.getInstance().addService(s);
            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Duration and Price.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
