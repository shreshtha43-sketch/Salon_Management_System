package com.thegroomroom.ui.dialogs;

import com.thegroomroom.model.Bill;
import com.thegroomroom.model.BillItem;
import com.thegroomroom.ui.components.ModernButton;
import com.thegroomroom.ui.components.RoundedPanel;
import com.thegroomroom.ui.theme.UITheme;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class InvoiceReceiptDialog extends JDialog {

    public InvoiceReceiptDialog(Frame parent, Bill bill) {
        super(parent, "The Groom Room - GST Tax Invoice", true);
        setSize(480, 680);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.COLOR_CANVAS_BG);
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setOpaque(false);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Receipt Card
        RoundedPanel receiptCard = new RoundedPanel(16, Color.WHITE, UITheme.COLOR_CARD_BORDER, true);
        receiptCard.setLayout(new BorderLayout(10, 10));
        receiptCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel lblSalon = new JLabel("THE GROOM ROOM");
        lblSalon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSalon.setForeground(UITheme.COLOR_PRIMARY_DARK);
        lblSalon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("LUXURY GROOMING LOUNGE & SALON");
        lblTagline.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTagline.setForeground(UITheme.COLOR_BROWN_MED);
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAddress = new JLabel("Connaught Place, New Delhi - 110001 | +91 98101 23456");
        lblAddress.setFont(UITheme.FONT_SMALL);
        lblAddress.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lblAddress.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblGst = new JLabel("GSTIN: 07AAAAA0000A1Z5 | TAX INVOICE");
        lblGst.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblGst.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lblGst.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblSalon);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(lblTagline);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblAddress);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(lblGst);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(createDivider());
        headerPanel.add(Box.createVerticalStrut(10));

        // Details Grid
        JPanel metaPanel = new JPanel(new GridLayout(4, 2, 8, 4));
        metaPanel.setOpaque(false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        String dateStr = bill.getBillDate() != null ? sdf.format(bill.getBillDate()) : sdf.format(new java.util.Date());

        addMetaRow(metaPanel, "Invoice No:", bill.getBillNumber());
        addMetaRow(metaPanel, "Date & Time:", dateStr);
        addMetaRow(metaPanel, "Customer:", bill.getCustomerName() + " (" + bill.getCustomerPhone() + ")");
        addMetaRow(metaPanel, "Stylist / Barber:", bill.getBarberName());

        headerPanel.add(metaPanel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(createDivider());

        receiptCard.add(headerPanel, BorderLayout.NORTH);

        // Line Items List
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        // Header for items
        JPanel itemHeader = new JPanel(new BorderLayout());
        itemHeader.setOpaque(false);
        JLabel lblItemTitle = new JLabel("SERVICE / ITEM DESCRIPTION");
        lblItemTitle.setFont(UITheme.FONT_SMALL_BOLD);
        lblItemTitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        JLabel lblPriceTitle = new JLabel("AMOUNT (₹)");
        lblPriceTitle.setFont(UITheme.FONT_SMALL_BOLD);
        lblPriceTitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        itemHeader.add(lblItemTitle, BorderLayout.WEST);
        itemHeader.add(lblPriceTitle, BorderLayout.EAST);
        itemsPanel.add(itemHeader);
        itemsPanel.add(Box.createVerticalStrut(6));

        for (BillItem it : bill.getItems()) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel name = new JLabel("• " + it.getItemName() + (it.getQuantity() > 1 ? " (x" + it.getQuantity() + ")" : ""));
            name.setFont(UITheme.FONT_BODY);
            name.setForeground(UITheme.COLOR_TEXT_PRIMARY);

            JLabel price = new JLabel(String.format("₹%.2f", it.getTotal()));
            price.setFont(UITheme.FONT_BODY_BOLD);
            price.setForeground(UITheme.COLOR_TEXT_PRIMARY);

            row.add(name, BorderLayout.WEST);
            row.add(price, BorderLayout.EAST);
            itemsPanel.add(row);
            itemsPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane scrollItems = new JScrollPane(itemsPanel);
        scrollItems.setBorder(null);
        scrollItems.setOpaque(false);
        scrollItems.getViewport().setOpaque(false);
        receiptCard.add(scrollItems, BorderLayout.CENTER);

        // Summary Calculations (South)
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(false);
        summaryPanel.add(Box.createVerticalStrut(8));
        summaryPanel.add(createDivider());
        summaryPanel.add(Box.createVerticalStrut(8));

        addSummaryRow(summaryPanel, "Subtotal:", String.format("₹%.2f", bill.getSubtotal()), false);
        addSummaryRow(summaryPanel, "CGST (2.5%):", String.format("₹%.2f", bill.getTaxAmount() / 2.0), false);
        addSummaryRow(summaryPanel, "SGST (2.5%):", String.format("₹%.2f", bill.getTaxAmount() / 2.0), false);
        if (bill.getDiscountAmount() > 0) {
            addSummaryRow(summaryPanel, "Discount:", String.format("-₹%.2f", bill.getDiscountAmount()), false);
        }

        summaryPanel.add(Box.createVerticalStrut(6));
        summaryPanel.add(createDivider());
        summaryPanel.add(Box.createVerticalStrut(6));

        // Grand Total Row
        JPanel grandRow = new JPanel(new BorderLayout());
        grandRow.setOpaque(false);
        JLabel lblGrand = new JLabel("GRAND TOTAL:");
        lblGrand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblGrand.setForeground(UITheme.COLOR_PRIMARY_DARK);

        JLabel lblGrandVal = new JLabel(String.format("₹%.2f", bill.getGrandTotal()));
        lblGrandVal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblGrandVal.setForeground(UITheme.COLOR_BROWN_MED);

        grandRow.add(lblGrand, BorderLayout.WEST);
        grandRow.add(lblGrandVal, BorderLayout.EAST);
        summaryPanel.add(grandRow);

        summaryPanel.add(Box.createVerticalStrut(6));

        JLabel lblPayMode = new JLabel("Payment Mode: " + bill.getPaymentMethod() + " (" + bill.getPaymentStatus() + ")");
        lblPayMode.setFont(UITheme.FONT_SMALL_BOLD);
        lblPayMode.setForeground(UITheme.COLOR_SUCCESS);
        lblPayMode.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryPanel.add(lblPayMode);

        summaryPanel.add(Box.createVerticalStrut(6));
        JLabel lblFooter = new JLabel("Thank you for grooming with us! Visit again.");
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryPanel.add(lblFooter);

        receiptCard.add(summaryPanel, BorderLayout.SOUTH);

        rootPanel.add(receiptCard, BorderLayout.CENTER);

        // Bottom Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        ModernButton btnPrint = new ModernButton("Print Invoice", ModernButton.ButtonStyle.ACCENT);
        btnPrint.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Invoice " + bill.getBillNumber() + " sent to POS thermal receipt printer successfully!", "Print Success", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton btnClose = new ModernButton("Close", ModernButton.ButtonStyle.SECONDARY);
        btnClose.addActionListener(e -> dispose());

        actionsPanel.add(btnPrint);
        actionsPanel.add(btnClose);

        rootPanel.add(actionsPanel, BorderLayout.SOUTH);

        add(rootPanel);
    }

    private void addMetaRow(JPanel p, String title, String val) {
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        JLabel v = new JLabel(val);
        v.setFont(UITheme.FONT_SMALL_BOLD);
        v.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        p.add(t);
        p.add(v);
    }

    private void addSummaryRow(JPanel p, String title, String val, boolean bold) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(bold ? UITheme.FONT_BODY_BOLD : UITheme.FONT_BODY);
        t.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        JLabel v = new JLabel(val);
        v.setFont(bold ? UITheme.FONT_BODY_BOLD : UITheme.FONT_BODY);
        v.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        r.add(t, BorderLayout.WEST);
        r.add(v, BorderLayout.EAST);
        p.add(r);
        p.add(Box.createVerticalStrut(2));
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.COLOR_CARD_BORDER);
        return sep;
    }
}
