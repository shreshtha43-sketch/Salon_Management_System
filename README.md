# The Groom Room - Luxury Indian Salon Management System

> **A Premium Desktop Management & Billing Suite Crafted Exclusively in Pure Java (Swing & AWT)**

---

## ✂️ Overview & Salon Identity
**The Groom Room** is an Indian salon and luxury grooming lounge desktop management application. Built 100% in pure Java with **zero CSS, zero HTML, and zero Tailwind**, it delivers an engaging aesthetic featuring a **classy walnut brown, warm espresso, and amber gold** palette, smooth anti-aliased geometry, and custom Java2D UI components.

Designed specifically for the Indian market:
- **Currency**: Indian Rupees (**₹ INR**) throughout the entire application.
- **GST Invoicing**: Automatic 5% Salon GST breakdown (2.5% CGST + 2.5% SGST) with printable tax receipts.
- **Service Catalog**: Tailored Indian grooming services (Traditional Head Champi, Royal Hot Towel Shave, Beard Sculpting, Charcoal D-Tan Cleanup, Herbal Gold Radiance Facial, Bridal Groom Packages).
- **Payment Modes**: Direct support for **UPI** (Google Pay / PhonePe / Paytm), Cash, Debit/Credit Card, and Net Banking.

---

## 👥 3 Distinct Role Interfaces & Credentials

The login screen features 3 portal interfaces with dedicated workspaces and **One-Click Quick Demo Login** buttons:

