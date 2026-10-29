-- ============================================================
-- THE GROOM ROOM - LUXURY SALON MANAGEMENT SYSTEM (INDIAN)
-- Complete MySQL Database Schema & Initial Data Seed
-- ============================================================

CREATE DATABASE IF NOT EXISTS the_groom_room
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE the_groom_room;

-- 1. Users Table (Authentication for Admin, Receptionist, Barber)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONIST', 'BARBER') NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Barbers / Stylists Profile Table
CREATE TABLE IF NOT EXISTS barbers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) DEFAULT 'Master Stylist & Groomer',
    chair_number INT NOT NULL UNIQUE,
    commission_rate DECIMAL(5,2) DEFAULT 35.00, -- e.g. 35% commission
    phone VARCHAR(15),
    status ENUM('AVAILABLE', 'IN_SERVICE', 'ON_BREAK', 'OFF_DUTY') DEFAULT 'AVAILABLE',
    total_cuts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Salon Services Catalog Table
CREATE TABLE IF NOT EXISTS services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category ENUM('HAIR', 'BEARD', 'SPA_DETOX', 'FACIAL_CLEANUP', 'BRIDAL_GROOM') NOT NULL,
    name VARCHAR(120) NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 30,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Customers Table (Indian mobile number & profile)
CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    gender ENUM('MALE', 'FEMALE', 'OTHER') DEFAULT 'MALE',
    visits_count INT DEFAULT 1,
    total_spend DECIMAL(12,2) DEFAULT 0.00,
    last_visit TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    barber_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time VARCHAR(20) NOT NULL,
    services_requested VARCHAR(255) NOT NULL,
    estimated_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('SCHEDULED', 'IN_CHAIR', 'COMPLETED', 'CANCELLED', 'PAID') DEFAULT 'SCHEDULED',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (barber_id) REFERENCES barbers(id) ON DELETE CASCADE
);

-- 6. Bills / Invoices Table (Indian Salon POS & GST)
CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    barber_id INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_percent DECIMAL(5,2) DEFAULT 5.00, -- 5% GST standard salon service
    tax_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    grand_total DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'UPI', 'CARD', 'NET_BANKING') NOT NULL DEFAULT 'UPI',
    payment_status ENUM('PAID', 'PENDING', 'REFUNDED') DEFAULT 'PAID',
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (barber_id) REFERENCES barbers(id) ON DELETE CASCADE
);

-- 7. Bill Line Items Table
CREATE TABLE IF NOT EXISTS bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    service_id INT NULL,
    item_name VARCHAR(120) NOT NULL,
    item_type ENUM('SERVICE', 'PRODUCT') DEFAULT 'SERVICE',
    quantity INT DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE SET NULL
);

-- 8. Inventory & Retail Products Table
CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(120) NOT NULL,
    category VARCHAR(60) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2) NOT NULL,
    alert_threshold INT DEFAULT 5,
    supplier_info VARCHAR(120),
    last_restocked TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SEED DATA: Realistic Indian Salon (The Groom Room)
-- ============================================================

-- 1. Default Users
INSERT INTO users (username, password, full_name, role, phone, email) VALUES
('admin', 'admin123', 'Rajesh Malhotra (Owner)', 'ADMIN', '9810123456', 'admin@thegroomroom.in'),
('reception', 'recep123', 'Pooja Sharma (Head Receptionist)', 'RECEPTIONIST', '9820654321', 'reception@thegroomroom.in'),
('barber_vikram', 'barber123', 'Vikram Singh', 'BARBER', '9876501234', 'vikram@thegroomroom.in'),
('barber_kabir', 'barber123', 'Kabir Khan', 'BARBER', '9876502345', 'kabir@thegroomroom.in'),
('barber_arjun', 'barber123', 'Arjun Verma', 'BARBER', '9876503456', 'arjun@thegroomroom.in');

-- 2. Barbers linked to Users
INSERT INTO barbers (user_id, name, specialization, chair_number, commission_rate, phone, status, total_cuts) VALUES
(3, 'Vikram Singh', 'Master Fade & Beard Architect', 1, 40.00, '9876501234', 'AVAILABLE', 142),
(4, 'Kabir Khan', 'Royal Shave & Hair Spa Specialist', 2, 38.00, '9876502345', 'IN_SERVICE', 165),
(5, 'Arjun Verma', 'Senior Stylist & Scalp Therapist', 3, 35.00, '9876503456', 'AVAILABLE', 98);

-- 3. Indian Salon Services Catalog (Prices in INR)
INSERT INTO services (category, name, duration_minutes, price, description) VALUES
-- Hair
('HAIR', 'Signature Haircut & Wash', 40, 450.00, 'Precision cut, luxury hair wash, scalp massage, and blow-dry styling.'),
('HAIR', 'Classic Buzz & Fade Cut', 30, 350.00, 'Zero fade, taper blend, precision trimmer outline, and hot towel finish.'),
('HAIR', 'Senior Hair Styling & Blow Dry', 25, 250.00, 'Matte clay/pomade styling, texture work, and professional finish.'),
('HAIR', 'Global Hair Colour / Highlights', 60, 1500.00, 'Ammonia-free organic hair colouring with shine lock conditioner.'),

-- Beard
('BEARD', 'Royal Hot Towel Shave', 30, 350.00, 'Pre-shave essential oil, double lather, straight-edge blade, alum stone & soothing balm.'),
('BEARD', 'Beard Sculpting & Lineup', 25, 300.00, 'Geometric beard trimming, straight razor neck line, and Argan oil massage.'),
('BEARD', 'Premium Beard Spa & Conditioning', 30, 400.00, 'Deep cleansing wash, warm towel steam, growth serum, and butter massage.'),

