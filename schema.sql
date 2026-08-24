-- =========================================================
-- Hospital Appointment Scheduling System - Database Schema
-- Database: MySQL
-- =========================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Users Table (Patients and Admins)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'PATIENT',
    phone VARCHAR(20),
    gender VARCHAR(10),
    age INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    department_id INT,
    department VARCHAR(100) NOT NULL,
    available_days VARCHAR(100) NOT NULL DEFAULT 'Monday,Tuesday,Wednesday,Thursday,Friday',
    available_time VARCHAR(100) NOT NULL DEFAULT '09:00 - 17:00',
    room_no VARCHAR(50) DEFAULT 'Room 101',
    consultation_fee DECIMAL(10, 2) DEFAULT 50.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- 4. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    department VARCHAR(100) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time VARCHAR(20) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- =========================================================
-- SEED DATA
-- =========================================================

-- Insert Departments
INSERT IGNORE INTO departments (id, name, description) VALUES
(1, 'Cardiology', 'Specialized care for heart and cardiovascular disorders'),
(2, 'Neurology', 'Diagnosis and treatment of nervous system and brain conditions'),
(3, 'Orthopedics', 'Treatment of musculoskeletal system, bones, and joints'),
(4, 'Pediatrics', 'Comprehensive medical care for infants, children, and adolescents'),
(5, 'Dermatology', 'Treatment of skin, hair, and nail conditions'),
(6, 'General Medicine', 'Primary healthcare, diagnosis, and preventative medical care'),
(7, 'Ophthalmology', 'Complete eye care, vision exams, and optical surgeries'),
(8, 'ENT (Otolaryngology)', 'Ear, nose, throat, and head & neck medical care');

-- Insert Pre-seeded Users (Passwords hashed with SHA-256)
-- admin123   -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- patient123 -> 55525110904ab719ef16ff5ff57a070e637feeb1b00e31d87e07a38fe41121d5
-- password123 -> ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f

INSERT IGNORE INTO users (id, name, email, password, role, phone, gender, age) VALUES
(1, 'Hospital Administrator', 'admin@hospital.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', '+1-555-0100', 'Other', 35),
(2, 'John Doe', 'patient@hospital.com', '55525110904ab719ef16ff5ff57a070e637feeb1b00e31d87e07a38fe41121d5', 'PATIENT', '+1-555-0199', 'Male', 29),
(3, 'Sarah Connor', 'sarah.smith@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT', '+1-555-0188', 'Female', 34);

-- Insert Doctors
INSERT IGNORE INTO doctors (id, name, email, phone, specialization, department_id, department, available_days, available_time, room_no, consultation_fee) VALUES
(1, 'Dr. Robert Harrison', 'robert.harrison@hospital.com', '+1-555-0111', 'Senior Interventional Cardiologist', 1, 'Cardiology', 'Monday,Wednesday,Friday', '09:00 - 16:00', 'Room 301', 80.00),
(2, 'Dr. Elena Rostova', 'elena.rostova@hospital.com', '+1-555-0112', 'Consultant Neurologist', 2, 'Neurology', 'Tuesday,Thursday,Saturday', '10:00 - 17:00', 'Room 402', 90.00),
(3, 'Dr. Marcus Vance', 'marcus.vance@hospital.com', '+1-555-0113', 'Orthopedic & Joint Surgeon', 3, 'Orthopedics', 'Monday,Tuesday,Thursday', '09:00 - 15:00', 'Room 205', 75.00),
(4, 'Dr. Maya Patel', 'maya.patel@hospital.com', '+1-555-0114', 'Pediatric Specialist', 4, 'Pediatrics', 'Monday,Tuesday,Wednesday,Thursday,Friday', '08:30 - 16:30', 'Room 108', 65.00),
(5, 'Dr. James Chen', 'james.chen@hospital.com', '+1-555-0115', 'Clinical Dermatologist', 5, 'Dermatology', 'Wednesday,Friday,Saturday', '11:00 - 18:00', 'Room 214', 70.00),
(6, 'Dr. Claire Bennett', 'claire.bennett@hospital.com', '+1-555-0116', 'General Physician & Internist', 6, 'General Medicine', 'Monday,Tuesday,Wednesday,Thursday,Friday', '08:00 - 17:00', 'Room 102', 50.00),
(7, 'Dr. Alexander Wright', 'alex.wright@hospital.com', '+1-555-0117', 'Ophthalmic Surgeon', 7, 'Ophthalmology', 'Tuesday,Wednesday,Friday', '09:30 - 16:00', 'Room 312', 75.00),
(8, 'Dr. Sophia Morales', 'sophia.morales@hospital.com', '+1-555-0118', 'ENT Head & Neck Specialist', 8, 'ENT (Otolaryngology)', 'Monday,Thursday,Friday', '10:00 - 16:30', 'Room 220', 70.00);

-- Insert Sample Appointments
INSERT IGNORE INTO appointments (id, patient_id, doctor_id, department, appointment_date, appointment_time, reason, status) VALUES
(1, 2, 1, 'Cardiology', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00 AM', 'Routine cardiovascular health checkup and ECG review', 'Confirmed'),
(2, 2, 6, 'General Medicine', DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:00 AM', 'Seasonal flu symptoms and general consultation', 'Pending'),
(3, 3, 4, 'Pediatrics', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '11:00 AM', 'Child routine immunization and developmental check', 'Confirmed'),
(4, 2, 5, 'Dermatology', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '02:00 PM', 'Skin allergy consultation and follow up', 'Cancelled');