| Role Portal | Username | Password | Key Responsibilities & Features |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | • Executive KPI Cards (Today's Revenue ₹, Bookings, Active Staff)<br>• Pure Java2D 7-Day Revenue Trend Bar Chart<br>• Stylist Commission Leaderboard & Cuts Ranking<br>• Full Staff, Service Catalog & Retail Inventory Management<br>• Customer Directory & Database Config |
| **Receptionist** | `reception` | `recep123` | • Front Desk Appointment Calendar & Queue<br>• Quick Walk-In POS Billing with live GST (₹) calculation<br>• Instant Tax Invoice Generation & Thermal Print Preview<br>• Live Salon Chairs / Station Status Monitor<br>• Client Directory with past visit history |
| **Barber / Stylist** | `barber_vikram`<br>`barber_kabir`<br>`barber_arjun` | `barber123` | • Station-specific Chair View (e.g. Chair 1, 2, 3)<br>• Active Client Card with "Start Service" & "Mark Completed"<br>• Personal Daily Earnings & Commission Tracker (₹)<br>• Today's Assigned Queue & Shift Availability Toggle |

---

## 🗄️ Database Connection & MySQL Setup

The application features an **Intelligent Dual-Mode Architecture**:
1. **Out-of-the-Box Local Mode**: Runs immediately without needing MySQL to be pre-started, pre-seeded with realistic Indian salon dummy records (services, barbers, appointments, and customers).
2. **Full-Fledged MySQL Connection**: When you are ready to connect to your MySQL database:
   - Click the **"Configure Database & Root Password"** button on the Login screen (or the **"Database Config"** sidebar button in any dashboard).
   - Enter your MySQL server details:
     - **Host**: `localhost`
     - **Port**: `3306`
     - **Database Name**: `the_groom_room`
     - **Username**: `root`
     - **Password**: *[Enter your MySQL root password]*
   - Click **"Test Connection"** to verify connectivity.
   - Click **"Initialize Database & Tables"** to automatically create `the_groom_room`, create all 8 relational tables, and seed them with initial data!

### Manual MySQL Import (Optional)
If you prefer importing via MySQL CLI, MySQL Workbench, or phpMyAdmin:
```bash
mysql -u root -p < database/schema.sql
```

The database configuration is saved in `db_config.properties`:
```properties
db.host=localhost
db.port=3306
db.name=the_groom_room
db.user=root
db.password=YOUR_ROOT_PASSWORD
db.use_ssl=false
db.allow_public_key_retrieval=true
```

---

## 🚀 How to Run the Application

### Option A: One-Click Launch (Windows)
Double-click `run.bat` in the `The_Groom_Room` folder, or open terminal and execute:
```cmd
cd The_Groom_Room
run.bat
```

### Option B: Recompile from Source
If you make code modifications, recompile cleanly with:
```cmd
cd The_Groom_Room
compile.bat
```

Or manually using `javac`:
```cmd
javac -encoding UTF-8 -cp "lib\mysql-connector-j.jar;bin" -d bin src\com\thegroomroom\*.java src\com\thegroomroom\config\*.java src\com\thegroomroom\model\*.java src\com\thegroomroom\dao\*.java src\com\thegroomroom\ui\theme\*.java src\com\thegroomroom\ui\components\*.java src\com\thegroomroom\ui\dialogs\*.java src\com\thegroomroom\ui\auth\*.java src\com\thegroomroom\ui\admin\*.java src\com\thegroomroom\ui\receptionist\*.java src\com\thegroomroom\ui\barber\*.java
```

---

## 🎨 Aesthetic & Styling Architecture

- **Color Harmony**:
  - `Deep Espresso` (`#1C130D`): Header, sidebar background, and brand identity.
  - `Warm Walnut` (`#241912`): Sidebar container and active card borders.
  - `Cognac Amber Gold` (`#C58D54`): Primary button accents, badges, and focus rings.
  - `Soft Silk Ivory` (`#F8F5F0`): Warm canvas background (replaces generic harsh gray).
  - `Pure White` (`#FFFFFF`): Elevated card surfaces.
- **Custom Java Components**:
  - `RoundedPanel`: Anti-aliased card container with customizable corner radius and subtle shadow.
  - `ModernButton`: Anti-aliased rounded buttons with dynamic hover/press states.
  - `ModernTable`: Custom table header in deep espresso, alternating zebra rows in silk ivory, 40px comfortable row height, and status badge cell renderers.
  - `ModernTextField` / `ModernPasswordField`: Rounded input fields with placeholder text and warm cognac focus glow.
  - `Badge`: Status tags (`AVAILABLE`, `IN_CHAIR`, `COMPLETED`, `PAID`, `LOW_STOCK`).
  - `KpiCard`: Financial and operational metric display with symbol badges.

---

## 📁 Project Structure

```
The_Groom_Room/
├── bin/                             # Compiled Java class binaries
├── database/
│   └── schema.sql                   # Complete MySQL DDL & Indian salon seed data
├── lib/
│   └── mysql-connector-j.jar        # Official MySQL JDBC Driver
├── src/
│   └── com/
│       └── thegroomroom/
│           ├── Main.java            # Desktop entrypoint & Swing EDT bootstrapper
│           ├── config/
│           │   ├── DBConnection.java        # JDBC connection manager & config parser
│           │   └── DatabaseInitializer.java # Auto-creates DB & executes schema
│           ├── dao/
│           │   └── DataStore.java           # Central repository & offline-first cache
│           ├── model/
│           │   ├── Appointment.java
│           │   ├── Barber.java
│           │   ├── Bill.java
│           │   ├── BillItem.java
│           │   ├── Customer.java
│           │   ├── InventoryItem.java
│           │   ├── ServiceItem.java
│           │   └── User.java
│           └── ui/
│               ├── theme/
│               │   └── UITheme.java         # Classy brown palette & typography
│               ├── components/
│               │   ├── Badge.java
│               │   ├── KpiCard.java
│               │   ├── ModernButton.java
│               │   ├── ModernPasswordField.java
│               │   ├── ModernTable.java
│               │   ├── ModernTextField.java
│               │   └── RoundedPanel.java
│               ├── dialogs/
│               │   ├── AddAppointmentDialog.java
│               │   ├── AddBarberDialog.java
│               │   ├── AddInventoryDialog.java
│               │   ├── AddServiceDialog.java
│               │   ├── DatabaseSettingsDialog.java
│               │   └── InvoiceReceiptDialog.java
│               ├── auth/
│               │   └── LoginFrame.java      # 3-role switcher & quick demo login
│               ├── admin/
│               │   └── AdminDashboardFrame.java
│               ├── receptionist/
│               │   └── ReceptionistDashboardFrame.java
│               └── barber/
│                   └── BarberDashboardFrame.java
├── compile.bat                      # One-click build script
├── run.bat                          # One-click launcher script
├── db_config.properties             # Config file for MySQL credentials
└── README.md                        # Project documentation
```
