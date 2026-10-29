package com.thegroomroom.ui.dialogs;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.Appointment;
import com.thegroomroom.model.Barber;
import com.thegroomroom.model.Customer;
import com.thegroomroom.model.ServiceItem;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernTextField;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AddAppointmentDialog extends JDialog {
    private ModernTextField txtPhone;
    private ModernTextField txtName;
    private JComboBox<Barber> comboBarber;
    private JComboBox<String> comboTime;
    private ModernTextField txtNotes;
    private JLabel lblTotalEstimate;

    private List<JCheckBox> serviceCheckBoxes = new ArrayList<>();
    private Runnable onSuccessCallback;

    public AddAppointmentDialog(Frame parent, Runnable onSuccess) {
        super(parent, "New Salon Appointment - The Groom Room", true);
        this.onSuccessCallback = onSuccess;
        setSize(560, 680);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_CANVAS_BG);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Book New Appointment");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JLabel sub = new JLabel("Enter client details, select stylist and services.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(sub);
        header.add(Box.createVerticalStrut(8));
        header.add(new JSeparator());

        card.add(header, BorderLayout.NORTH);

        // Body with ScrollPane
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Client info
        JPanel clientGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        clientGrid.setOpaque(false);

        txtPhone = new ModernTextField("10-digit Mobile Number");
        txtName = new ModernTextField("Client Full Name");

        txtPhone.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String phone = txtPhone.getText().trim();
                Customer c = DataStore.getInstance().getCustomerByPhone(phone);
                if (c != null) {
                    txtName.setText(c.getName());
                }
            }
        });

        clientGrid.add(createFieldLabel("Customer Mobile (+91):"));
        clientGrid.add(createFieldLabel("Customer Name:"));
        clientGrid.add(txtPhone);
        clientGrid.add(txtName);

        body.add(clientGrid);
        body.add(Box.createVerticalStrut(12));

        // Barber and Time Grid
        JPanel timingGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        timingGrid.setOpaque(false);

        comboBarber = new JComboBox<>();
        comboBarber.setFont(UITheme.FONT_BODY);
        for (Barber b : DataStore.getInstance().getBarbers()) {
            comboBarber.addItem(b);
        }

        comboTime = new JComboBox<>(new String[]{
                "10:00 AM", "10:45 AM", "11:30 AM", "12:15 PM", "01:00 PM",
                "02:00 PM", "02:45 PM", "03:30 PM", "04:15 PM", "05:00 PM",
                "05:45 PM", "06:30 PM", "07:15 PM", "08:00 PM"
        });
        comboTime.setFont(UITheme.FONT_BODY);

        timingGrid.add(createFieldLabel("Assign Stylist / Barber:"));
        timingGrid.add(createFieldLabel("Time Slot:"));
        timingGrid.add(comboBarber);
        timingGrid.add(comboTime);

        body.add(timingGrid);
        body.add(Box.createVerticalStrut(14));

        // Services checklist
        body.add(createFieldLabel("Select Services:"));
        body.add(Box.createVerticalStrut(6));

        JPanel servicesBox = new JPanel();
        servicesBox.setLayout(new BoxLayout(servicesBox, BoxLayout.Y_AXIS));
        servicesBox.setBackground(UITheme.COLOR_CANVAS_BG);
        servicesBox.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (ServiceItem s : DataStore.getInstance().getServices()) {
            JCheckBox cb = new JCheckBox(s.getName() + " - " + s.getFormattedPrice());
            cb.setFont(UITheme.FONT_BODY);
            cb.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            cb.setOpaque(false);
            cb.putClientProperty("service", s);
            cb.addActionListener(e -> updateEstimate());
            serviceCheckBoxes.add(cb);
            servicesBox.add(cb);
            servicesBox.add(Box.createVerticalStrut(4));
        }

        JScrollPane scrollServices = new JScrollPane(servicesBox);
        scrollServices.setPreferredSize(new Dimension(0, 140));
        scrollServices.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD_BORDER));
        body.add(scrollServices);
        body.add(Box.createVerticalStrut(10));

        // Notes
        body.add(createFieldLabel("Special Instructions / Client Notes:"));
        body.add(Box.createVerticalStrut(4));
        txtNotes = new ModernTextField("e.g. Low skin fade, beard steam, green tea");
        body.add(txtNotes);

        card.add(body, BorderLayout.CENTER);

        // Footer & Actions
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        lblTotalEstimate = new JLabel("Estimated Total: ₹0.00");
        lblTotalEstimate.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalEstimate.setForeground(UITheme.COLOR_BROWN_MED);
        footer.add(lblTotalEstimate, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        ModernButton btnBook = new ModernButton("Confirm Booking", ModernButton.ButtonStyle.ACCENT);
        btnBook.addActionListener(e -> bookAppointment());

        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        btnCancel.addActionListener(e -> dispose());

        btnRow.add(btnBook);
        btnRow.add(btnCancel);
        footer.add(btnRow, BorderLayout.EAST);

        card.add(footer, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        add(root);
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        return l;
    }

    private void updateEstimate() {
        double total = 0.0;
        for (JCheckBox cb : serviceCheckBoxes) {
            if (cb.isSelected()) {
                ServiceItem s = (ServiceItem) cb.getClientProperty("service");
                total += s.getPrice();
            }
        }
        lblTotalEstimate.setText(String.format("Estimated Total: ₹%.2f", total));
    }

    private void bookAppointment() {
        String phone = txtPhone.getText().trim();
        String name = txtName.getText().trim();

        if (phone.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer mobile number and name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> chosenServices = new ArrayList<>();
        double total = 0.0;
        for (JCheckBox cb : serviceCheckBoxes) {
            if (cb.isSelected()) {
                ServiceItem s = (ServiceItem) cb.getClientProperty("service");
                chosenServices.add(s.getName());
                total += s.getPrice();
            }
        }

        if (chosenServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service.", "Service Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Barber b = (Barber) comboBarber.getSelectedItem();
        if (b == null) return;

        Customer cust = DataStore.getInstance().findOrCreateCustomer(name, phone);

        Appointment appt = new Appointment();
        appt.setCustomerId(cust.getId());
        appt.setCustomerName(name);
        appt.setCustomerPhone(phone);
        appt.setBarberId(b.getId());
        appt.setBarberName(b.getName());
        appt.setAppointmentDate(new Date(System.currentTimeMillis()));
        appt.setAppointmentTime((String) comboTime.getSelectedItem());
        appt.setServicesRequested(String.join(", ", chosenServices));
        appt.setEstimatedAmount(total);
        appt.setStatus("SCHEDULED");
        appt.setNotes(txtNotes.getText().trim());

        DataStore.getInstance().addAppointment(appt);

        JOptionPane.showMessageDialog(this, "Appointment scheduled successfully for " + name + " with " + b.getName() + " at " + comboTime.getSelectedItem() + "!", "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

        if (onSuccessCallback != null) {
            onSuccessCallback.run();
        }
        dispose();
    }
}
