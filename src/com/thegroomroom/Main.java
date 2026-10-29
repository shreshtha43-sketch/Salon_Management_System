package com.thegroomroom;

import com.thegroomroom.dao.DataStore;
import com.thegroomroom.ui.auth.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Enable high-DPI scaling & anti-aliased font rendering
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Use standard clean cross-platform or system UI look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Pre-initialize data store (checks MySQL and loads seed data)
        DataStore.getInstance();

        // Launch on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
