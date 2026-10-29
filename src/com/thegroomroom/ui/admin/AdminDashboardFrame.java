package com.thegroomroom.ui.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.Barber;
import com.thegroomroom.model.Bill;
import com.thegroomroom.model.Customer;
import com.thegroomroom.model.InventoryItem;
import com.thegroomroom.model.ServiceItem;
import com.thegroomroom.model.User;
import com.thegroomroom.ui.auth.LoginFrame;
import com.thegroomroom.ui.components.Badge;
import com.thegroomroom.ui.components.KpiCard;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernTable;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.dialogs.AddBarberDialog;
import com.thegroomroom.ui.dialogs.AddInventoryDialog;
import com.thegroomroom.ui.dialogs.AddServiceDialog;
import com.thegroomroom.ui.dialogs.DatabaseSettingsDialog;
import com.thegroomroom.ui.dialogs.InvoiceReceiptDialog;
import com.thegroomroom.ui.theme.UITheme;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AdminDashboardFrame extends JFrame {
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Sidebar buttons
    private JButton btnNavDashboard;
    private JButton btnNavStaff;
    private JButton btnNavServices;
    private JButton btnNavInventory;
    private JButton btnNavCustomers;
    private JButton btnNavDb;

    // Tables
    private ModernTable tableLeaderboard;
    private ModernTable tableRecentBills;
    private ModernTable tableBarbers;
    private ModernTable tableServices;
    private ModernTable tableInventory;
    private ModernTable tableCustomers;

    // KPI Cards
    private KpiCard kpiRevenue;
    private KpiCard kpiBookings;
    private KpiCard kpiStaff;
    private KpiCard kpiTopService;

    public AdminDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("The Groom Room - Luxury Salon Management (Administrator)");
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
        // 1. Sidebar Panel
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Main Content Panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.COLOR_CANVAS_BG);

        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");
        mainContentPanel.add(createStaffPanel(), "STAFF");
        mainContentPanel.add(createServicesPanel(), "SERVICES");
        mainContentPanel.add(createInventoryPanel(), "INVENTORY");
        mainContentPanel.add(createCustomersPanel(), "CUSTOMERS");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    