-- Spa & Detox
('SPA_DETOX', 'Traditional Head Champi (25m)', 25, 450.00, 'Pure Ayurvedic Brahmi & Bhringraj warm herbal oil pressure-point scalp massage.'),
('SPA_DETOX', 'Deep Conditioning Hair Spa', 50, 750.00, 'Intense keratin infusion, ozone steam therapy, scalp revitalizer masque.'),
('SPA_DETOX', 'Anti-Dandruff Scalp Treatment', 45, 850.00, 'Tea-tree clarifying scrub, salicylic scalp wash, and therapeutic cooling masque.'),

-- Facial & Cleanup
('FACIAL_CLEANUP', 'Charcoal Detox D-Tan Cleanup', 35, 700.00, 'Activated charcoal scrub, tan removal peel-off mask, and mint toner.'),
('FACIAL_CLEANUP', 'Herbal Gold Radiance Facial', 50, 1200.00, 'Gold dust micro-exfoliation, ultrasonic toning, hydra-infusion mask.'),
('FACIAL_CLEANUP', 'Instant Glow Face Polishing', 30, 600.00, 'Papaya enzymatic exfoliation, vitamin C serum application, and jade roller finish.'),

-- Groom Bridal Packages
('BRIDAL_GROOM', 'The Royal Groom Makeover Package', 120, 3500.00, 'Full makeover: Signature Haircut, Royal Shave, Gold Facial, D-Tan, and Head Champi.');

-- 4. Customers
INSERT INTO customers (name, phone, email, gender, visits_count, total_spend, notes) VALUES
('Aarav Mehta', '9821011223', 'aarav.m@gmail.com', 'MALE', 5, 4200.00, 'Prefers low skin fade and extra mint cooling after-shave.'),
('Rohan Kapoor', '9819033445', 'rohan.k@outlook.com', 'MALE', 3, 2150.00, 'Sensitive skin on neck. Always use sensitive shaving foam.'),
('Vikramaditya Roy', '9833055667', 'v.roy@yahoo.in', 'MALE', 8, 8900.00, 'VIP regular. Drinks green tea during Head Champi.'),
('Priya Sundaram', '9844077889', 'priya.s@gmail.com', 'FEMALE', 2, 2250.00, 'Global hair color and deep conditioning spa client.'),
('Kabir Sengupta', '9855099001', 'kabir.s@gmail.com', 'MALE', 1, 800.00, 'First time walk-in. Very happy with beard lineup.');

-- 5. Appointments for Today
INSERT INTO appointments (customer_id, barber_id, appointment_date, appointment_time, services_requested, estimated_amount, status, notes) VALUES
(1, 1, CURDATE(), '11:00 AM', 'Signature Haircut & Wash, Beard Sculpting', 750.00, 'COMPLETED', 'Regular client on Chair 1'),
(2, 2, CURDATE(), '12:30 PM', 'Royal Hot Towel Shave, Charcoal Detox D-Tan', 1050.00, 'IN_CHAIR', 'Currently in chair 2 with Kabir'),
(3, 1, CURDATE(), '02:00 PM', 'Traditional Head Champi, Signature Haircut', 900.00, 'SCHEDULED', 'Wants hot green tea on arrival'),
(4, 3, CURDATE(), '03:30 PM', 'Deep Conditioning Hair Spa', 750.00, 'SCHEDULED', 'Consultation for scalp hydration'),
(5, 2, CURDATE(), '05:00 PM', 'Beard Sculpting & Lineup', 300.00, 'SCHEDULED', 'Quick trim before evening meeting');

-- 6. Today's Invoices (Bills)
INSERT INTO bills (bill_number, customer_id, barber_id, subtotal, tax_percent, tax_amount, discount_amount, grand_total, payment_method, payment_status) VALUES
('TGR-2026-1001', 1, 1, 750.00, 5.00, 37.50, 0.00, 787.50, 'UPI', 'PAID'),
('TGR-2026-1002', 3, 2, 1650.00, 5.00, 82.50, 50.00, 1682.50, 'CARD', 'PAID');

-- 7. Bill Items
INSERT INTO bill_items (bill_id, service_id, item_name, item_type, quantity, price) VALUES
(1, 1, 'Signature Haircut & Wash', 'SERVICE', 1, 450.00),
(1, 6, 'Beard Sculpting & Lineup', 'SERVICE', 1, 300.00),
(2, 8, 'Traditional Head Champi (25m)', 'SERVICE', 1, 450.00),
(2, 12, 'Herbal Gold Radiance Facial', 'SERVICE', 1, 1200.00);

-- 8. Inventory & Retail Grooming Products
INSERT INTO inventory (product_name, category, stock_quantity, unit_price, cost_price, alert_threshold, supplier_info) VALUES
('Beard Growth & Shine Oil (50ml)', 'Beard Care', 18, 450.00, 240.00, 5, 'Bombay Shaving Co. Dist.'),
('Matte Finish Hair Clay Wax (80g)', 'Hair Styling', 12, 550.00, 310.00, 4, 'Urban Groom Labs'),
('Activated Charcoal Face Scrub (100g)', 'Skin Care', 8, 399.00, 190.00, 3, 'Khadi Naturals Ltd.'),
('Argan Oil Hair Restorative Serum', 'Hair Care', 4, 699.00, 380.00, 5, 'L’Oreal Professional'),
('Ayurvedic Brahmi Bhringraj Oil (200ml)', 'Scalp Care', 15, 350.00, 180.00, 6, 'Kottakkal Arya Vaidya Sala'),
('Double-Edge Platinum Razor Blades (100pk)', 'Salon Consumables', 25, 450.00, 220.00, 8, 'SuperMax India');
