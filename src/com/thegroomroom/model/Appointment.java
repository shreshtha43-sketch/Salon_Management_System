package com.thegroomroom.model;

import java.sql.Date;

public class Appointment {
    private int id;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private int barberId;
    private String barberName;
    private Date appointmentDate;
    private String appointmentTime;
    private String servicesRequested;
    private double estimatedAmount;
    private String status; // SCHEDULED, IN_CHAIR, COMPLETED, CANCELLED, PAID
    private String notes;

    public Appointment() {}

    public Appointment(int id, int customerId, String customerName, String customerPhone, 
                       int barberId, String barberName, Date appointmentDate, String appointmentTime, 
                       String servicesRequested, double estimatedAmount, String status, String notes) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.barberId = barberId;
        this.barberName = barberName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.servicesRequested = servicesRequested;
        this.estimatedAmount = estimatedAmount;
        this.status = status;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getServicesRequested() { return servicesRequested; }
    public void setServicesRequested(String servicesRequested) { this.servicesRequested = servicesRequested; }

    public double getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(double estimatedAmount) { this.estimatedAmount = estimatedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
