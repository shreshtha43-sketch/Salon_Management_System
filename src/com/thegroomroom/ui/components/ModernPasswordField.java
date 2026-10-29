package com.thegroomroom.ui.components;

import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernPasswordField extends JPasswordField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 8;

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        initField();
    }

    public ModernPasswordField() {
        this("");
    }

    private void initField() {
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.COLOR_TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setCaretColor(UITheme.COLOR_PRIMARY_DARK);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Border / Glow
        if (isFocused) {
            g2.setColor(UITheme.COLOR_ACCENT_GOLD);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(UITheme.COLOR_CARD_BORDER);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.dispose();

        super.paintComponent(g);

        // Draw Placeholder if empty and not focused
        if (getPassword().length == 0 && !placeholder.isEmpty() && !isFocused) {
            Graphics2D gPlaceholder = (Graphics2D) g.create();
            UITheme.enableAntiAliasing(gPlaceholder);
            gPlaceholder.setColor(UITheme.COLOR_TEXT_SECONDARY);
            gPlaceholder.setFont(getFont());
            Insets insets = getInsets();
            FontMetrics fm = gPlaceholder.getFontMetrics();
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            gPlaceholder.drawString(placeholder, insets.left, y);
            gPlaceholder.dispose();
        }
    }
}
