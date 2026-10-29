
package com.thegroomroom.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thegroomroom.ui.theme.UITheme;

public class KpiCard extends RoundedPanel {
    private JLabel lblTitle;
    private JLabel lblValue;
    private JLabel lblSubtitle;

    public KpiCard(String title, String value, String subtitle, String iconSymbol) {
        super(14, UITheme.COLOR_CARD_BG, UITheme.COLOR_CARD_BORDER, false);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SMALL_BOLD);
        lblTitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(UITheme.FONT_SMALL);
        lblSubtitle.setForeground(UITheme.COLOR_BROWN_MED);

        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(lblValue);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(lblSubtitle);

        add(contentPanel, BorderLayout.CENTER);

        // Icon badge on right
        if (iconSymbol != null && !iconSymbol.isEmpty()) {
            JPanel iconPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.enableAntiAliasing(g2);

                    g2.setColor(UITheme.COLOR_ACCENT_MUTED);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                    g2.setColor(UITheme.COLOR_BROWN_MED);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));

                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(iconSymbol)) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                    g2.drawString(iconSymbol, x, y);
                    g2.dispose();
                }
            };

            iconPanel.setOpaque(false);
            iconPanel.setPreferredSize(new Dimension(46, 46));

            add(iconPanel, BorderLayout.EAST);
        }
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }
}

