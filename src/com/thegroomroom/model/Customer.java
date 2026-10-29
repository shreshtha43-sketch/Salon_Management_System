package com.thegroomroom.model;

import java.sql.Timestamp;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String gender;
    private int visitsCount;
    private double totalSpend;
    private Timestamp lastVisit;
    private String notes;

    public Customer() {}

    public Customer(int id, String name, String phone, String email, String gender, int visitsCount, double totalSpend, Timestamp lastVisit, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.visitsCount = visitsCount;
        this.totalSpend = totalSpend;
        this.lastVisit = lastVisit;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getVisitsCount() { return visitsCount; }
    public void setVisitsCount(int visitsCount) { this.visitsCount = visitsCount; }

    public double getTotalSpend() { return totalSpend; }
    public void setTotalSpend(double totalSpend) { this.totalSpend = totalSpend; }

    public Timestamp getLastVisit() { return lastVisit; }
    public void setLastVisit(Timestamp lastVisit) { this.lastVisit = lastVisit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
