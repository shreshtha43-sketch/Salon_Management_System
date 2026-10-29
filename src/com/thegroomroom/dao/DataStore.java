package com.thegroomroom.dao;

import com.thegroomroom.config.DBConnection;
import com.thegroomroom.model.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * DataStore provides unified data access.
 * If MySQL is connected, it reads/writes from the database.
 * If MySQL is not yet configured with root password, it operates seamlessly
 * in Memory/Local mode with pre-seeded realistic Indian salon data.
 */
public class DataStore {
    private static DataStore instance;

    private final List<User> users = new CopyOnWriteArrayList<>();
    private final List<Barber> barbers = new CopyOnWriteArrayList<>();
    private final List<ServiceItem> services = new CopyOnWriteArrayList<>();
    private final List<Customer> customers = new CopyOnWriteArrayList<>();
    private final List<Appointment> appointments = new CopyOnWriteArrayList<>();
    private final List<Bill> bills = new CopyOnWriteArrayList<>();
    private final List<InventoryItem> inventory = new CopyOnWriteArrayList<>();

    private boolean isMySqlMode = false;

    private DataStore() {
        checkAndSyncWithDatabase();
        if (!isMySqlMode) {
            seedDefaultData();
        }
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public synchronized boolean checkAndSyncWithDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                isMySqlMode = true;
                loadFromMySQL(conn);
                return true;
            }
        } catch (Exception e) {
            isMySqlMode = false;
        }
        return false;
    }

    public boolean isMySqlMode() {
        return isMySqlMode;
    }

    public void setMySqlMode(boolean mode) {
        this.isMySqlMode = mode;
    }

    private void seedDefaultData() {
        // 1. Users
        users.clear();
        users.add(new User(1, "admin", "admin123", "Rajesh Malhotra", "ADMIN", "9810123456", "admin@thegroomroom.in", true));
        users.add(new User(2, "reception", "recep123", "Pooja Sharma", "RECEPTIONIST", "9820654321", "reception@thegroomroom.in", true));
        users.add(new User(3, "barber_vikram", "barber123", "Vikram Singh", "BARBER", "9876501234", "vikram@thegroomroom.in", true));
        users.add(new User(4, "barber_kabir", "barber123", "Kabir Khan", "BARBER", "9876502345", "kabir@thegroomroom.in", true));
        users.add(new User(5, "barber_arjun", "barber123", "Arjun Verma", "BARBER", "9876503456", "arjun@thegroomroom.in", true));

        // 2. Barbers
        barbers.clear();
        barbers.add(new Barber(1, 3, "Vikram Singh", "Master Fade & Beard Architect", 1, 40.0, "9876501234", "AVAILABLE", 142));
        barbers.add(new Barber(2, 4, "Kabir Khan", "Royal Shave & Hair Spa Specialist", 2, 38.0, "9876502345", "IN_SERVICE", 165));
        barbers.add(new Barber(3, 5, "Arjun Verma", "Senior Stylist & Scalp Therapist", 3, 35.0, "9876503456", "AVAILABLE", 98));

        // 3. Indian Salon Services (Prices in INR)
        services.clear();
        // Hair
        services.add(new ServiceItem(1, "HAIR", "Signature Haircut & Wash", 40, 450.0, "Precision cut, luxury hair wash, scalp massage, blow-dry styling.", true));
        services.add(new ServiceItem(2, "HAIR", "Classic Buzz & Fade Cut", 30, 350.0, "Zero fade, taper blend, precision trimmer outline, hot towel finish.", true));
        services.add(new ServiceItem(3, "HAIR", "Senior Hair Styling & Blow Dry", 25, 250.0, "Matte clay/pomade styling, texture work, professional finish.", true));
        services.add(new ServiceItem(4, "HAIR", "Global Hair Colour / Highlights", 60, 1500.0, "Ammonia-free organic hair colouring with shine lock conditioner.", true));
        // Beard
        services.add(new ServiceItem(5, "BEARD", "Royal Hot Towel Shave", 30, 350.0, "Pre-shave essential oil, double lather, straight-edge blade, alum stone & balm.", true));
        services.add(new ServiceItem(6, "BEARD", "Beard Sculpting & Lineup", 25, 300.0, "Geometric beard trimming, straight razor neck line, Argan oil massage.", true));
        services.add(new ServiceItem(7, "BEARD", "Premium Beard Spa & Conditioning", 30, 400.0, "Deep cleansing wash, warm towel steam, growth serum, butter massage.", true));
        // Spa & Champi
        services.add(new ServiceItem(8, "SPA_DETOX", "Traditional Head Champi (25m)", 25, 450.0, "Pure Ayurvedic Brahmi & Bhringraj warm herbal oil scalp massage.", true));
        services.add(new ServiceItem(9, "SPA_DETOX", "Deep Conditioning Hair Spa", 50, 750.0, "Intense keratin infusion, ozone steam therapy, scalp revitalizer masque.", true));
        services.add(new ServiceItem(10, "SPA_DETOX", "Anti-Dandruff Scalp Treatment", 45, 850.0, "Tea-tree clarifying scrub, salicylic scalp wash, cooling masque.", true));
        // Facial & Cleanup
        services.add(new ServiceItem(11, "FACIAL_CLEANUP", "Charcoal Detox D-Tan Cleanup", 35, 700.0, "Activated charcoal scrub, tan removal peel-off mask, mint toner.", true));
        services.add(new ServiceItem(12, "FACIAL_CLEANUP", "Herbal Gold Radiance Facial", 50, 1200.0, "Gold dust micro-exfoliation, ultrasonic toning, hydra-infusion mask.", true));
        services.add(new ServiceItem(13, "FACIAL_CLEANUP", "Instant Glow Face Polishing", 30, 600.0, "Papaya enzymatic exfoliation, vitamin C serum, jade roller finish.", true));
        // Packages
        services.add(new ServiceItem(14, "BRIDAL_GROOM", "The Royal Groom Makeover Package", 120, 3500.0, "Signature Haircut, Royal Shave, Gold Facial, D-Tan, and Head Champi.", true));

        // 4. Customers
        customers.clear();
        long now = System.currentTimeMillis();
        customers.add(new Customer(1, "Aarav Mehta", "9821011223", "aarav.m@gmail.com", "MALE", 5, 4200.0, new Timestamp(now - 86400000L * 2), "Prefers low skin fade and extra mint cooling balm."));
        customers.add(new Customer(2, "Rohan Kapoor", "9819033445", "rohan.k@outlook.com", "MALE", 3, 2150.0, new Timestamp(now - 86400000L), "Sensitive skin on neck. Always use sensitive shaving foam."));
        customers.add(new Customer(3, "Vikramaditya Roy", "9833055667", "v.roy@yahoo.in", "MALE", 8, 8900.0, new Timestamp(now - 86400000L * 5), "VIP regular. Green tea during Head Champi."));
        customers.add(new Customer(4, "Priya Sundaram", "9844077889", "priya.s@gmail.com", "FEMALE", 2, 2250.0, new Timestamp(now - 86400000L * 10), "Deep conditioning spa client."));
        customers.add(new Customer(5, "Kabir Sengupta", "9855099001", "kabir.s@gmail.com", "MALE", 1, 800.0, new Timestamp(now - 86400000L * 3), "First time walk-in. Very happy with beard lineup."));

        // 5. Appointments
        appointments.clear();
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        appointments.add(new Appointment(1, 1, "Aarav Mehta", "9821011223", 1, "Vikram Singh", today, "11:00 AM", "Signature Haircut & Wash, Beard Sculpting", 750.0, "COMPLETED", "Regular client on Chair 1"));
        appointments.add(new Appointment(2, 2, "Rohan Kapoor", "9819033445", 2, "Kabir Khan", today, "12:30 PM", "Royal Hot Towel Shave, Charcoal Detox D-Tan", 1050.0, "IN_CHAIR", "Currently in chair 2 with Kabir"));
        appointments.add(new Appointment(3, 3, "Vikramaditya Roy", "9833055667", 1, "Vikram Singh", today, "02:00 PM", "Traditional Head Champi, Signature Haircut", 900.0, "SCHEDULED", "Hot green tea requested"));
        appointments.add(new Appointment(4, 4, "Priya Sundaram", "9844077889", 3, "Arjun Verma", today, "03:30 PM", "Deep Conditioning Hair Spa", 750.0, "SCHEDULED", "Scalp hydration consultation"));
        appointments.add(new Appointment(5, 5, "Kabir Sengupta", "9855099001", 2, "Kabir Khan", today, "05:00 PM", "Beard Sculpting & Lineup", 300.0, "SCHEDULED", "Pre-meeting trim"));

        // 6. Bills
        bills.clear();
        Bill b1 = new Bill();
        b1.setId(1);
        b1.setBillNumber("TGR-2026-1001");
        b1.setCustomerId(1);
        b1.setCustomerName("Aarav Mehta");
        b1.setCustomerPhone("9821011223");
        b1.setBarberId(1);
        b1.setBarberName("Vikram Singh");
        b1.setSubtotal(750.0);
        b1.setTaxPercent(5.0);
        b1.setTaxAmount(37.5);
        b1.setDiscountAmount(0.0);
        b1.setGrandTotal(787.5);
        b1.setPaymentMethod("UPI");
        b1.setPaymentStatus("PAID");
        b1.setBillDate(new Timestamp(now - 3600000L * 3));
        b1.addItem(new BillItem("Signature Haircut & Wash", "SERVICE", 1, 450.0));
        b1.addItem(new BillItem("Beard Sculpting & Lineup", "SERVICE", 1, 300.0));
        bills.add(b1);

        Bill b2 = new Bill();
        b2.setId(2);
        b2.setBillNumber("TGR-2026-1002");
        b2.setCustomerId(3);
        b2.setCustomerName("Vikramaditya Roy");
        b2.setCustomerPhone("9833055667");
        b2.setBarberId(2);
        b2.setBarberName("Kabir Khan");
        b2.setSubtotal(1650.0);
        b2.setTaxPercent(5.0);
        b2.setTaxAmount(82.5);
        b2.setDiscountAmount(50.0);
        b2.setGrandTotal(1682.5);
        b2.setPaymentMethod("CARD");
        b2.setPaymentStatus("PAID");
        b2.setBillDate(new Timestamp(now - 3600000L * 1));
        b2.addItem(new BillItem("Traditional Head Champi (25m)", "SERVICE", 1, 450.0));
        b2.addItem(new BillItem("Herbal Gold Radiance Facial", "SERVICE", 1, 1200.0));
        bills.add(b2);

        // 7. Inventory
        inventory.clear();
        inventory.add(new InventoryItem(1, "Beard Growth & Shine Oil (50ml)", "Beard Care", 18, 450.0, 240.0, 5, "Bombay Shaving Co. Dist.", new Timestamp(now)));
        inventory.add(new InventoryItem(2, "Matte Finish Hair Clay Wax (80g)", "Hair Styling", 12, 550.0, 310.0, 4, "Urban Groom Labs", new Timestamp(now)));
        inventory.add(new InventoryItem(3, "Activated Charcoal Face Scrub (100g)", "Skin Care", 8, 399.0, 190.0, 3, "Khadi Naturals Ltd.", new Timestamp(now)));
        inventory.add(new InventoryItem(4, "Argan Oil Hair Restorative Serum", "Hair Care", 4, 699.0, 380.0, 5, "L’Oreal Professional", new Timestamp(now)));
        inventory.add(new InventoryItem(5, "Ayurvedic Brahmi Bhringraj Oil (200ml)", "Scalp Care", 15, 350.0, 180.0, 6, "Kottakkal Arya Vaidya Sala", new Timestamp(now)));
        inventory.add(new InventoryItem(6, "Double-Edge Platinum Razor Blades (100pk)", "Salon Consumables", 25, 450.0, 220.0, 8, "SuperMax India", new Timestamp(now)));
    }

    private void loadFromMySQL(Connection conn) {
        try {
            // Load Users
            users.clear();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getBoolean("is_active")
                ));
            }

            // Load Barbers
            barbers.clear();
            rs = st.executeQuery("SELECT * FROM barbers");
            while (rs.next()) {
                barbers.add(new Barber(
                        rs.getInt("id"),
                        (Integer) rs.getObject("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("chair_number"),
                        rs.getDouble("commission_rate"),
                        rs.getString("phone"),
                        rs.getString("status"),
                        rs.getInt("total_cuts")
                ));
            }

            // Load Services
            services.clear();
            rs = st.executeQuery("SELECT * FROM services WHERE is_active = TRUE");
            while (rs.next()) {
                services.add(new ServiceItem(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getString("name"),
                        rs.getInt("duration_minutes"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                ));
            }

            // Load Customers
            customers.clear();
            rs = st.executeQuery("SELECT * FROM customers");
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getInt("visits_count"),
                        rs.getDouble("total_spend"),
                        rs.getTimestamp("last_visit"),
                        rs.getString("notes")
                ));
            }

            // Load Appointments
            appointments.clear();
            rs = st.executeQuery(
                    "SELECT a.*, c.name as cust_name, c.phone as cust_phone, b.name as barb_name " +
                    "FROM appointments a " +
                    "LEFT JOIN customers c ON a.customer_id = c.id " +
                    "LEFT JOIN barbers b ON a.barber_id = b.id ORDER BY a.appointment_date DESC, a.id DESC"
            );
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("cust_name"),
                        rs.getString("cust_phone"),
                        rs.getInt("barber_id"),
                        rs.getString("barb_name"),
                        rs.getDate("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("services_requested"),
                        rs.getDouble("estimated_amount"),
                        rs.getString("status"),
                        rs.getString("notes")
                ));
            }

            // Load Inventory
            inventory.clear();
            rs = st.executeQuery("SELECT * FROM inventory");
            while (rs.next()) {
                inventory.add(new InventoryItem(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("stock_quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("cost_price"),
                        rs.getInt("alert_threshold"),
                        rs.getString("supplier_info"),
                        rs.getTimestamp("last_restocked")
                ));
            }

            // Load Bills
            bills.clear();
            rs = st.executeQuery(
                    "SELECT b.*, c.name as cust_name, c.phone as cust_phone, bar.name as barb_name " +
                    "FROM bills b " +
                    "LEFT JOIN customers c ON b.customer_id = c.id " +
                    "LEFT JOIN barbers bar ON b.barber_id = bar.id ORDER BY b.id DESC"
            );
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillNumber(rs.getString("bill_number"));
                bill.setCustomerId(rs.getInt("customer_id"));
                bill.setCustomerName(rs.getString("cust_name"));
                bill.setCustomerPhone(rs.getString("cust_phone"));
                bill.setBarberId(rs.getInt("barber_id"));
                bill.setBarberName(rs.getString("barb_name"));
                bill.setSubtotal(rs.getDouble("subtotal"));
                bill.setTaxPercent(rs.getDouble("tax_percent"));
                bill.setTaxAmount(rs.getDouble("tax_amount"));
                bill.setDiscountAmount(rs.getDouble("discount_amount"));
                bill.setGrandTotal(rs.getDouble("grand_total"));
                bill.setPaymentMethod(rs.getString("payment_method"));
                bill.setPaymentStatus(rs.getString("payment_status"));
                bill.setBillDate(rs.getTimestamp("bill_date"));
                bills.add(bill);
            }

            // Load Bill Items
            for (Bill b : bills) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM bill_items WHERE bill_id = ?");
                ps.setInt(1, b.getId());
                ResultSet rsItems = ps.executeQuery();
                while (rsItems.next()) {
                    b.addItem(new BillItem(
                            rsItems.getInt("id"),
                            rsItems.getInt("bill_id"),
                            (Integer) rsItems.getObject("service_id"),
                            rsItems.getString("item_name"),
                            rsItems.getString("item_type"),
                            rsItems.getInt("quantity"),
                            rsItems.getDouble("price")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[DataStore] Error loading data from MySQL: " + e.getMessage());
        }
    }

    // ==========================================
    // GETTERS & MUTATORS
    // ==========================================

    public List<User> getUsers() { return users; }
    public List<Barber> getBarbers() { return barbers; }
    public List<ServiceItem> getServices() { return services; }
    public List<Customer> getCustomers() { return customers; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Bill> getBills() { return bills; }
    public List<InventoryItem> getInventory() { return inventory; }

    public User authenticate(String username, String password, String expectedRole) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                if (expectedRole == null || u.getRole().equalsIgnoreCase(expectedRole)) {
                    return u;
                }
            }
        }
        return null;
    }

    public Barber getBarberByUserId(int userId) {
        for (Barber b : barbers) {
            if (b.getUserId() != null && b.getUserId() == userId) {
                return b;
            }
        }
        // Fallback to first barber if not linked
        return barbers.isEmpty() ? null : barbers.get(0);
    }

    public Barber getBarberById(int id) {
        for (Barber b : barbers) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    public Customer getCustomerByPhone(String phone) {
        for (Customer c : customers) {
            if (c.getPhone().equals(phone.trim())) return c;
        }
        return null;
    }

    public Customer findOrCreateCustomer(String name, String phone) {
        Customer existing = getCustomerByPhone(phone);
        if (existing != null) {
            return existing;
        }
        int newId = customers.size() + 1;
        Customer c = new Customer(newId, name, phone, "", "MALE", 1, 0.0, new Timestamp(System.currentTimeMillis()), "New Walk-in");
        customers.add(c);

        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customers (name, phone, visits_count, total_spend) VALUES (?, ?, 1, 0)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) c.setId(gk.getInt(1));
            } catch (Exception e) {
                System.err.println("[DataStore] DB save customer err: " + e.getMessage());
            }
        }
        return c;
    }

    public void addAppointment(Appointment appt) {
        int newId = appointments.size() + 1;
        appt.setId(newId);
        appointments.add(0, appt); // Add to beginning of list

        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO appointments (customer_id, barber_id, appointment_date, appointment_time, services_requested, estimated_amount, status, notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setInt(1, appt.getCustomerId());
                ps.setInt(2, appt.getBarberId());
                ps.setDate(3, appt.getAppointmentDate());
                ps.setString(4, appt.getAppointmentTime());
                ps.setString(5, appt.getServicesRequested());
                ps.setDouble(6, appt.getEstimatedAmount());
                ps.setString(7, appt.getStatus());
                ps.setString(8, appt.getNotes());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) appt.setId(gk.getInt(1));
            } catch (Exception e) {
                System.err.println("[DataStore] DB save appt err: " + e.getMessage());
            }
        }
    }

    public void updateAppointmentStatus(int appointmentId, String newStatus) {
        for (Appointment a : appointments) {
            if (a.getId() == appointmentId) {
                a.setStatus(newStatus);
                break;
            }
        }
        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status = ? WHERE id = ?");
                ps.setString(1, newStatus);
                ps.setInt(2, appointmentId);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("[DataStore] DB update appt status err: " + e.getMessage());
            }
        }
    }

    public void updateBarberStatus(int barberId, String newStatus) {
        for (Barber b : barbers) {
            if (b.getId() == barberId) {
                b.setStatus(newStatus);
                break;
            }
        }
        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE barbers SET status = ? WHERE id = ?");
                ps.setString(1, newStatus);
                ps.setInt(2, barberId);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("[DataStore] DB update barber status err: " + e.getMessage());
            }
        }
    }

    public void saveBill(Bill bill) {
        int newId = bills.size() + 1;
        bill.setId(newId);
        bills.add(0, bill);

        // Update customer total spend and visit count
        for (Customer c : customers) {
            if (c.getId() == bill.getCustomerId()) {
                c.setVisitsCount(c.getVisitsCount() + 1);
                c.setTotalSpend(c.getTotalSpend() + bill.getGrandTotal());
                c.setLastVisit(bill.getBillDate());
                break;
            }
        }

        // Increment barber cuts
        for (Barber b : barbers) {
            if (b.getId() == bill.getBarberId()) {
                b.setTotalCuts(b.getTotalCuts() + 1);
                break;
            }
        }

        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO bills (bill_number, customer_id, barber_id, subtotal, tax_percent, tax_amount, discount_amount, grand_total, payment_method, payment_status, bill_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, bill.getBillNumber());
                ps.setInt(2, bill.getCustomerId());
                ps.setInt(3, bill.getBarberId());
                ps.setDouble(4, bill.getSubtotal());
                ps.setDouble(5, bill.getTaxPercent());
                ps.setDouble(6, bill.getTaxAmount());
                ps.setDouble(7, bill.getDiscountAmount());
                ps.setDouble(8, bill.getGrandTotal());
                ps.setString(9, bill.getPaymentMethod());
                ps.setString(10, bill.getPaymentStatus());
                ps.setTimestamp(11, bill.getBillDate());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) {
                    int billId = gk.getInt(1);
                    bill.setId(billId);
                    for (BillItem item : bill.getItems()) {
                        PreparedStatement psi = conn.prepareStatement(
                                "INSERT INTO bill_items (bill_id, service_id, item_name, item_type, quantity, price) VALUES (?, ?, ?, ?, ?, ?)"
                        );
                        psi.setInt(1, billId);
                        if (item.getServiceId() != null) psi.setInt(2, item.getServiceId());
                        else psi.setNull(2, Types.INTEGER);
                        psi.setString(3, item.getItemName());
                        psi.setString(4, item.getItemType());
                        psi.setInt(5, item.getQuantity());
                        psi.setDouble(6, item.getPrice());
                        psi.executeUpdate();
                    }
                }
            } catch (Exception e) {
                System.err.println("[DataStore] DB save bill err: " + e.getMessage());
            }
        }
    }

    public void addService(ServiceItem s) {
        int newId = services.size() + 1;
        s.setId(newId);
        services.add(s);

        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO services (category, name, duration_minutes, price, description, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, s.getCategory());
                ps.setString(2, s.getName());
                ps.setInt(3, s.getDurationMinutes());
                ps.setDouble(4, s.getPrice());
                ps.setString(5, s.getDescription());
                ps.setBoolean(6, s.isActive());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) s.setId(gk.getInt(1));
            } catch (Exception e) {
                System.err.println("[DataStore] DB add service err: " + e.getMessage());
            }
        }
    }

    public void deleteService(int serviceId) {
        services.removeIf(s -> s.getId() == serviceId);
        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE services SET is_active = FALSE WHERE id = ?");
                ps.setInt(1, serviceId);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("[DataStore] DB delete service err: " + e.getMessage());
            }
        }
    }

    public void addBarber(Barber b) {
        int newId = barbers.size() + 1;
        b.setId(newId);
        barbers.add(b);

        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO barbers (user_id, name, specialization, chair_number, commission_rate, phone, status, total_cuts) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                if (b.getUserId() != null) ps.setInt(1, b.getUserId());
                else ps.setNull(1, Types.INTEGER);
                ps.setString(2, b.getName());
                ps.setString(3, b.getSpecialization());
                ps.setInt(4, b.getChairNumber());
                ps.setDouble(5, b.getCommissionRate());
                ps.setString(6, b.getPhone());
                ps.setString(7, b.getStatus());
                ps.setInt(8, b.getTotalCuts());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) b.setId(gk.getInt(1));
            } catch (Exception e) {
                System.err.println("[DataStore] DB add barber err: " + e.getMessage());
            }
        }
    }

    public void addInventoryItem(InventoryItem item) {
        int newId = inventory.size() + 1;
        item.setId(newId);
        inventory.add(item);

        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO inventory (product_name, category, stock_quantity, unit_price, cost_price, alert_threshold, supplier_info) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, item.getProductName());
                ps.setString(2, item.getCategory());
                ps.setInt(3, item.getStockQuantity());
                ps.setDouble(4, item.getUnitPrice());
                ps.setDouble(5, item.getCostPrice());
                ps.setInt(6, item.getAlertThreshold());
                ps.setString(7, item.getSupplierInfo());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) item.setId(gk.getInt(1));
            } catch (Exception e) {
                System.err.println("[DataStore] DB add inventory err: " + e.getMessage());
            }
        }
    }

    public void updateInventoryStock(int itemId, int newQuantity) {
        for (InventoryItem it : inventory) {
            if (it.getId() == itemId) {
                it.setStockQuantity(newQuantity);
                break;
            }
        }
        if (isMySqlMode) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE inventory SET stock_quantity = ? WHERE id = ?");
                ps.setInt(1, newQuantity);
                ps.setInt(2, itemId);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("[DataStore] DB update stock err: " + e.getMessage());
            }
        }
    }
}
