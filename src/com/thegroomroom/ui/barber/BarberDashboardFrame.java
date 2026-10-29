package com.thegroomroom.ui.barber;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.Appointment;
import com.thegroomroom.model.Barber;
import com.thegroomroom.model.Bill;
import com.thegroomroom.model.User;
import com.thegroomroom.ui.auth.LoginFrame;
import com.thegroomroom.ui.components.Badge;
import com.thegroomroom.ui.components.KpiCard;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernTable;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.dialogs.DatabaseSettingsDialog;
import com.thegroomroom.ui.theme.UITheme;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BarberDashboardFrame extends JFrame {
    private Barber currentBarber;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Sidebar buttons
    private JButton btnNavStation;
    private JButton btnNavEarnings;
    private JButton btnNavShift;

    // Station Panel components
    private JLabel lblCurrentClient;
    private JLabel lblCurrentServices;
    private JLabel lblCurrentNotes;
    private ModernButton btnStartService;
    private ModernButton btnCompleteService;
    private Appointment activeClientAppointment;
    private ModernTable tableQueue;

    // Earnings Panel components
    private KpiCard kpiCuts;
    private KpiCard kpiCommission;
    private KpiCard kpiTips;
    private ModernTable tableCompletedCuts;

    // Duty Status
    private JLabel lblDutyStatus;
    private JComboBox<String> comboDutyStatus;

    public BarberDashboardFrame(User user) {
    this.currentBarber = DataStore.getInstance()
            .getBarberByUserId(user != null ? user.getId() : 1);

    if (this.currentBarber == null
            && !DataStore.getInstance().getBarbers().isEmpty()) {
        this.currentBarber = DataStore.getInstance().getBarbers().get(0);
    }

        String barberName = currentBarber != null ? currentBarber.getName() : "Stylist";
        int chairNo = currentBarber != null ? currentBarber.getChairNumber() : 1;

        setTitle("The Groom Room - Stylist Portal (" + barberName + " - Chair " + chairNo + ")");
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

        mainContentPanel.add(createStationPanel(), "STATION");
        mainContentPanel.add(createEarningsPanel(), "EARNINGS");
        mainContentPanel.add(createDutyPanel(), "DUTY");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {

            private BufferedImage backgroundImage;

            {
                try {
                    backgroundImage = ImageIO.read(
                        new File("src/com/thegroomroom/resources/images/barber.png")
                    );
                } catch (Exception e) {
                    System.err.println("Could not load barber sidebar image.");
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

                    // Dark overlay for readable sidebar content
                    g.setColor(new Color(20, 12, 8, 135));
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

        JLabel lblTag = new JLabel("STYLIST & BARBER PORTAL");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(UITheme.COLOR_TEXT_LIGHT);

        JPanel roleBadgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        roleBadgeWrapper.setOpaque(false);
        int chair = currentBarber != null ? currentBarber.getChairNumber() : 1;
        roleBadgeWrapper.add(new Badge("CHAIR " + chair + " STYLIST", UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING));

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

        btnNavStation = createNavButton("My Station & Queue", "STATION", true);
        btnNavEarnings = createNavButton("My Earnings & Tips", "EARNINGS", false);
        btnNavShift = createNavButton("Duty & Shift Status", "DUTY", false);

        menuPanel.add(btnNavStation);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavEarnings);
        menuPanel.add(Box.createVerticalStrut(4));
        menuPanel.add(btnNavShift);
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

        String bName = currentBarber != null ? currentBarber.getName() : "Vikram Singh";
        JLabel lblUserName = new JLabel(bName);
        lblUserName.setFont(UITheme.FONT_BODY_BOLD);
        lblUserName.setForeground(Color.WHITE);

        String bSpec = currentBarber != null ? currentBarber.getSpecialization() : "Master Stylist";
        JLabel lblUserSpec = new JLabel(bSpec);
        lblUserSpec.setFont(UITheme.FONT_SMALL);
        lblUserSpec.setForeground(UITheme.COLOR_TEXT_LIGHT);

        userInfo.add(lblUserName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(lblUserSpec);

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
        JButton[] buttons = {btnNavStation, btnNavEarnings, btnNavShift};
        for (JButton b : buttons) {
            boolean isCurrent = (b == target);
            b.putClientProperty("active", isCurrent);
            b.setForeground(isCurrent ? UITheme.COLOR_ACCENT_GOLD : UITheme.COLOR_TEXT_LIGHT);
            b.repaint();
        }
    }

    // ==========================================
    // 1. MY STATION & QUEUE PANEL
    // ==========================================
    private JPanel createStationPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        // Header
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        int chair = currentBarber != null ? currentBarber.getChairNumber() : 1;
        JLabel lblTitle = new JLabel("Chair " + chair + " - Active Customer & Today's Schedule");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        lblDutyStatus = new JLabel("Shift Status: AVAILABLE");
        lblDutyStatus.setFont(UITheme.FONT_BODY_BOLD);
        lblDutyStatus.setForeground(UITheme.COLOR_SUCCESS);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(lblDutyStatus, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);

        // 1. Current Client in Chair Focus Card
        RoundedPanel inChairCard = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        inChairCard.setLayout(new BorderLayout(14, 14));
        inChairCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inChairCard.setMaximumSize(new Dimension(2000, 200));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        JLabel lblInChairTitle = new JLabel("ACTIVE CLIENT IN CHAIR");
        lblInChairTitle.setFont(UITheme.FONT_SMALL_BOLD);
        lblInChairTitle.setForeground(UITheme.COLOR_BROWN_MED);
        cardHeader.add(lblInChairTitle, BorderLayout.WEST);
        inChairCard.add(cardHeader, BorderLayout.NORTH);

        JPanel cardBody = new JPanel(new GridLayout(3, 1, 4, 4));
        cardBody.setOpaque(false);

        lblCurrentClient = new JLabel("No client currently seated.");
        lblCurrentClient.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblCurrentClient.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        lblCurrentServices = new JLabel("Services: None");
        lblCurrentServices.setFont(UITheme.FONT_BODY);
        lblCurrentServices.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblCurrentNotes = new JLabel("Client Notes: -");
        lblCurrentNotes.setFont(UITheme.FONT_SMALL);
        lblCurrentNotes.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        cardBody.add(lblCurrentClient);
        cardBody.add(lblCurrentServices);
        cardBody.add(lblCurrentNotes);
        inChairCard.add(cardBody, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionRow.setOpaque(false);

        btnStartService = new ModernButton("Seat Next & Start Service", ModernButton.ButtonStyle.ACCENT);
        btnStartService.addActionListener(e -> startNextClient());

        btnCompleteService = new ModernButton("Mark Service Completed", ModernButton.ButtonStyle.PRIMARY);
        btnCompleteService.addActionListener(e -> completeActiveService());

        actionRow.add(btnStartService);
        actionRow.add(btnCompleteService);
        inChairCard.add(actionRow, BorderLayout.SOUTH);

        centerBox.add(inChairCard);
        centerBox.add(Box.createVerticalStrut(18));

        // 2. Queue for Today
        RoundedPanel queueCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        queueCard.setLayout(new BorderLayout(10, 10));
        queueCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblQueueTitle = new JLabel("Upcoming Appointments Assigned To You");
        lblQueueTitle.setFont(UITheme.FONT_SECTION);
        lblQueueTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        queueCard.add(lblQueueTitle, BorderLayout.NORTH);

        tableQueue = new ModernTable(new DefaultTableModel(
                new Object[]{"ID", "Scheduled Time", "Client Name", "Phone (+91)", "Requested Services", "Est. Amount (₹)", "Status", "Notes"}, 0
        ));
        queueCard.add(ModernTable.createModernScrollPane(tableQueue), BorderLayout.CENTER);

        centerBox.add(queueCard);

        panel.add(centerBox, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 2. MY EARNINGS & TIPS PANEL
    // ==========================================
    private JPanel createEarningsPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JLabel lblTitle = new JLabel("My Daily Earnings & Commission Tracker");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);

        // KPI Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setMaximumSize(new Dimension(2000, 105));

        kpiCuts = new KpiCard("SERVICES TODAY", "3 Cuts", "Goal: 6 cuts", "✂");
        double commRate = currentBarber != null ? currentBarber.getCommissionRate() : 40.0;
        kpiCommission = new KpiCard("ESTIMATED COMMISSION (" + (int)commRate + "%)", "₹1,020.00", "Updated in real-time", "₹");
        kpiTips = new KpiCard("TIPS COLLECTED", "₹250.00", "Cash & UPI tips", "★");

        kpiRow.add(kpiCuts);
        kpiRow.add(kpiCommission);
        kpiRow.add(kpiTips);

        centerBox.add(kpiRow);
        centerBox.add(Box.createVerticalStrut(18));

        // Completed Cuts Table
        RoundedPanel compCard = new RoundedPanel(14, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        compCard.setLayout(new BorderLayout(10, 10));
        compCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblHistTitle = new JLabel("Completed Services & Commission Breakdown");
        lblHistTitle.setFont(UITheme.FONT_SECTION);
        lblHistTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        compCard.add(lblHistTitle, BorderLayout.NORTH);

        tableCompletedCuts = new ModernTable(new DefaultTableModel(
                new Object[]{"Time", "Client Name", "Services Performed", "Bill Total (₹)", "Commission %", "Your Payout (₹)", "Status"}, 0
        ));
        compCard.add(ModernTable.createModernScrollPane(tableCompletedCuts), BorderLayout.CENTER);

        centerBox.add(compCard);
        panel.add(centerBox, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 3. DUTY & SHIFT STATUS PANEL
    // ==========================================
    private JPanel createDutyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JLabel lblTitle = new JLabel("Shift Schedule & Duty Status");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);
        panel.add(lblTitle, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel lblH = new JLabel("Change Your Station Availability");
        lblH.setFont(UITheme.FONT_SECTION);
        lblH.setForeground(UITheme.COLOR_PRIMARY_DARK);
        card.add(lblH);
        card.add(Box.createVerticalStrut(6));

        JLabel lblD = new JLabel("Notify Front Desk / Reception when you step out for lunch or conclude your shift.");
        lblD.setFont(UITheme.FONT_BODY);
        lblD.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        card.add(lblD);
        card.add(Box.createVerticalStrut(16));

        comboDutyStatus = new JComboBox<>(new String[]{"AVAILABLE", "IN_SERVICE", "ON_BREAK", "OFF_DUTY"});
        comboDutyStatus.setFont(UITheme.FONT_BODY);
        comboDutyStatus.setMaximumSize(new Dimension(300, 40));
        if (currentBarber != null) {
            comboDutyStatus.setSelectedItem(currentBarber.getStatus());
        }
        card.add(comboDutyStatus);
        card.add(Box.createVerticalStrut(16));

        ModernButton btnUpdateShift = new ModernButton("Update Shift Status", ModernButton.ButtonStyle.ACCENT);
        btnUpdateShift.addActionListener(e -> {
            String newStatus = (String) comboDutyStatus.getSelectedItem();
            if (currentBarber != null) {
                DataStore.getInstance().updateBarberStatus(currentBarber.getId(), newStatus);
                currentBarber.setStatus(newStatus);
                lblDutyStatus.setText("Shift Status: " + newStatus);
                JOptionPane.showMessageDialog(this, "Shift status updated to: " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card.add(btnUpdateShift);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // REFRESH DATA & ACTIONS
    // ==========================================
    public void refreshAllData() {
        DataStore ds = DataStore.getInstance();
        if (currentBarber == null) return;

        // Check if there's an active in-chair appointment
        activeClientAppointment = null;
        for (Appointment a : ds.getAppointments()) {
            if (a.getBarberId() == currentBarber.getId() && "IN_CHAIR".equals(a.getStatus())) {
                activeClientAppointment = a;
                break;
            }
        }

        if (activeClientAppointment != null) {
            lblCurrentClient.setText("In Chair: " + activeClientAppointment.getCustomerName() + " (" + activeClientAppointment.getCustomerPhone() + ")");
            lblCurrentServices.setText("Services: " + activeClientAppointment.getServicesRequested());
            lblCurrentNotes.setText("Client Instructions: " + (activeClientAppointment.getNotes().isEmpty() ? "Standard Cut" : activeClientAppointment.getNotes()));
            btnStartService.setEnabled(false);
            btnCompleteService.setEnabled(true);
        } else {
            lblCurrentClient.setText("No client currently seated.");
            lblCurrentServices.setText("Services: -");
            lblCurrentNotes.setText("Client Instructions: -");
            btnStartService.setEnabled(true);
            btnCompleteService.setEnabled(false);
        }

        // Queue Table
        DefaultTableModel queueModel = (DefaultTableModel) tableQueue.getModel();
        queueModel.setRowCount(0);
        for (Appointment a : ds.getAppointments()) {
            if (a.getBarberId() == currentBarber.getId()) {
                queueModel.addRow(new Object[]{
                        a.getId(),
                        a.getAppointmentTime(),
                        a.getCustomerName(),
                        a.getCustomerPhone(),
                        a.getServicesRequested(),
                        String.format("₹%.2f", a.getEstimatedAmount()),
                        a.getStatus(),
                        a.getNotes()
                });
            }
        }

        // Earnings
        double myTotalSales = 0.0;
        int completedCount = 0;
        DefaultTableModel compModel = (DefaultTableModel) tableCompletedCuts.getModel();
        compModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        for (Bill b : ds.getBills()) {
            if (b.getBarberId() == currentBarber.getId()) {
                completedCount++;
                double payout = b.getSubtotal() * (currentBarber.getCommissionRate() / 100.0);
                myTotalSales += payout;
                compModel.addRow(new Object[]{
                        b.getBillDate() != null ? sdf.format(b.getBillDate()) : "Today",
                        b.getCustomerName(),
                        "Salon Services",
                        String.format("₹%.2f", b.getSubtotal()),
                        currentBarber.getCommissionRate() + "%",
                        String.format("₹%.2f", payout),
                        "PAID"
                });
            }
        }

        kpiCuts.setValue(completedCount + " Cuts");
        kpiCommission.setValue(String.format("₹%.2f", myTotalSales));
    }

    private void startNextClient() {
        DataStore ds = DataStore.getInstance();
        Appointment next = null;
        for (Appointment a : ds.getAppointments()) {
            if (a.getBarberId() == currentBarber.getId() && "SCHEDULED".equals(a.getStatus())) {
                next = a;
                break;
            }
        }
        if (next == null) {
            JOptionPane.showMessageDialog(this, "No scheduled appointments currently in queue for your chair.", "Queue Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ds.updateAppointmentStatus(next.getId(), "IN_CHAIR");
        ds.updateBarberStatus(currentBarber.getId(), "IN_SERVICE");
        refreshAllData();
        JOptionPane.showMessageDialog(this, "Client " + next.getCustomerName() + " is now in your chair. You may begin the service!", "Service Started", JOptionPane.INFORMATION_MESSAGE);
    }

    private void completeActiveService() {
        if (activeClientAppointment == null) return;
        DataStore ds = DataStore.getInstance();
        ds.updateAppointmentStatus(activeClientAppointment.getId(), "COMPLETED");
        ds.updateBarberStatus(currentBarber.getId(), "AVAILABLE");
        refreshAllData();
        JOptionPane.showMessageDialog(this, "Service marked as COMPLETED! Front desk notified for invoicing and payment collection.", "Service Finished", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout from Stylist Portal?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
