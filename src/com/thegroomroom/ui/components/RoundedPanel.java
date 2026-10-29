package com.thegroomroom.ui.components;

import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private boolean hasShadow;

    public RoundedPanel(int radius, Color bgColor, Color borderColor, boolean shadow) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        this.hasShadow = shadow;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor) {
        this(radius, bgColor, null, false);
    }

    public RoundedPanel(int radius) {
        this(radius, UITheme.COLOR_CARD_BG, UITheme.COLOR_CARD_BORDER, false);
    }

    public void setBackgroundColor(Color c) {
        this.backgroundColor = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        int shadowOffset = hasShadow ? 3 : 0;
        int renderWidth = width - shadowOffset - 1;
        int renderHeight = height - shadowOffset - 1;

        if (hasShadow) {
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(2, 2, renderWidth + 1, renderHeight + 1, cornerRadius, cornerRadius);
        }

        // Draw Background
        g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        g2.fillRoundRect(0, 0, renderWidth, renderHeight, cornerRadius, cornerRadius);

        // Draw Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, renderWidth, renderHeight, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
