package com.thegroomroom.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.thegroomroom.ui.theme.UITheme;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initModernStyle();
    }

    public ModernTable() {
        super();
        initModernStyle();
    }

    private void initModernStyle() {
        setRowHeight(40);
        setFont(UITheme.FONT_BODY);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionBackground(UITheme.COLOR_ACCENT_MUTED);
        setSelectionForeground(UITheme.COLOR_TEXT_PRIMARY);
        setBackground(Color.WHITE);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(UITheme.COLOR_PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(UITheme.COLOR_PRIMARY_DARK);
                lbl.setForeground(UITheme.COLOR_ACCENT_MUTED);
                lbl.setFont(UITheme.FONT_BODY_BOLD);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        // Cell Renderer for alternating colors and padding
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Badge) {
                    return (Component) value;
                }

                String text = value != null ? value.toString() : "";

                // Check if text is a known status keyword
                String upper = text.toUpperCase().replace(" ", "_");
                if (upper.equals("COMPLETED") || upper.equals("IN_CHAIR") || upper.equals("SCHEDULED") ||
                    upper.equals("AVAILABLE") || upper.equals("IN_SERVICE") || upper.equals("PAID") ||
                    upper.equals("CANCELLED") || upper.equals("ON_BREAK") || upper.equals("LOW_STOCK") ||
                    upper.equals("ACTIVE")) {
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
                    p.setOpaque(true);
                    p.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : UITheme.COLOR_CANVAS_BG));
                    p.add(Badge.ofStatus(text));
                    return p;
                }

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                lbl.setFont(UITheme.FONT_BODY);

                if (isSelected) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.COLOR_CANVAS_BG);
                    lbl.setForeground(UITheme.COLOR_TEXT_PRIMARY);
                }

                // If column header has 'Price' or 'Amount' or starts with ₹, bold it
                if (text.startsWith("₹")) {
                    lbl.setFont(UITheme.FONT_BODY_BOLD);
                    lbl.setForeground(UITheme.COLOR_BROWN_MED);
                }

                return lbl;
            }
        });
    }

    public static JScrollPane createModernScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        return scrollPane;
    }
}
