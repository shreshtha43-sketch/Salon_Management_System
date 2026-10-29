package com.thegroomroom.ui.dialogs;

import com.thegroomroom.config.DBConnection;
import com.thegroomroom.config.DatabaseInitializer;
import com.thegroomroom.dao.DataStore;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.ModernPasswordField;
import com.thegroomroom.ui.components.ModernTextField;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class DatabaseSettingsDialog extends JDialog {
    private ModernTextField txtHost;
    private ModernTextField txtPort;
    private ModernTextField txtDbName;
    private ModernTextField txtUser;
    private ModernPasswordField txtPassword;
    private JLabel lblStatusBanner;

    public DatabaseSettingsDialog(Frame parent) {
        super(parent, "MySQL Database Connection & Setup", true);
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_CANVAS_BG);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content Card
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, false);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Header
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Database Configuration");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JLabel lblDesc = new JLabel("Connect to your local or remote MySQL server using your root password.");
        lblDesc.setFont(UITheme.FONT_SMALL);
        lblDesc.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblStatusBanner = new JLabel(DataStore.getInstance().isMySqlMode() ?
                "● Status: Connected to MySQL (the_groom_room)" :
                "● Status: In-Memory / Demo Mode (Ready to connect)");
        lblStatusBanner.setFont(UITheme.FONT_BODY_BOLD);
        lblStatusBanner.setForeground(DataStore.getInstance().isMySqlMode() ? UITheme.COLOR_SUCCESS : UITheme.COLOR_WARNING);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblDesc);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(lblStatusBanner);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(new JSeparator());

        card.add(titlePanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 12, 14));
        formPanel.setOpaque(false);

        txtHost = new ModernTextField("localhost");
        txtHost.setText(DBConnection.getHost());

        txtPort = new ModernTextField("3306");
        txtPort.setText(String.valueOf(DBConnection.getPort()));

        txtDbName = new ModernTextField("the_groom_room");
        txtDbName.setText(DBConnection.getDbName());

        txtUser = new ModernTextField("root");
        txtUser.setText(DBConnection.getUser());

        txtPassword = new ModernPasswordField("Enter MySQL root password");
        txtPassword.setText(DBConnection.getPassword());

        addFormField(formPanel, "MySQL Host:", txtHost);
        addFormField(formPanel, "Port:", txtPort);
        addFormField(formPanel, "Database Name:", txtDbName);
        addFormField(formPanel, "Username:", txtUser);
        addFormField(formPanel, "Password (Root):", txtPassword);

        card.add(formPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionsBox = new JPanel();
        actionsBox.setLayout(new BoxLayout(actionsBox, BoxLayout.Y_AXIS));
        actionsBox.setOpaque(false);

        JPanel rowBtn1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        rowBtn1.setOpaque(false);

        ModernButton btnTest = new ModernButton("Test Connection", ModernButton.ButtonStyle.SECONDARY);
        btnTest.addActionListener(e -> testConnection());

        ModernButton btnInit = new ModernButton("Initialize Database & Tables", ModernButton.ButtonStyle.ACCENT);
        btnInit.addActionListener(e -> initializeDatabaseAction());

        rowBtn1.add(btnTest);
        rowBtn1.add(btnInit);

        JPanel rowBtn2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        rowBtn2.setOpaque(false);

        ModernButton btnSave = new ModernButton("Save Config", ModernButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> saveConfigAction());

        ModernButton btnClose = new ModernButton("Close", ModernButton.ButtonStyle.OUTLINE);
        btnClose.addActionListener(e -> dispose());

        rowBtn2.add(btnSave);
        rowBtn2.add(btnClose);

        actionsBox.add(rowBtn1);
        actionsBox.add(Box.createVerticalStrut(10));
        actionsBox.add(rowBtn2);

        card.add(actionsBox, BorderLayout.SOUTH);

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

    private void testConnection() {
        String host = txtHost.getText().trim();
        int port = 3306;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException ignored) {}
        String user = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        try (Connection conn = DBConnection.getServerConnection(host, port, user, pass)) {
            if (conn != null && !conn.isClosed()) {
                lblStatusBanner.setText("● Status: Connected to MySQL Server successfully!");
                lblStatusBanner.setForeground(UITheme.COLOR_SUCCESS);
                JOptionPane.showMessageDialog(this,
                        "Successfully pinged MySQL Server at " + host + ":" + port + " as user '" + user + "'!\n\nYou can now click 'Initialize Database & Tables' to set up all salon tables.",
                        "Connection Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            lblStatusBanner.setText("● Status: Connection Failed: " + ex.getMessage());
            lblStatusBanner.setForeground(UITheme.COLOR_DANGER);
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to MySQL:\n" + ex.getMessage() + "\n\nPlease ensure MySQL service is running and credentials are correct.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeDatabaseAction() {
        String host = txtHost.getText().trim();
        int port = 3306;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException ignored) {}
        String user = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DatabaseInitializer.InitResult res = DatabaseInitializer.initializeDatabase(host, port, user, pass);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        if (res.success) {
            DBConnection.saveConfig(host, port, txtDbName.getText().trim(), user, pass);
            DataStore.getInstance().checkAndSyncWithDatabase();
            lblStatusBanner.setText("● Status: Connected & Initialized (" + txtDbName.getText().trim() + ")");
            lblStatusBanner.setForeground(UITheme.COLOR_SUCCESS);
            JOptionPane.showMessageDialog(this, res.message, "Database Initialized", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, res.message, "Initialization Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveConfigAction() {
        String host = txtHost.getText().trim();
        int port = 3306;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException ignored) {}
        String dbName = txtDbName.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        DBConnection.saveConfig(host, port, dbName, user, pass);
        boolean synced = DataStore.getInstance().checkAndSyncWithDatabase();
        if (synced) {
            lblStatusBanner.setText("● Status: MySQL Live Connected");
            lblStatusBanner.setForeground(UITheme.COLOR_SUCCESS);
            JOptionPane.showMessageDialog(this, "Configuration saved and synced with MySQL database!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblStatusBanner.setText("● Status: Saved. (MySQL server not reachable yet)");
            lblStatusBanner.setForeground(UITheme.COLOR_WARNING);
            JOptionPane.showMessageDialog(this, "Configuration saved to db_config.properties.\nWill connect automatically when MySQL server is running.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
