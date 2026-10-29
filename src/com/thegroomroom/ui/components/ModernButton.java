package com.thegroomroom.ui.components;

import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    public enum ButtonStyle {
        PRIMARY,    // Deep Walnut/Brown
        ACCENT,     // Warm Cognac Gold
        SECONDARY,  // Soft Ivory/Muted
        OUTLINE,    // Border with transparent BG
        DANGER      // Red
    }

    private ButtonStyle style;
    private int cornerRadius = 10;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        initButton();
    }

    public ModernButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    private void initButton() {
        setFont(UITheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 18, 10, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        switch (style) {
            case ACCENT:
                bg = isPressed ? UITheme.COLOR_BROWN_MED : (isHovered ? UITheme.COLOR_ACCENT_HOVER : UITheme.COLOR_ACCENT_GOLD);
                fg = UITheme.COLOR_PRIMARY_DARK;
                break;
            case SECONDARY:
                bg = isPressed ? UITheme.COLOR_CARD_BORDER : (isHovered ? UITheme.COLOR_HOVER_BG : UITheme.COLOR_ACCENT_MUTED);
                fg = UITheme.COLOR_TEXT_PRIMARY;
                border = UITheme.COLOR_CARD_BORDER;
                break;
            case OUTLINE:
                bg = isHovered ? UITheme.COLOR_HOVER_BG : new Color(0, 0, 0, 0);
                fg = UITheme.COLOR_BROWN_MED;
                border = isHovered ? UITheme.COLOR_BROWN_MED : UITheme.COLOR_CARD_BORDER;
                break;
            case DANGER:
                bg = isPressed ? UITheme.COLOR_DANGER.darker() : (isHovered ? UITheme.COLOR_DANGER.brighter() : UITheme.COLOR_DANGER);
                fg = Color.WHITE;
                break;
            case PRIMARY:
            default:
                bg = isPressed ? UITheme.COLOR_PRIMARY_DARK : (isHovered ? UITheme.COLOR_SIDEBAR_ACTIVE : UITheme.COLOR_SIDEBAR_BG);
                fg = Color.WHITE;
                break;
        }

        // Draw shape
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        // Draw text & icon
        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent() - fm.getDescent();

        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - 1;

        g2.setColor(fg);
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}
