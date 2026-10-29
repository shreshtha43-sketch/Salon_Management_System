package com.thegroomroom.ui.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class UITheme {
    // Brand Colors (Classy Brown & Amber Gold Luxury Tone)
    public static final Color COLOR_PRIMARY_DARK = new Color(0x1C, 0x13, 0x0D);      // Deep Espresso
    public static final Color COLOR_SIDEBAR_BG = new Color(0x24, 0x19, 0x12);        // Walnut Leather
    public static final Color COLOR_SIDEBAR_HOVER = new Color(0x35, 0x26, 0x1C);     // Roasted Brown
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(0x4A, 0x33, 0x25);    // Warm Cognac Active
    public static final Color COLOR_ACCENT_GOLD = new Color(0xC5, 0x8D, 0x54);       // Warm Amber Gold
    public static final Color COLOR_ACCENT_HOVER = new Color(0xD8, 0x9F, 0x67);      // Lighter Gold Hover
    public static final Color COLOR_ACCENT_MUTED = new Color(0xF3, 0xE7, 0xDC);      // Soft Cream Gold Tint
    public static final Color COLOR_BROWN_MED = new Color(0x7D, 0x53, 0x38);         // Medium Cognac
    public static final Color COLOR_BROWN_LIGHT = new Color(0xA4, 0x75, 0x55);       // Light Walnut

    // Canvas & Surfaces
    public static final Color COLOR_CANVAS_BG = new Color(0xF8, 0xF5, 0xF0);         // Warm Silk Ivory
    public static final Color COLOR_CARD_BG = Color.WHITE;                            // Crisp Card White
    public static final Color COLOR_CARD_BORDER = new Color(0xE8, 0xDF, 0xD5);       // Warm Border
    public static final Color COLOR_HOVER_BG = new Color(0xF4, 0xEE, 0xE7);          // Soft Hover Ivory

    // Typography Colors
    public static final Color COLOR_TEXT_PRIMARY = new Color(0x24, 0x18, 0x11);      // Deep Roast Text
    public static final Color COLOR_TEXT_SECONDARY = new Color(0x7D, 0x71, 0x67);    // Warm Muted Gray
    public static final Color COLOR_TEXT_LIGHT = new Color(0xE6, 0xDC, 0xD3);        // Light Text for Dark BG
    public static final Color COLOR_TEXT_GOLD = new Color(0xC5, 0x8D, 0x54);         // Gold Text

    // Status Badges
    public static final Color COLOR_SUCCESS = new Color(0x27, 0x7A, 0x44);            // Emerald Green
    public static final Color COLOR_SUCCESS_BG = new Color(0xE8, 0xF5, 0xEC);
    public static final Color COLOR_WARNING = new Color(0xC0, 0x76, 0x22);            // Warm Amber
    public static final Color COLOR_WARNING_BG = new Color(0xFD, 0xF3, 0xE6);
    public static final Color COLOR_INFO = new Color(0x2B, 0x69, 0x93);               // Slate Blue
    public static final Color COLOR_INFO_BG = new Color(0xE9, 0xF2, 0xF8);
    public static final Color COLOR_DANGER = new Color(0xB8, 0x35, 0x2B);             // Crimson
    public static final Color COLOR_DANGER_BG = new Color(0xFD, 0xEB, 0xEB);

    // Modern Typography
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    public static void enableAntiAliasing(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
    }
}
