package com.thegroomroom.ui.dialogs;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.Barber;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernTextField;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

public class AddBarberDialog extends JDialog {
    private ModernTextField txtName;
    private ModernTextField txtSpecialization;
    private ModernTextField txtChair;
    private ModernTextField txtCommission;
    private ModernTextField txtPhone;
    private Runnable onSuccess;

    public AddBarberDialog(Frame parent, Runnable onSuccess) {
        super(parent, "Add New Stylist / Barber - The Groom Room", true);
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
        JLabel lblTitle = new JLabel("Add Barber / Stylist");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        card.add(lblTitle, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 14));
        form.setOpaque(false);

        txtName = new ModernTextField("Full Name (e.g. Sameer Sheikh)");
        txtSpecialization = new ModernTextField("Specialization (e.g. Master Beard Stylist)");
        txtChair = new ModernTextField(String.valueOf(DataStore.getInstance().getBarbers().size() + 1));
        txtCommission = new ModernTextField("40.0");
        txtPhone = new ModernTextField("10-digit Mobile Number");

        addFormField(form, "Stylist Name:", txtName);
        addFormField(form, "Specialization:", txtSpecialization);
        addFormField(form, "Chair / Station No:", txtChair);
        addFormField(form, "Commission Rate (%):", txtCommission);
        addFormField(form, "Mobile (+91):", txtPhone);

        card.add(form, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        ModernButton btnSave = new ModernButton("Add Stylist", ModernButton.ButtonStyle.ACCENT);
        btnSave.addActionListener(e -> saveBarber());

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

    private void saveBarber() {
        String name = txtName.getText().trim();
        String spec = txtSpecialization.getText().trim();
        String chairStr = txtChair.getText().trim();
        String commStr = txtCommission.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || chairStr.isEmpty() || commStr.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all stylist details.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int chair = Integer.parseInt(chairStr);
            double comm = Double.parseDouble(commStr);

            Barber b = new Barber();
            b.setName(name);
            b.setSpecialization(spec);
            b.setChairNumber(chair);
            b.setCommissionRate(comm);
            b.setPhone(phone);
            b.setStatus("AVAILABLE");
            b.setTotalCuts(0);

            DataStore.getInstance().addBarber(b);
            if (onSuccess != null) onSuccess.run();
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Chair and Commission %.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
