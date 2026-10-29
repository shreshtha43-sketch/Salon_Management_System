
package com.thegroomroom.ui.auth;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.model.User;
import com.thegroomroom.ui.admin.AdminDashboardFrame;
import com.thegroomroom.ui.barber.BarberDashboardFrame;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernPasswordField;
import com.thegroomroom.ui.components.ModernTextField;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.dialogs.DatabaseSettingsDialog;
import com.thegroomroom.ui.receptionist.ReceptionistDashboardFrame;
import com.thegroomroom.ui.theme.UITheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoginFrame extends JFrame {

    private String selectedRole = "ADMIN";

    private JButton btnTabAdmin;
    private JButton btnTabReception;
    private JButton btnTabBarber;

    private JLabel lblRoleTitle;
    private JLabel lblRoleDesc;
    private ModernTextField txtUsername;
    private ModernPasswordField txtPassword;
    private JLabel lblError;
    private JLabel lblDbStatus;

    public LoginFrame() {
        setTitle("The Groom Room - Luxury Salon Management System");
        setSize(1120, 700);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_CANVAS_BG);
        setLayout(new BorderLayout());

        initUI();
        selectRole("ADMIN");
        updateDbStatus();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0));
        root.setOpaque(true);
        root.setBackground(new Color(248, 244, 239));

        root.add(createImagePanel());
        root.add(createRightSide());

        add(root, BorderLayout.CENTER);
    }

    /**
     * Static salon image panel.
     * The image fills the entire left panel while maintaining
     * its aspect ratio. Excess edges are cropped if necessary.
     */
    private JPanel createImagePanel() {
        return new JPanel() {

            private BufferedImage heroImage;

            {
                setBackground(new Color(25, 17, 13));

                File imageFile = new File(
                    "src/com/thegroomroom/resources/images/hairslaon.png"
                );

                try {
                    if (imageFile.isFile()) {
                        heroImage = ImageIO.read(imageFile);
                    } else {
                        System.err.println(
                            "Hero image not found: "
                            + imageFile.getAbsolutePath()
                        );
                    }
                } catch (Exception ex) {
                    System.err.println(
                        "Could not load hero image: "
                        + ex.getMessage()
                    );
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                try {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

                    g2.setColor(new Color(25, 17, 13));
                    g2.fillRect(0, 0, panelWidth, panelHeight);

                    if (heroImage == null) {
                        g2.setColor(new Color(205, 151, 91));
                        g2.setFont(
                            new Font("Segoe UI", Font.BOLD, 25)
                        );

                        String text = "THE GROOM ROOM";

                        int textWidth = g2.getFontMetrics()
                            .stringWidth(text);

                        g2.drawString(
                            text,
                            (panelWidth - textWidth) / 2,
                            panelHeight / 2
                        );

                        return;
                    }

                    g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC
                    );

                    g2.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY
                    );

                    int imageWidth = heroImage.getWidth();
                    int imageHeight = heroImage.getHeight();

                    // Scale enough to cover the entire panel.
                    double scale = Math.max(
                        (double) panelWidth / imageWidth,
                        (double) panelHeight / imageHeight
                    );

                    int scaledWidth = (int) (imageWidth * scale);
                    int scaledHeight = (int) (imageHeight * scale);

                    // Center the image and crop excess edges.
                    int x = (panelWidth - scaledWidth) / 2;
                    int y = (panelHeight - scaledHeight) / 2;

                    g2.drawImage(
                        heroImage,
                        x,
                        y,
                        scaledWidth,
                        scaledHeight,
                        this
                    );

                } finally {
                    g2.dispose();
                }
            }
        };
    }

    private JPanel createRightSide() {
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setBackground(new Color(248, 244, 239));
        right.setBorder(
            BorderFactory.createEmptyBorder(18, 24, 16, 24)
        );

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(
            new BoxLayout(brandPanel, BoxLayout.Y_AXIS)
        );

        JLabel lblBrand = new JLabel("THE GROOM ROOM");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 27));
        lblBrand.setForeground(UITheme.COLOR_TEXT_GOLD);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(
            "PREMIUM INDIAN GROOMING LOUNGE"
        );
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSubtitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMotto = new JLabel(
            "Crafted Style. Timeless Confidence."
        );
        lblMotto.setFont(UITheme.FONT_SMALL);
        lblMotto.setForeground(UITheme.COLOR_BROWN_LIGHT);
        lblMotto.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(lblBrand);
        brandPanel.add(Box.createVerticalStrut(3));
        brandPanel.add(lblSubtitle);
        brandPanel.add(Box.createVerticalStrut(5));
        brandPanel.add(lblMotto);

        right.add(brandPanel, BorderLayout.NORTH);
        right.add(createRightLoginCard(), BorderLayout.CENTER);

        JPanel dbPanel = new JPanel();
        dbPanel.setOpaque(false);
        dbPanel.setLayout(
            new BoxLayout(dbPanel, BoxLayout.Y_AXIS)
        );

        lblDbStatus = new JLabel("● Database: Checking...");
        lblDbStatus.setFont(UITheme.FONT_SMALL_BOLD);
        lblDbStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton btnQuickDb = new ModernButton(
            "Configure Database & Root Password",
            ModernButton.ButtonStyle.OUTLINE
        );

        btnQuickDb.setFont(UITheme.FONT_SMALL_BOLD);
        btnQuickDb.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnQuickDb.addActionListener(e -> {
            new DatabaseSettingsDialog(this).setVisible(true);
            updateDbStatus();
        });

        dbPanel.add(lblDbStatus);
        dbPanel.add(Box.createVerticalStrut(7));
        dbPanel.add(btnQuickDb);

        right.add(dbPanel, BorderLayout.SOUTH);

        return right;
    }

    private JPanel createRightLoginCard() {
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setBorder(
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        );

        RoundedPanel loginCard = new RoundedPanel(
            18,
            Color.WHITE,
            UITheme.COLOR_CARD_BORDER,
            true
        );

        loginCard.setPreferredSize(new Dimension(420, 490));
        loginCard.setLayout(new BorderLayout(12, 12));
        loginCard.setBorder(
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        JPanel topBox = new JPanel();
        topBox.setLayout(
            new BoxLayout(topBox, BoxLayout.Y_AXIS)
        );
        topBox.setOpaque(false);

        JLabel lblSelectPortal = new JLabel(
            "SELECT PORTAL INTERFACE"
        );
        lblSelectPortal.setFont(UITheme.FONT_SMALL_BOLD);
        lblSelectPortal.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lblSelectPortal.setAlignmentX(Component.CENTER_ALIGNMENT);

        topBox.add(lblSelectPortal);
        topBox.add(Box.createVerticalStrut(8));

        JPanel tabsRow = new JPanel(
            new GridLayout(1, 3, 6, 0)
        );
        tabsRow.setOpaque(false);

        btnTabAdmin = createTabButton("Admin", "ADMIN");
        btnTabReception = createTabButton(
            "Receptionist", "RECEPTIONIST"
        );
        btnTabBarber = createTabButton("Barber", "BARBER");

        tabsRow.add(btnTabAdmin);
        tabsRow.add(btnTabReception);
        tabsRow.add(btnTabBarber);

        topBox.add(tabsRow);
        topBox.add(Box.createVerticalStrut(12));

        RoundedPanel roleInfoCard = new RoundedPanel(
            10,
            UITheme.COLOR_CANVAS_BG,
            UITheme.COLOR_CARD_BORDER,
            false
        );

        roleInfoCard.setLayout(
            new BoxLayout(roleInfoCard, BoxLayout.Y_AXIS)
        );

        roleInfoCard.setBorder(
            BorderFactory.createEmptyBorder(9, 11, 9, 11)
        );

        lblRoleTitle = new JLabel("Administrator Portal");
        lblRoleTitle.setFont(UITheme.FONT_BODY_BOLD);
        lblRoleTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        lblRoleDesc = new JLabel(
            "Access Salon revenue KPIs, staff rosters, "
            + "service catalog & DB settings."
        );
        lblRoleDesc.setFont(UITheme.FONT_SMALL);
        lblRoleDesc.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        roleInfoCard.add(lblRoleTitle);
        roleInfoCard.add(Box.createVerticalStrut(3));
        roleInfoCard.add(lblRoleDesc);

        topBox.add(roleInfoCard);
        loginCard.add(topBox, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(
            new BoxLayout(formPanel, BoxLayout.Y_AXIS)
        );
        formPanel.setOpaque(false);
        formPanel.add(Box.createVerticalStrut(8));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(4));

        txtUsername = new ModernTextField("Enter username");
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(10));

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(4));

        txtPassword = new ModernPasswordField("Enter password");
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(7));

        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL_BOLD);
        lblError.setForeground(UITheme.COLOR_DANGER);

        formPanel.add(lblError);
        loginCard.add(formPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(
            new BoxLayout(actionsPanel, BoxLayout.Y_AXIS)
        );
        actionsPanel.setOpaque(false);

        ModernButton btnLogin = new ModernButton(
            "Sign In to The Groom Room",
            ModernButton.ButtonStyle.PRIMARY
        );

        btnLogin.setFont(UITheme.FONT_BODY_BOLD);
        btnLogin.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );
        btnLogin.addActionListener(e -> attemptLogin());

        ModernButton btnQuickDemo = new ModernButton(
            "Quick One-Click Demo Login",
            ModernButton.ButtonStyle.ACCENT
        );

        btnQuickDemo.setFont(UITheme.FONT_SMALL_BOLD);
        btnQuickDemo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );
        btnQuickDemo.addActionListener(e -> quickDemoLogin());

        actionsPanel.add(btnLogin);
        actionsPanel.add(Box.createVerticalStrut(8));
        actionsPanel.add(btnQuickDemo);

        loginCard.add(actionsPanel, BorderLayout.SOUTH);

        rightWrapper.add(loginCard);

        return rightWrapper;
    }

    private JButton createTabButton(
        String text,
        String roleKey
    ) {
        JButton btn = new JButton(text);

        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 6, 6, 6));

        btn.addActionListener(e -> selectRole(roleKey));

        return btn;
    }

    private void selectRole(String role) {
        selectedRole = role;
        lblError.setText(" ");

        styleTab(btnTabAdmin, "ADMIN".equals(role));
        styleTab(
            btnTabReception,
            "RECEPTIONIST".equals(role)
        );
        styleTab(btnTabBarber, "BARBER".equals(role));

        switch (role) {
            case "RECEPTIONIST":
                lblRoleTitle.setText(
                    "Front Desk & Reception Portal"
                );
                lblRoleDesc.setText(
                    "Book appointments, walk-in billing POS, "
                    + "GST invoices (₹) & chair status."
                );
                txtUsername.setText("reception");
                txtPassword.setText("recep123");
                break;

            case "ADMIN":
                lblRoleTitle.setText("Administrator Portal");
                lblRoleDesc.setText(
                    "Access Salon revenue KPIs, staff rosters, "
                    + "service catalog & DB settings."
                );
                txtUsername.setText("admin");
                txtPassword.setText("admin123");
                break;

            case "BARBER":
                lblRoleTitle.setText(
                    "Stylist & Barber Portal"
                );
                lblRoleDesc.setText(
                    "View your station queue, mark cuts "
                    + "completed & track personal earnings in ₹."
                );
                txtUsername.setText("barber_vikram");
                txtPassword.setText("barber123");
                break;
        }
    }

    private void styleTab(
        JButton btn,
        boolean isSelected
    ) {
        if (isSelected) {
            btn.setBackground(UITheme.COLOR_PRIMARY_DARK);
            btn.setForeground(UITheme.COLOR_ACCENT_GOLD);
            btn.setBorder(
                BorderFactory.createLineBorder(
                    UITheme.COLOR_ACCENT_GOLD, 1
                )
            );
        } else {
            btn.setBackground(UITheme.COLOR_CANVAS_BG);
            btn.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            btn.setBorder(
                BorderFactory.createLineBorder(
                    UITheme.COLOR_CARD_BORDER, 1
                )
            );
        }
    }

    private void updateDbStatus() {
        boolean connected =
            DataStore.getInstance().isMySqlMode();

        if (connected) {
            lblDbStatus.setText(
                "● MySQL Database: Connected (the_groom_room)"
            );
            lblDbStatus.setForeground(UITheme.COLOR_SUCCESS);
        } else {
            lblDbStatus.setText(
                "● Database: In-Memory / Demo (MySQL Ready)"
            );
            lblDbStatus.setForeground(
                UITheme.COLOR_ACCENT_GOLD
            );
        }
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password =
            new String(txtPassword.getPassword());

        if (!username.isEmpty() && !password.isEmpty()) {
            User authenticated =
                DataStore.getInstance().authenticate(
                    username,
                    password,
                    selectedRole
                );

            if (authenticated != null) {
                openDashboardForRole(authenticated);
            } else {
                lblError.setText(
                    "Invalid credentials for "
                    + selectedRole + " portal."
                );
            }
        } else {
            lblError.setText(
                "Please enter username and password."
            );
        }
    }

    private void quickDemoLogin() {
        switch (selectedRole) {
            case "RECEPTIONIST":
                txtUsername.setText("reception");
                txtPassword.setText("recep123");
                break;

            case "ADMIN":
                txtUsername.setText("admin");
                txtPassword.setText("admin123");
                break;

            case "BARBER":
                txtUsername.setText("barber_vikram");
                txtPassword.setText("barber123");
                break;
        }

        attemptLogin();
    }

    private void openDashboardForRole(User user) {
        dispose();

        switch (user.getRole().toUpperCase()) {
            case "RECEPTIONIST":
                new ReceptionistDashboardFrame(user)
                    .setVisible(true);
                return;

            case "ADMIN":
                new AdminDashboardFrame(user).setVisible(true);
                return;

            case "BARBER":
                new BarberDashboardFrame(user).setVisible(true);
                return;

            default:
                new AdminDashboardFrame(user).setVisible(true);
        }
    }
}