private JPanel createSidebar() {

    JPanel sidebar = new JPanel(new BorderLayout()) {

        private BufferedImage backgroundImage;

        {
            try {
                backgroundImage = ImageIO.read(
                    new File("src/com/thegroomroom/resources/images/admin.png")
                );
            } catch (Exception e) {
                System.err.println("Sidebar background image could not be loaded.");
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

                // Dark overlay for readable text and buttons
                g.setColor(new Color(20, 12, 8, 155));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    };

    sidebar.setPreferredSize(new Dimension(250, 0));
    sidebar.setBackground(UITheme.COLOR_SIDEBAR_BG);
    sidebar.setBorder(BorderFactory.createMatteBorder(
        0, 0, 0, 1, UITheme.COLOR_PRIMARY_DARK
    ));

    // Keep the rest of your existing createSidebar() code below unchanged.


        // Top Brand Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel lblLogo = new JLabel("THE GROOM ROOM");
        lblLogo.setFont(UITheme.FONT_LOGO);
        lblLogo.setForeground(UITheme.COLOR_TEXT_GOLD);

        JLabel lblTag = new JLabel("SALON MANAGEMENT");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(UITheme.COLOR_TEXT_LIGHT);

        JPanel roleBadgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        roleBadgeWrapper.setOpaque(false);
        roleBadgeWrapper.add(new Badge("ADMINISTRATOR", UITheme.COLOR_ACCENT_MUTED, UITheme.COLOR_BROWN_MED));

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(lblTag);
        brandPanel.add(roleBadgeWrapper);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Menu Nav Items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        btnNavDashboard = createNavButton("Dashboard & Analytics", "DASHBOARD", true);
        btnNavStaff = createNavButton("Staff & Stylists", "STAFF", false);
        btnNavServices = createNavButton("Services & Pricing", "SERVICES", false);
        btnNavInventory = createNavButton("Retail Inventory", "INVENTORY", false);
        btnNavCustomers = createNavButton("Customer Directory", "CUSTOMERS", false);

        menuPanel.add(btnNavDashboard);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavStaff);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavServices);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavInventory);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavCustomers);
        menuPanel.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.COLOR_SIDEBAR_ACTIVE);
        menuPanel.add(sep);
        menuPanel.add(Box.createVerticalStrut(12));

        btnNavDb = createNavButton("Database Config", "DB_MODAL", false);
        btnNavDb.addActionListener(e -> {
            new DatabaseSettingsDialog(this).setVisible(true);
            refreshAllData();
        });
        menuPanel.add(btnNavDb);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Bottom User Profile & Logout
        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 20, 16));

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel lblUserName = new JLabel(currentUser != null ? currentUser.getFullName() : "Administrator");
        lblUserName.setFont(UITheme.FONT_BODY_BOLD);
        lblUserName.setForeground(Color.WHITE);

        JLabel lblUserRole = new JLabel("admin@thegroomroom.in");
        lblUserRole.setFont(UITheme.FONT_SMALL);
        lblUserRole.setForeground(UITheme.COLOR_TEXT_LIGHT);

        userInfo.add(lblUserName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(lblUserRole);

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
        JButton[] buttons = {btnNavDashboard, btnNavStaff, btnNavServices, btnNavInventory, btnNavCustomers};
        for (JButton b : buttons) {
            boolean isCurrent = (b == target);
            b.putClientProperty("active", isCurrent);
            b.setForeground(isCurrent ? UITheme.COLOR_ACCENT_GOLD : UITheme.COLOR_TEXT_LIGHT);
            b.repaint();
        }
    }

    // ==========================================
    // 1. DASHBOARD & ANALYTICS PANEL
    // ==========================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Salon Analytics & Performance");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JLabel lblDate = new JLabel("Live Operations: " + new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new java.util.Date()));
        lblDate.setFont(UITheme.FONT_BODY);
        lblDate.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblDate, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Center Content: Scrollable
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);

        // 1. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setMaximumSize(new Dimension(2000, 105));

        kpiRevenue = new KpiCard("TODAY'S REVENUE", "₹2,470.00", "+18% vs yesterday", "₹");
        kpiBookings = new KpiCard("TOTAL BOOKINGS", "5", "3 Completed, 1 In Chair", "✂");
        kpiStaff = new KpiCard("ACTIVE STYLISTS", "3 On Duty", "1 In Service, 2 Available", "★");
        kpiTopService = new KpiCard("TOP SERVICE", "Royal Hot Towel Shave", "35% of today's total", "♥");

        kpiRow.add(kpiRevenue);
        kpiRow.add(kpiBookings);
        kpiRow.add(kpiStaff);
        kpiRow.add(kpiTopService);

        centerBox.add(kpiRow);
        centerBox.add(Box.createVerticalStrut(18));

        // 2. Middle Row: 7-Day Revenue Bar Chart + Stylist Leaderboard
        JPanel middleRow = new JPanel(new GridLayout(1, 2, 16, 0));
        middleRow.setOpaque(false);
        middleRow.setPreferredSize(new Dimension(0, 240));
        middleRow.setMaximumSize(new Dimension(2000, 260));

        // Custom Java2D Weekly Bar Chart
        RoundedPanel chartCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        chartCard.setLayout(new BorderLayout(10, 10));
        chartCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblChartTitle = new JLabel("Weekly Revenue Trend (₹ INR)");
        lblChartTitle.setFont(UITheme.FONT_SECTION);
        lblChartTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        chartCard.add(lblChartTitle, BorderLayout.NORTH);

        JPanel customBarChart = new JPanel() {
            private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            private final int[] values = {12500, 14200, 11800, 16900, 21400, 28500, 24700};

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.enableAntiAliasing(g2);

                int w = getWidth();
                int h = getHeight() - 30;
                int maxVal = 30000;
                int barWidth = Math.max(24, (w / days.length) - 24);

                for (int i = 0; i < days.length; i++) {
                    int barH = (int) (((double) values[i] / maxVal) * (h - 20));
                    int x = i * (w / days.length) + ((w / days.length) - barWidth) / 2;
                    int y = h - barH;

                    // Bar gradient
                    GradientPaint gp = new GradientPaint(x, y, UITheme.COLOR_ACCENT_GOLD, x, h, UITheme.COLOR_BROWN_MED);
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, barWidth, barH, 8, 8);

                    // Day label
                    g2.setColor(UITheme.COLOR_TEXT_SECONDARY);
                    g2.setFont(UITheme.FONT_SMALL_BOLD);
                    FontMetrics fm = g2.getFontMetrics();
                    int lx = x + (barWidth - fm.stringWidth(days[i])) / 2;
                    g2.drawString(days[i], lx, h + 18);

                    // Price text above bar
                    String amt = "₹" + (values[i] / 1000) + "k";
                    int ax = x + (barWidth - fm.stringWidth(amt)) / 2;
                    g2.drawString(amt, ax, y - 5);
                }
                g2.dispose();
            }
        };
        customBarChart.setOpaque(false);
        chartCard.add(customBarChart, BorderLayout.CENTER);

        // Leaderboard
        RoundedPanel leaderCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        leaderCard.setLayout(new BorderLayout(8, 8));
        leaderCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblLeadTitle = new JLabel("Stylist Leaderboard & Commission");
        lblLeadTitle.setFont(UITheme.FONT_SECTION);
        lblLeadTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        leaderCard.add(lblLeadTitle, BorderLayout.NORTH);

        tableLeaderboard = new ModernTable(new DefaultTableModel(
                new Object[]{"Stylist", "Chair", "Cuts Done", "Comm %", "Est. Commission (₹)"}, 0
        ));
        leaderCard.add(ModernTable.createModernScrollPane(tableLeaderboard), BorderLayout.CENTER);

        middleRow.add(chartCard);
        middleRow.add(leaderCard);

        centerBox.add(middleRow);
        centerBox.add(Box.createVerticalStrut(18));

        // 3. Bottom Row: Recent Invoices Table
        RoundedPanel billCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        billCard.setLayout(new BorderLayout(10, 10));
        billCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel billHeader = new JPanel(new BorderLayout());
        billHeader.setOpaque(false);

        JLabel lblBillTitle = new JLabel("Recent Invoices & Transactions");
        lblBillTitle.setFont(UITheme.FONT_SECTION);
        lblBillTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        ModernButton btnViewBill = new ModernButton("View Invoice Receipt", ModernButton.ButtonStyle.SECONDARY);
        btnViewBill.addActionListener(e -> viewSelectedInvoice());

        billHeader.add(lblBillTitle, BorderLayout.WEST);
        billHeader.add(btnViewBill, BorderLayout.EAST);
        billCard.add(billHeader, BorderLayout.NORTH);

        tableRecentBills = new ModernTable(new DefaultTableModel(
                new Object[]{"Invoice No", "Date", "Customer", "Stylist", "Subtotal", "GST", "Grand Total (₹)", "Payment", "Status"}, 0
        ));
        billCard.add(ModernTable.createModernScrollPane(tableRecentBills), BorderLayout.CENTER);

        centerBox.add(billCard);

        JScrollPane mainScroll = new JScrollPane(centerBox);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(UITheme.COLOR_CANVAS_BG);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(mainScroll, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 2. STAFF & BARBERS PANEL
    // ==========================================
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Staff & Stylists Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        ModernButton btnAddBarber = new ModernButton("+ Add Stylist", ModernButton.ButtonStyle.ACCENT);
        btnAddBarber.addActionListener(e -> new AddBarberDialog(this, this::refreshAllData).setVisible(true));

        ModernButton btnToggleStatus = new ModernButton("Toggle Shift Status", ModernButton.ButtonStyle.SECONDARY);
        btnToggleStatus.addActionListener(e -> toggleBarberStatus());

        btnRow.add(btnAddBarber);
        btnRow.add(btnToggleStatus);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(btnRow, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableBarbers = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Stylist Name", "Specialization", "Chair No", "Commission %", "Phone (+91)", "Total Cuts", "Shift Status"}, 0
        ));
        card.add(ModernTable.createModernScrollPane(tableBarbers), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 3. SERVICES & PRICING PANEL
    // ==========================================
    private JPanel createServicesPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Salon Services Catalog & Pricing");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        ModernButton btnAddService = new ModernButton("+ Add Service", ModernButton.ButtonStyle.ACCENT);
        btnAddService.addActionListener(e -> new AddServiceDialog(this, this::refreshAllData).setVisible(true));

        ModernButton btnDeleteService = new ModernButton("Delete Service", ModernButton.ButtonStyle.DANGER);
        btnDeleteService.addActionListener(e -> deleteSelectedService());

        btnRow.add(btnAddService);
        btnRow.add(btnDeleteService);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(btnRow, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableServices = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Category", "Service Name", "Duration (Mins)", "Price (₹ INR)", "Description"}, 0
        ));
        card.add(ModernTable.createModernScrollPane(tableServices), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 4. INVENTORY PANEL
    // ==========================================
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Retail Inventory & Salon Supplies");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        ModernButton btnAddInv = new ModernButton("+ Add Product", ModernButton.ButtonStyle.ACCENT);
        btnAddInv.addActionListener(e -> new AddInventoryDialog(this, this::refreshAllData).setVisible(true));

        ModernButton btnRestock = new ModernButton("Restock Selected", ModernButton.ButtonStyle.SECONDARY);
        btnRestock.addActionListener(e -> restockSelectedProduct());

        btnRow.add(btnAddInv);
        btnRow.add(btnRestock);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(btnRow, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableInventory = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Product Name", "Category", "Stock Qty", "Selling Price (₹)", "Cost Price (₹)", "Alert Thresh", "Stock Status"}, 0
        ));
        card.add(ModernTable.createModernScrollPane(tableInventory), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 5. CUSTOMERS PANEL
    // ==========================================
    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Customer Relationship & History");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        panel.add(topRow, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        tableCustomers = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Customer Name", "Mobile (+91)", "Total Visits", "Lifetime Spend (₹)", "Client Preferences & Notes"}, 0
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

        // 1. KPI Calculation
        double todayRev = 0.0;
        for (Bill b : ds.getBills()) {
            todayRev += b.getGrandTotal();
        }
        kpiRevenue.setValue(String.format("₹%.2f", todayRev));
        kpiBookings.setValue(String.valueOf(ds.getAppointments().size()));
        kpiStaff.setValue(ds.getBarbers().size() + " Barbers");

        // 2. Leaderboard Table
        DefaultTableModel leadModel = (DefaultTableModel) tableLeaderboard.getModel();
        leadModel.setRowCount(0);
        for (Barber b : ds.getBarbers()) {
            double estimatedComm = b.getTotalCuts() * 150.0 * (b.getCommissionRate() / 100.0);
            leadModel.addRow(new Object[]{
                    b.getName(),
                    "Chair " + b.getChairNumber(),
                    b.getTotalCuts(),
                    b.getCommissionRate() + "%",
                    String.format("₹%.2f", estimatedComm)
            });
        }

        // 3. Recent Bills Table
        DefaultTableModel billModel = (DefaultTableModel) tableRecentBills.getModel();
        billModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        for (Bill b : ds.getBills()) {
            String time = b.getBillDate() != null ? sdf.format(b.getBillDate()) : "Today";
            billModel.addRow(new Object[]{
                    b.getBillNumber(),
                    time,
                    b.getCustomerName(),
                    b.getBarberName(),
                    String.format("₹%.2f", b.getSubtotal()),
                    String.format("₹%.2f", b.getTaxAmount()),
                    String.format("₹%.2f", b.getGrandTotal()),
                    b.getPaymentMethod(),
                    b.getPaymentStatus()
            });
        }

        // 4. Barbers Table
        DefaultTableModel barbModel = (DefaultTableModel) tableBarbers.getModel();
        barbModel.setRowCount(0);
        for (Barber b : ds.getBarbers()) {
            barbModel.addRow(new Object[]{
                    b.getId(),
                    b.getName(),
                    b.getSpecialization(),
                    b.getChairNumber(),
                    b.getCommissionRate() + "%",
                    b.getPhone(),
                    b.getTotalCuts(),
                    b.getStatus()
            });
        }

        // 5. Services Table
        DefaultTableModel servModel = (DefaultTableModel) tableServices.getModel();
        servModel.setRowCount(0);
        for (ServiceItem s : ds.getServices()) {
            servModel.addRow(new Object[]{
                    s.getId(),
                    s.getCategory(),
                    s.getName(),
                    s.getDurationMinutes(),
                    s.getFormattedPrice(),
                    s.getDescription()
            });
        }

        // 6. Inventory Table
        DefaultTableModel invModel = (DefaultTableModel) tableInventory.getModel();
        invModel.setRowCount(0);
        for (InventoryItem it : ds.getInventory()) {
            invModel.addRow(new Object[]{
                    it.getId(),
                    it.getProductName(),
                    it.getCategory(),
                    it.getStockQuantity(),
                    String.format("₹%.2f", it.getUnitPrice()),
                    String.format("₹%.2f", it.getCostPrice()),
                    it.getAlertThreshold(),
                    it.isLowStock() ? "LOW_STOCK" : "AVAILABLE"
            });
        }

        // 7. Customers Table
        DefaultTableModel custModel = (DefaultTableModel) tableCustomers.getModel();
        custModel.setRowCount(0);
        for (Customer c : ds.getCustomers()) {
            custModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getVisitsCount(),
                    String.format("₹%.2f", c.getTotalSpend()),
                    c.getNotes()
            });
        }
    }

    private void viewSelectedInvoice() {
        int row = tableRecentBills.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice from the table.", "Select Invoice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String billNo = (String) tableRecentBills.getValueAt(row, 0);
        for (Bill b : DataStore.getInstance().getBills()) {
            if (b.getBillNumber().equals(billNo)) {
                new InvoiceReceiptDialog(this, b).setVisible(true);
                return;
            }
        }
    }

    private void toggleBarberStatus() {
        int row = tableBarbers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a stylist to toggle shift status.", "Select Stylist", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableBarbers.getValueAt(row, 0);
        Barber b = DataStore.getInstance().getBarberById(id);
        if (b != null) {
            String newStatus = "AVAILABLE".equals(b.getStatus()) ? "ON_BREAK" : ("ON_BREAK".equals(b.getStatus()) ? "OFF_DUTY" : "AVAILABLE");
            DataStore.getInstance().updateBarberStatus(id, newStatus);
            refreshAllData();
        }
    }

    private void deleteSelectedService() {
        int row = tableServices.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a service to delete.", "Select Service", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableServices.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this service?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.getInstance().deleteService(id);
            refreshAllData();
        }
    }

    private void restockSelectedProduct() {
        int row = tableInventory.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to restock.", "Select Product", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableInventory.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Enter quantity to add to stock:", "Restock Product", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int addQty = Integer.parseInt(input.trim());
                for (InventoryItem it : DataStore.getInstance().getInventory()) {
                    if (it.getId() == id) {
                        DataStore.getInstance().updateInventoryStock(id, it.getStockQuantity() + addQty);
                        refreshAllData();
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout from Administrator session?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
