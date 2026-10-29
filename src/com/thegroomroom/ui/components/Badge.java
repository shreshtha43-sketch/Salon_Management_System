package com.thegroomroom.ui.components;

import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

public class Badge extends JLabel {
    private Color bgColor;
    private Color fgColor;
    private int cornerRadius = 12;

    public Badge(String text, Color bgColor, Color fgColor) {
        super(text, SwingConstants.CENTER);
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        setFont(UITheme.FONT_SMALL_BOLD);
        setForeground(fgColor);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    public static Badge ofStatus(String status) {
        if (status == null) status = "UNKNOWN";
        String normalized = status.toUpperCase().replace(" ", "_");

        switch (normalized) {
            case "AVAILABLE":
            case "COMPLETED":
            case "PAID":
            case "ACTIVE":
                return new Badge(status.replace("_", " "), UITheme.COLOR_SUCCESS_BG, UITheme.COLOR_SUCCESS);

            case "IN_CHAIR":
            case "IN_SERVICE":
            case "ON_BREAK":
            case "LOW_STOCK":
                return new Badge(status.replace("_", " "), UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING);

            case "SCHEDULED":
            case "RECEPTIONIST":
                return new Badge(status.replace("_", " "), UITheme.COLOR_INFO_BG, UITheme.COLOR_INFO);

            case "ADMIN":
                return new Badge("ADMIN", UITheme.COLOR_ACCENT_MUTED, UITheme.COLOR_BROWN_MED);

            case "BARBER":
                return new Badge("BARBER", UITheme.COLOR_INFO_BG, UITheme.COLOR_BROWN_MED);

            case "CANCELLED":
            case "OFF_DUTY":
            case "REFUNDED":
            default:
                return new Badge(status.replace("_", " "), UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Subtle border
        g2.setColor(fgColor);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.dispose();

        super.paintComponent(g);
    }
}
