# CarePulse — Hospital Appointment Scheduling System (Java Swing + MySQL + JDBC + Maven)

A complete, production-grade **Hospital Appointment Scheduling System (MVP)** developed in pure **Java** featuring a modern **Java Swing GUI**, **JDBC**, **MySQL**, and **Maven**.

---

## 🌟 Key Highlights & Presentation Points

This project is tailored for academic evaluation and professional demonstration:
1. **Java OOP**:
   - **Inheritance & Polymorphism**: `User` base class extended by `Patient` and `Admin`.
   - **Encapsulation**: Strict private/protected state with validated domain models (`Doctor`, `Department`, `Appointment`).
   - **Abstraction & Separation of Concerns**: Multi-tier architecture dividing **Models**, **DAOs**, **Services**, **Views**, and **Utilities**.
2. **Modern Java Swing GUI**:
   - Styled with **FlatLaf** design system for a sleek medical-teal desktop interface.
   - Interactive CardLayout, real-time doctor search & department filters, dynamic appointment time slot picker with visual booked vs available indicators, and custom pill status badges.
3. **Robust JDBC & MySQL Database**:
   - Parameterized SQL PreparedStatements preventing SQL Injection.
   - Dynamic database connection management and auto-schema initialisation from `schema.sql`.
   - In-app **Database Connection Settings** dialog for real-time configuration without recompilation.
4. **Strict Business Logic & Validations**:
   - **Double-booking Prevention**: A doctor cannot have two active appointments at the same date and time.
   - **Past Date Prevention**: Rejects past booking dates; enforces future/today date selection.
   - **Doctor Availability Match**: Dynamically maps appointment day-of-week against doctor's working days.
   - **Cancelled Slot Re-booking**: Cancelled appointments instantly free up the slot for other patients.

---

## 🏗️ Architecture Overview

```
src/
├── main/
│   ├── java/com/hospital/
│   │   ├── Main.java                          # Application Entry Point & LookAndFeel Setup
│   │   ├── model/                             # Domain Entity Models
│   │   │   ├── Role.java                      # Enum (PATIENT, ADMIN)
│   │   │   ├── AppointmentStatus.java         # Enum (PENDING, CONFIRMED, CANCELLED)
│   │   │   ├── User.java                      # Base User class
│   │   │   ├── Patient.java                   # Patient subclass
│   │   │   ├── Admin.java                     # Admin subclass
│   │   │   ├── Department.java                # Department entity
│   │   │   ├── Doctor.java                    # Doctor entity
│   │   │   └── Appointment.java               # Appointment entity
│   │   ├── dao/                               # JDBC Data Access Objects
│   │   │   ├── UserDAO.java                   # User authentication & registration
│   │   │   ├── DepartmentDAO.java             # Department queries
│   │   │   ├── DoctorDAO.java                 # Doctor CRUD & filtering
│   │   │   └── AppointmentDAO.java            # Appointment persistence & slot conflict checks
│   │   ├── service/                           # Business Logic Services
│   │   │   ├── AuthService.java               # Login & Registration workflows
│   │   │   ├── DoctorService.java             # Doctor management & dynamic slot availability
│   │   │   ├── AppointmentService.java        # Booking, Confirm, and Cancel logic
│   │   │   └── DashboardService.java          # Aggregated metrics for Admin & Patient
│   │   ├── util/                              # Utilities
│   │   │   ├── DBConnection.java              # JDBC Singleton & config reader
│   │   │   ├── DatabaseInitializer.java       # Automatic schema loader
│   │   │   ├── PasswordUtils.java             # SHA-256 password hashing
│   │   │   ├── ValidationUtils.java           # Form validation & constraints
│   │   │   ├── DateUtils.java                 # Date formatting & time slot generation
│   │   │   ├── SessionManager.java            # Active authenticated user state
│   │   │   └── UIUtils.java                   # Colors, typography, component factories
│   │   └── view/                              # Modern Swing GUI
│   │       ├── MainFrame.java                 # Main window container & top navigation
│   │       ├── auth/                          # Login & Patient Registration panels
│   │       ├── patient/                       # Patient Dashboard, Doctor Directory, Booking modal, My Appointments
│   │       ├── admin/                         # Admin Dashboard, Manage Doctors, Manage Appointments
│   │       ├── common/                        # CustomTable, StatusBadge, StatCard
│   │       └── dialog/                        # DatabaseConfigDialog
│   └── resources/
│       ├── db.properties                      # MySQL connection configuration
│       └── schema.sql                         # Database DDL and rich seed data
└── test/
    └── java/com/hospital/SystemTest.java       # Automated unit test suite
```

---

## 🗄️ Database Tables (`schema.sql`)

- `departments`: `id`, `name`, `description`, `created_at`
- `users`: `id`, `name`, `email`, `password`, `role` (`PATIENT`/`ADMIN`), `phone`, `gender`, `age`, `created_at`
- `doctors`: `id`, `name`, `email`, `phone`, `specialization`, `department_id`, `department`, `available_days`, `available_time`, `room_no`, `consultation_fee`, `created_at`
- `appointments`: `id`, `patient_id`, `doctor_id`, `department`, `appointment_date`, `appointment_time`, `reason`, `status` (`Pending`, `Confirmed`, `Cancelled`), `created_at`, `updated_at`

---

## 🔑 Pre-seeded Demo Credentials

For quick presentation and evaluation, use the one-click demo fill buttons on the login screen or enter:

| Role | Email | Password | Pre-seeded Features |
| :--- | :--- | :--- | :--- |
| **Patient** | `patient@hospital.com` | `patient123` | Active bookings, doctor directory, booking modal |
| **Admin** | `admin@hospital.com` | `admin123` | Admin KPI dashboard, Doctor CRUD, Appointment Confirm/Cancel |

---

## 🚀 How to Run

### Method 1: Double-Click or Run Batch Script (Zero-config)
Double click `run.bat` or `build_and_run.bat` in the project folder.

### Method 2: Command Line (Java 17+)
```bash
# Compile
javac -d target/classes -cp "lib/*;src/main/resources" src/main/java/com/hospital/**/*.java

# Run
java -cp "target/classes;lib/*" com.hospital.Main
```

### Method 3: Maven
```bash
# Compile and package
mvn clean compile

# Run
mvn exec:java
```

### Method 4: Any IDE (Eclipse / IntelliJ / VS Code / NetBeans)
1. Open the project root folder as a Maven project.
2. Ensure Project SDK is set to Java 17+.
3. Run `com.hospital.Main.java`.

---

## 🧪 Automated Unit & Logic Tests

Run the included automated test suite verifying password hashing, appointment transitions, doctor availability, and past-date validation:
```bash
javac -d target/classes -cp "target/classes;lib/*" src/test/java/com/hospital/SystemTest.java
java -ea -cp "target/classes;lib/*" com.hospital.SystemTest
```
Result: `7 Passed, 0 Failed`.
