package com.thegroomroom.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int id;
    private String billNumber;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private int barberId;
    private String barberName;
    private double subtotal;
    private double taxPercent; // e.g. 5.0%
    private double taxAmount;
    private double discountAmount;
    private double grandTotal;
    private String paymentMethod; // CASH, UPI, CARD, NET_BANKING
    private String paymentStatus; // PAID, PENDING, REFUNDED
    private Timestamp billDate;
    private List<BillItem> items = new ArrayList<>();

    public Bill() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public int getBarberId() { return barberId; }
    public void setBarberId(int barberId) { this.barberId = barberId; }

    public String getBarberName() { return barberName; }
    public void setBarberName(String barberName) { this.barberName = barberName; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTaxPercent() { return taxPercent; }
    public void setTaxPercent(double taxPercent) { this.taxPercent = taxPercent; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Timestamp getBillDate() { return billDate; }
    public void setBillDate(Timestamp billDate) { this.billDate = billDate; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
    public void addItem(BillItem item) { this.items.add(item); }
}
