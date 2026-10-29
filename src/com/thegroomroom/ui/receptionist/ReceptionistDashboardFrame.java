package com.thegroomroom.ui.receptionist;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.*;
import com.thegroomroom.ui.auth.LoginFrame;
import com.thegroomroom.ui.components.*;
import com.thegroomroom.ui.dialogs.AddAppointmentDialog;
import com.thegroomroom.ui.dialogs.DatabaseSettingsDialog;
import com.thegroomroom.ui.dialogs.InvoiceReceiptDialog;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ReceptionistDashboardFrame extends JFrame {
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Sidebar buttons
    private JButton btnNavAppointments;
    private JButton btnNavPos;
    private JButton btnNavChairs;
    private JButton btnNavCustomers;

    // Appointments Panel
    private ModernTable tableAppointments;

    // POS Billing Panel Controls
    private ModernTextField txtPosPhone;
    private ModernTextField txtPosName;
    private JComboBox<Barber> comboPosBarber;
    private JComboBox<String> comboPosPayment;
    private ModernTextField txtPosDiscount;
    private List<JCheckBox> posServiceChecks = new ArrayList<>();
    private JLabel lblPosSubtotal;
    private JLabel lblPosGst;
    private JLabel lblPosGrandTotal;

    // Chairs Monitor Panel
    private JPanel chairsGridPanel;

    // Customers Table
    private ModernTable tableCustomers;

    public ReceptionistDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("The Groom Room - Front Desk & Billing POS (Receptionist)");
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_CANVAS_BG);
        setLayout(new BorderLayout());

        initUI();
        refreshAllData();
    }

    private void initUI() {
        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.COLOR_CANVAS_BG);

        mainContentPanel.add(createAppointmentsPanel(), "APPOINTMENTS");
        mainContentPanel.add(createPosBillingPanel(), "POS_BILLING");
        mainContentPanel.add(createChairsPanel(), "CHAIRS");
        mainContentPanel.add(createCustomersPanel(), "CUSTOMERS");

        add(mainContentPanel, BorderLayout.CENTER);
    }
   
    private JPanel createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout()) {

            private BufferedImage backgroundImage;

            {
                try {
                    backgroundImage = ImageIO.read(
                        new File("src/com/thegroomroom/resources/images/reception.png")
                    );
                } catch (Exception e) {
                    System.err.println("Could not load receptionist sidebar image.");
                    e.printStackTrace();
                }
            }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                g.drawImage(
                    backgroundImage,
                    0, 0,
                    getWidth(), getHeight(),
                    this
                );

                // Dark overlay to keep text readable
                g.setColor(new Color(20, 12, 8, 145));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    };

    sidebar.setPreferredSize(new Dimension(250, 0));
    sidebar.setBackground(UITheme.COLOR_SIDEBAR_BG);
    sidebar.setBorder(BorderFactory.createMatteBorder(
        0, 0, 0, 1, UITheme.COLOR_PRIMARY_DARK
    ));


        // Brand
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel lblLogo = new JLabel("THE GROOM ROOM");
        lblLogo.setFont(UITheme.FONT_LOGO);
        lblLogo.setForeground(UITheme.COLOR_TEXT_GOLD);

        JLabel lblTag = new JLabel("FRONT DESK & POS");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(UITheme.COLOR_TEXT_LIGHT);

        JPanel roleBadgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        roleBadgeWrapper.setOpaque(false);
        roleBadgeWrapper.add(new Badge("RECEPTIONIST", UITheme.COLOR_INFO_BG, UITheme.COLOR_INFO));

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(lblTag);
        brandPanel.add(roleBadgeWrapper);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        btnNavAppointments = createNavButton("Front Desk Schedule", "APPOINTMENTS", true);
        btnNavPos = createNavButton("Quick Walk-In & Bill", "POS_BILLING", false);
        btnNavChairs = createNavButton("Live Chair Monitor", "CHAIRS", false);
        btnNavCustomers = createNavButton("Client Directory", "CUSTOMERS", false);

        menuPanel.add(btnNavAppointments);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavPos);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavChairs);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavCustomers);
        menuPanel.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.COLOR_SIDEBAR_ACTIVE);
        menuPanel.add(sep);
        menuPanel.add(Box.createVerticalStrut(12));

        JButton btnDb = createNavButton("Database Config", "DB_MODAL", false);
        btnDb.addActionListener(e -> {
            new DatabaseSettingsDialog(this).setVisible(true);
            refreshAllData();
        });
        menuPanel.add(btnDb);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Bottom User
        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 20, 16));

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel lblUserName = new JLabel(currentUser != null ? currentUser.getFullName() : "Receptionist");
        lblUserName.setFont(UITheme.FONT_BODY_BOLD);
        lblUserName.setForeground(Color.WHITE);

        JLabel lblUserEmail = new JLabel("Front Desk Counter 1");
        lblUserEmail.setFont(UITheme.FONT_SMALL);
        lblUserEmail.setForeground(UITheme.COLOR_TEXT_LIGHT);

        userInfo.add(lblUserName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(lblUserEmail);

        ModernButton btnLogout = new ModernButton("Logout", ModernButton.ButtonStyle.OUTLINE);
        btnLogout.setFont(UITheme.FONT_SMALL_BOLD);
        btnLogout.setMargin(new Insets(6, 10, 6, 10));
        btnLogout.addActionListener(e -> logout());

        userPanel.add(userInfo, BorderLayout.CENTER);
        userPanel.add(btnLogout, BorderLayout.EAST);

        sidebar.add(userPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String title, String cardKey, boolean isActive) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);
                boolean active = getClientProperty("active") == Boolean.TRUE;
                boolean hover = getModel().isRollover();

                if (active) {
                    g2.setColor(UITheme.COLOR_SIDEBAR_ACTIVE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(UITheme.COLOR_ACCENT_GOLD);
                    g2.fillRect(0, 4, 4, getHeight() - 8);
                } else if (hover) {
                    g2.setColor(UITheme.COLOR_SIDEBAR_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setForeground(isActive ? UITheme.COLOR_ACCENT_GOLD : UITheme.COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(230, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("active", isActive);

        if (!"DB_MODAL".equals(cardKey)) {
            btn.addActionListener(e -> {
                setActiveNav(btn);
                cardLayout.show(mainContentPanel, cardKey);
            });
        }

        return btn;
    }

    private void setActiveNav(JButton target) {
        JButton[] buttons = {btnNavAppointments, btnNavPos, btnNavChairs, btnNavCustomers};
        for (JButton b : buttons) {
            boolean isCurrent = (b == target);
            b.putClientProperty("active", isCurrent);
            b.setForeground(isCurrent ? UITheme.COLOR_ACCENT_GOLD : UITheme.COLOR_TEXT_LIGHT);
            b.repaint();
        }
    }

    // ==========================================
    // 1. APPOINTMENTS SCHEDULE PANEL
    // ==========================================
    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Today's Appointments & Queue");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        ModernButton btnNewAppt = new ModernButton("+ New Appointment", ModernButton.ButtonStyle.ACCENT);
        btnNewAppt.addActionListener(e -> new AddAppointmentDialog(this, this::refreshAllData).setVisible(true));

        ModernButton btnCheckin = new ModernButton("Seat Client in Chair", ModernButton.ButtonStyle.PRIMARY);
        btnCheckin.addActionListener(e -> seatClientInChair());

        ModernButton btnCheckout = new ModernButton("Generate Bill & Invoice", ModernButton.ButtonStyle.SECONDARY);
        btnCheckout.addActionListener(e -> checkoutSelectedAppointment());

        btnRow.add(btnNewAppt);
        btnRow.add(btnCheckin);
        btnRow.add(btnCheckout);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(btnRow, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableAppointments = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Slot Time", "Client Name", "Phone (+91)", "Stylist", "Services Requested", "Estimate (₹)", "Status", "Notes"}, 0
        ));
        card.add(ModernTable.createModernScrollPane(tableAppointments), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 2. QUICK WALK-IN & BILLING POS PANEL
    // ==========================================
    private JPanel createPosBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        // Header
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel lblTitle = new JLabel("Walk-In Quick Billing & GST Invoicing");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        topRow.add(lblTitle, BorderLayout.WEST);
        panel.add(topRow, BorderLayout.NORTH);

        // Split Layout: Left Form & Services | Right Live Invoice Card
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        // Left: Customer Details & Multi-Service Checklist
        RoundedPanel leftCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        leftCard.setLayout(new BorderLayout(12, 12));
        leftCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel leftBox = new JPanel();
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setOpaque(false);

        JLabel lblCustHead = new JLabel("1. Customer & Stylist Selection");
        lblCustHead.setFont(UITheme.FONT_SECTION);
        lblCustHead.setForeground(UITheme.COLOR_PRIMARY_DARK);
        leftBox.add(lblCustHead);
        leftBox.add(Box.createVerticalStrut(10));

        JPanel custGrid = new JPanel(new GridLayout(2, 2, 10, 8));
        custGrid.setOpaque(false);

        txtPosPhone = new ModernTextField("10-digit Mobile (+91)");
        txtPosName = new ModernTextField("Client Full Name");

        txtPosPhone.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String p = txtPosPhone.getText().trim();
                Customer c = DataStore.getInstance().getCustomerByPhone(p);
                if (c != null) {
                    txtPosName.setText(c.getName());
                }
            }
        });

        custGrid.add(new JLabel("Customer Mobile:"));
        custGrid.add(new JLabel("Customer Name:"));
        custGrid.add(txtPosPhone);
        custGrid.add(txtPosName);
        leftBox.add(custGrid);
        leftBox.add(Box.createVerticalStrut(12));

        JLabel lblBarberHead = new JLabel("Assign Stylist / Barber:");
        lblBarberHead.setFont(UITheme.FONT_BODY_BOLD);
        leftBox.add(lblBarberHead);
        leftBox.add(Box.createVerticalStrut(4));

        comboPosBarber = new JComboBox<>();
        comboPosBarber.setFont(UITheme.FONT_BODY);
        for (Barber b : DataStore.getInstance().getBarbers()) {
            comboPosBarber.addItem(b);
        }
        leftBox.add(comboPosBarber);
        leftBox.add(Box.createVerticalStrut(14));

        JLabel lblServHead = new JLabel("2. Select Grooming Services:");
        lblServHead.setFont(UITheme.FONT_SECTION);
        lblServHead.setForeground(UITheme.COLOR_PRIMARY_DARK);
        leftBox.add(lblServHead);
        leftBox.add(Box.createVerticalStrut(8));

        // Checklist of services
        JPanel checkListPanel = new JPanel();
        checkListPanel.setLayout(new BoxLayout(checkListPanel, BoxLayout.Y_AXIS));
        checkListPanel.setBackground(UITheme.COLOR_CANVAS_BG);
        checkListPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        posServiceChecks.clear();
        for (ServiceItem s : DataStore.getInstance().getServices()) {
            JCheckBox cb = new JCheckBox(s.getName() + " - " + s.getFormattedPrice());
            cb.setFont(UITheme.FONT_BODY);
            cb.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            cb.setOpaque(false);
            cb.putClientProperty("service", s);
            cb.addActionListener(e -> calculatePosBill());
            posServiceChecks.add(cb);
            checkListPanel.add(cb);
            checkListPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane scrollChecks = new JScrollPane(checkListPanel);
        scrollChecks.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD_BORDER));
        scrollChecks.setPreferredSize(new Dimension(0, 200));
        leftBox.add(scrollChecks);

        leftCard.add(leftBox, BorderLayout.CENTER);
        splitPanel.add(leftCard);

        // Right: Live Invoice Summary & Checkout
        RoundedPanel rightCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        rightCard.setLayout(new BorderLayout(14, 14));
        rightCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel rightBox = new JPanel();
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
        rightBox.setOpaque(false);

        JLabel lblInvTitle = new JLabel("Live Invoice Calculation");
        lblInvTitle.setFont(UITheme.FONT_SECTION);
        lblInvTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        rightBox.add(lblInvTitle);
        rightBox.add(Box.createVerticalStrut(14));

        // Price Breakdown
        lblPosSubtotal = createSummaryLabel("Subtotal: ₹0.00", false);
        lblPosGst = createSummaryLabel("Salon GST (5%): ₹0.00", false);

        rightBox.add(lblPosSubtotal);
        rightBox.add(Box.createVerticalStrut(8));
        rightBox.add(lblPosGst);
        rightBox.add(Box.createVerticalStrut(12));

        // Discount input
        JPanel discountRow = new JPanel(new BorderLayout(10, 0));
        discountRow.setOpaque(false);
        JLabel lblDisc = new JLabel("Discount (₹):");
        lblDisc.setFont(UITheme.FONT_BODY_BOLD);
        txtPosDiscount = new ModernTextField("0.00");
        txtPosDiscount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculatePosBill();
            }
        });
        discountRow.add(lblDisc, BorderLayout.WEST);
        discountRow.add(txtPosDiscount, BorderLayout.CENTER);
        rightBox.add(discountRow);
        rightBox.add(Box.createVerticalStrut(14));

        rightBox.add(new JSeparator());
        rightBox.add(Box.createVerticalStrut(12));

        lblPosGrandTotal = new JLabel("GRAND TOTAL: ₹0.00");
        lblPosGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPosGrandTotal.setForeground(UITheme.COLOR_BROWN_MED);
        rightBox.add(lblPosGrandTotal);
        rightBox.add(Box.createVerticalStrut(16));

        // Payment Mode
        JLabel lblPay = new JLabel("Payment Mode:");
        lblPay.setFont(UITheme.FONT_BODY_BOLD);
        rightBox.add(lblPay);
        rightBox.add(Box.createVerticalStrut(4));

        comboPosPayment = new JComboBox<>(new String[]{"UPI (GPay / PhonePe / Paytm)", "CASH", "CARD (Debit / Credit)", "NET_BANKING"});
        comboPosPayment.setFont(UITheme.FONT_BODY);
        rightBox.add(comboPosPayment);

        rightCard.add(rightBox, BorderLayout.CENTER);

        // Bottom Button
        ModernButton btnGenerateBill = new ModernButton("Generate & Print Tax Invoice (₹)", ModernButton.ButtonStyle.ACCENT);
        btnGenerateBill.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGenerateBill.setPreferredSize(new Dimension(0, 48));
        btnGenerateBill.addActionListener(e -> generatePosInvoice());
        rightCard.add(btnGenerateBill, BorderLayout.SOUTH);

        splitPanel.add(rightCard);

        panel.add(splitPanel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createSummaryLabel(String text, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(bold ? UITheme.FONT_BODY_BOLD : UITheme.FONT_BODY);
        l.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        return l;
    }

    private void calculatePosBill() {
        double subtotal = 0.0;
        for (JCheckBox cb : posServiceChecks) {
            if (cb.isSelected()) {
                ServiceItem s = (ServiceItem) cb.getClientProperty("service");
                subtotal += s.getPrice();
            }
        }
        double gst = subtotal * 0.05;
        double discount = 0.0;
        try {
            discount = Double.parseDouble(txtPosDiscount.getText().trim());
        } catch (NumberFormatException ignored) {}

        double grandTotal = Math.max(0.0, subtotal + gst - discount);

        lblPosSubtotal.setText(String.format("Subtotal: ₹%.2f", subtotal));
        lblPosGst.setText(String.format("Salon GST (5%%): ₹%.2f (CGST 2.5%% + SGST 2.5%%)", gst));
        lblPosGrandTotal.setText(String.format("GRAND TOTAL: ₹%.2f", grandTotal));
    }

    private void generatePosInvoice() {
        String phone = txtPosPhone.getText().trim();
        String name = txtPosName.getText().trim();

        if (phone.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer phone and name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<BillItem> chosenItems = new ArrayList<>();
        double subtotal = 0.0;
        for (JCheckBox cb : posServiceChecks) {
            if (cb.isSelected()) {
                ServiceItem s = (ServiceItem) cb.getClientProperty("service");
                chosenItems.add(new BillItem(s.getName(), "SERVICE", 1, s.getPrice()));
                subtotal += s.getPrice();
            }
        }

        if (chosenItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one grooming service.", "Service Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Barber b = (Barber) comboPosBarber.getSelectedItem();
        if (b == null) return;

        double gst = subtotal * 0.05;
        double discount = 0.0;
        try {
            discount = Double.parseDouble(txtPosDiscount.getText().trim());
        } catch (NumberFormatException ignored) {}
        double grandTotal = Math.max(0.0, subtotal + gst - discount);

        Customer c = DataStore.getInstance().findOrCreateCustomer(name, phone);

        String paymentChoice = (String) comboPosPayment.getSelectedItem();
        String payMethod = "UPI";
        if (paymentChoice.contains("CASH")) payMethod = "CASH";
        else if (paymentChoice.contains("CARD")) payMethod = "CARD";
        else if (paymentChoice.contains("NET")) payMethod = "NET_BANKING";

        String billNum = "TGR-" + (2026) + "-" + (1000 + DataStore.getInstance().getBills().size() + 1);

        Bill bill = new Bill();
        bill.setBillNumber(billNum);
        bill.setCustomerId(c.getId());
        bill.setCustomerName(name);
        bill.setCustomerPhone(phone);
        bill.setBarberId(b.getId());
        bill.setBarberName(b.getName());
        bill.setSubtotal(subtotal);
        bill.setTaxPercent(5.0);
        bill.setTaxAmount(gst);
        bill.setDiscountAmount(discount);
        bill.setGrandTotal(grandTotal);
        bill.setPaymentMethod(payMethod);
        bill.setPaymentStatus("PAID");
        bill.setBillDate(new Timestamp(System.currentTimeMillis()));
        for (BillItem item : chosenItems) {
            bill.addItem(item);
        }

        DataStore.getInstance().saveBill(bill);

        // Reset form
        txtPosPhone.setText("");
        txtPosName.setText("");
        txtPosDiscount.setText("0.00");
        for (JCheckBox cb : posServiceChecks) cb.setSelected(false);
        calculatePosBill();

        refreshAllData();

        // Open Invoice Receipt Dialog immediately!
        new InvoiceReceiptDialog(this, bill).setVisible(true);
    }

    // ==========================================
    // 3. CHAIRS LIVE MONITOR PANEL
    // ==========================================
    private JPanel createChairsPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Live Salon Stations & Chair Monitor");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        ModernButton btnRefresh = new ModernButton("Refresh Monitor", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> refreshAllData());

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(btnRefresh, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        chairsGridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        chairsGridPanel.setOpaque(false);

        panel.add(chairsGridPanel, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 4. CUSTOMERS DIRECTORY PANEL
    // ==========================================
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JLabel lblTitle = new JLabel("Customer History & Loyalty Directory");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        panel.add(lblTitle, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableCustomers = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Client Name", "Mobile (+91)", "Visits Count", "Total Spend (₹)", "Last Visit", "Client Notes"}, 0
        ));
        card.add(ModernTable.createModernScrollPane(tableCustomers), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // DATA REFRESH LOGIC
    // ==========================================
    public void refreshAllData() {
        DataStore ds = DataStore.getInstance();

        // 1. Appointments Table
        DefaultTableModel apptModel = (DefaultTableModel) tableAppointments.getModel();
        apptModel.setRowCount(0);
        for (Appointment a : ds.getAppointments()) {
            apptModel.addRow(new Object[]{
                    a.getId(),
                    a.getAppointmentTime(),
                    a.getCustomerName(),
                    a.getCustomerPhone(),
                    a.getBarberName(),
                    a.getServicesRequested(),
                    String.format("₹%.2f", a.getEstimatedAmount()),
                    a.getStatus(),
                    a.getNotes()
            });
        }

        // 2. Chairs Monitor Cards
        chairsGridPanel.removeAll();
        for (Barber b : ds.getBarbers()) {
            RoundedPanel chairCard = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
            chairCard.setLayout(new BorderLayout(10, 10));
            chairCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JPanel cHeader = new JPanel(new BorderLayout());
            cHeader.setOpaque(false);
            JLabel lblChair = new JLabel("CHAIR " + b.getChairNumber());
            lblChair.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblChair.setForeground(UITheme.COLOR_PRIMARY_DARK);

            cHeader.add(lblChair, BorderLayout.WEST);
            cHeader.add(Badge.ofStatus(b.getStatus()), BorderLayout.EAST);
            chairCard.add(cHeader, BorderLayout.NORTH);

            JPanel cBody = new JPanel();
            cBody.setLayout(new BoxLayout(cBody, BoxLayout.Y_AXIS));
            cBody.setOpaque(false);

            JLabel lblBarberName = new JLabel("Stylist: " + b.getName());
            lblBarberName.setFont(UITheme.FONT_BODY_BOLD);
            lblBarberName.setForeground(UITheme.COLOR_TEXT_PRIMARY);

            JLabel lblSpec = new JLabel(b.getSpecialization());
            lblSpec.setFont(UITheme.FONT_SMALL);
            lblSpec.setForeground(UITheme.COLOR_TEXT_SECONDARY);

            cBody.add(lblBarberName);
            cBody.add(Box.createVerticalStrut(3));
            cBody.add(lblSpec);
            cBody.add(Box.createVerticalStrut(10));

            // Check if there's an active appointment in chair
            Appointment inChairAppt = null;
            for (Appointment a : ds.getAppointments()) {
                if (a.getBarberId() == b.getId() && "IN_CHAIR".equals(a.getStatus())) {
                    inChairAppt = a;
                    break;
                }
            }

            if (inChairAppt != null) {
                JPanel activePanel = new JPanel();
                activePanel.setLayout(new BoxLayout(activePanel, BoxLayout.Y_AXIS));
                activePanel.setBackground(UITheme.COLOR_WARNING_BG);
                activePanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

                JLabel lblCur = new JLabel("Current Client: " + inChairAppt.getCustomerName());
                lblCur.setFont(UITheme.FONT_BODY_BOLD);
                lblCur.setForeground(UITheme.COLOR_WARNING);

                JLabel lblSrv = new JLabel("Services: " + inChairAppt.getServicesRequested());
                lblSrv.setFont(UITheme.FONT_SMALL);
                lblSrv.setForeground(UITheme.COLOR_TEXT_PRIMARY);

                activePanel.add(lblCur);
                activePanel.add(lblSrv);
                cBody.add(activePanel);
            } else {
                JLabel lblFree = new JLabel("Station Ready for next customer");
                lblFree.setFont(UITheme.FONT_SMALL);
                lblFree.setForeground(UITheme.COLOR_SUCCESS);
                cBody.add(lblFree);
            }

            chairCard.add(cBody, BorderLayout.CENTER);
            chairsGridPanel.add(chairCard);
        }
        chairsGridPanel.revalidate();
        chairsGridPanel.repaint();

        // 3. Customers Table
        DefaultTableModel custModel = (DefaultTableModel) tableCustomers.getModel();
        custModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        for (Customer c : ds.getCustomers()) {
            String lastVisit = c.getLastVisit() != null ? sdf.format(c.getLastVisit()) : "Never";
            custModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getVisitsCount(),
                    String.format("₹%.2f", c.getTotalSpend()),
                    lastVisit,
                    c.getNotes()
            });
        }
    }

    private void seatClientInChair() {
        int row = tableAppointments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table.", "Select Appointment", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableAppointments.getValueAt(row, 0);
        for (Appointment a : DataStore.getInstance().getAppointments()) {
            if (a.getId() == id) {
                DataStore.getInstance().updateAppointmentStatus(id, "IN_CHAIR");
                DataStore.getInstance().updateBarberStatus(a.getBarberId(), "IN_SERVICE");
                refreshAllData();
                JOptionPane.showMessageDialog(this, a.getCustomerName() + " is now seated in chair with " + a.getBarberName() + "!", "Client Seated", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    private void checkoutSelectedAppointment() {
        int row = tableAppointments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table.", "Select Appointment", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableAppointments.getValueAt(row, 0);
        for (Appointment a : DataStore.getInstance().getAppointments()) {
            if (a.getId() == id) {
                // Switch to POS tab and auto-fill!
                txtPosPhone.setText(a.getCustomerPhone());
                txtPosName.setText(a.getCustomerName());
                for (int i = 0; i < comboPosBarber.getItemCount(); i++) {
                    Barber b = comboPosBarber.getItemAt(i);
                    if (b.getId() == a.getBarberId()) {
                        comboPosBarber.setSelectedIndex(i);
                        break;
                    }
                }
                // Mark in pos checkboxes
                for (JCheckBox cb : posServiceChecks) {
                    ServiceItem s = (ServiceItem) cb.getClientProperty("service");
                    if (a.getServicesRequested().contains(s.getName())) {
                        cb.setSelected(true);
                    }
                }
                calculatePosBill();
                DataStore.getInstance().updateAppointmentStatus(id, "COMPLETED");
                DataStore.getInstance().updateBarberStatus(a.getBarberId(), "AVAILABLE");

                setActiveNav(btnNavPos);
                cardLayout.show(mainContentPanel, "POS_BILLING");
                return;
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout from Front Desk?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
