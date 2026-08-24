package com.hospital;

import com.hospital.model.*;
import com.hospital.util.DateUtils;
import com.hospital.util.PasswordUtils;
import com.hospital.util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Core System Unit & Logic Verification.
 */
public class SystemTest {

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        System.out.println("==================================================");
        System.out.println("  Hospital Appointment System - Automated Tests   ");
        System.out.println("==================================================");

        // Test 1: Password Hashing
        try {
            String raw = "patient123";
            String hash = PasswordUtils.hashPassword(raw);
            assert hash != null && !hash.equals(raw) : "Hash should not be raw";
            assert PasswordUtils.verifyPassword("patient123", hash) : "Password verification failed";
            assert !PasswordUtils.verifyPassword("wrongpass", hash) : "Password mismatch should fail";
            System.out.println("[PASS] 1. Password Hashing and Verification");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 1. Password Hashing: " + t.getMessage());
            failed++;
        }

        // Test 2: Validation Utils
        try {
            assert ValidationUtils.isValidEmail("john.doe@hospital.com") : "Valid email rejected";
            assert !ValidationUtils.isValidEmail("invalid-email") : "Invalid email accepted";
            assert ValidationUtils.isValidPhone("+1-555-0199") : "Valid phone rejected";
            assert !ValidationUtils.isValidPassword("12") : "Short password accepted";
            assert ValidationUtils.isValidPassword("secret123") : "Valid password rejected";
            System.out.println("[PASS] 2. Validation Utilities (Email, Phone, Password)");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 2. Validation Utils: " + t.getMessage());
            failed++;
        }

        // Test 3: Past Date Validation
        try {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate tomorrow = today.plusDays(1);

            assert DateUtils.isPastDate(yesterday) : "Yesterday should be past date";
            assert !DateUtils.isPastDate(today) : "Today should not be past date";
            assert !DateUtils.isPastDate(tomorrow) : "Tomorrow should not be past date";
            System.out.println("[PASS] 3. Past Date Rejection Logic");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 3. Past Date Logic: " + t.getMessage());
            failed++;
        }

        // Test 4: Doctor Model & Day Availability
        try {
            Doctor doc = new Doctor(1, "Dr. Robert", "doc@hospital.com", "+1-555-0100",
                    "Cardiologist", 1, "Cardiology",
                    "Monday,Wednesday,Friday", "09:00 - 17:00",
                    "Room 101", new BigDecimal("75.00"));

            assert doc.isAvailableOnDay("Monday") : "Should be available on Monday";
            assert doc.isAvailableOnDay("Wednesday") : "Should be available on Wednesday";
            assert doc.isAvailableOnDay("Friday") : "Should be available on Friday";
            assert !doc.isAvailableOnDay("Tuesday") : "Should NOT be available on Tuesday";
            assert !doc.isAvailableOnDay("Sunday") : "Should NOT be available on Sunday";
            System.out.println("[PASS] 4. Doctor Day-of-Week Availability Schedule");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 4. Doctor Schedule: " + t.getMessage());
            failed++;
        }

        // Test 5: Dynamic Time Slot Generation
        try {
            List<String> slots = DateUtils.generateTimeSlotsForDoctor("09:00 - 12:00");
            assert slots.contains("09:00 AM") : "Slot 09:00 AM missing";
            assert slots.contains("09:30 AM") : "Slot 09:30 AM missing";
            assert slots.contains("10:00 AM") : "Slot 10:00 AM missing";
            assert slots.contains("11:30 AM") : "Slot 11:30 AM missing";
            System.out.println("[PASS] 5. Dynamic Time Slot Generation");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 5. Time Slot Generation: " + t.getMessage());
            failed++;
        }

        // Test 6: Role Based Model Polymorphism
        try {
            User adminUser = new Admin(1, "Admin User", "admin@hospital.com", "hash", "+12345");
            User patientUser = new Patient(2, "Jane Doe", "jane@hospital.com", "hash", "+12345", "Female", 28);

            assert adminUser.isAdmin() : "AdminUser should return true for isAdmin";
            assert !adminUser.isPatient() : "AdminUser should return false for isPatient";
            assert patientUser.isPatient() : "PatientUser should return true for isPatient";
            assert !patientUser.isAdmin() : "PatientUser should return false for isAdmin";
            assert adminUser.getRole() == Role.ADMIN : "Role should be ADMIN";
            assert patientUser.getRole() == Role.PATIENT : "Role should be PATIENT";
            System.out.println("[PASS] 6. OOP Polymorphism & Role-Based Access Models");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 6. Role Models: " + t.getMessage());
            failed++;
        }

        // Test 7: Appointment Lifecycle Transitions
        try {
            Appointment appt = new Appointment(1, 2, 1, "Cardiology", LocalDate.now().plusDays(2), "10:00 AM", "Checkup", AppointmentStatus.PENDING);
            assert appt.isPending() : "Initial status should be Pending";
            assert !appt.isConfirmed() : "Initial status should not be Confirmed";

            appt.setStatus(AppointmentStatus.CONFIRMED);
            assert appt.isConfirmed() : "Status should be Confirmed";

            appt.setStatus(AppointmentStatus.CANCELLED);
            assert appt.isCancelled() : "Status should be Cancelled";
            System.out.println("[PASS] 7. Appointment Lifecycle Status Transitions");
            passed++;
        } catch (Throwable t) {
            System.err.println("[FAIL] 7. Status Transitions: " + t.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.println("Test Results: " + passed + " Passed, " + failed + " Failed.");
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
