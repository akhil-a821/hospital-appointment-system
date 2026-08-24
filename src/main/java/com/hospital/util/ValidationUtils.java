package com.hospital.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Validation utilities for form inputs, constraints, and business rules.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s./0-9]{6,15}$"
    );

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidAge(int age) {
        return age >= 0 && age <= 130;
    }

    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 4;
    }

    public static boolean isFutureOrToday(LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(LocalDate.now());
    }

    public static String validatePatientRegistration(String name, String email, String password, String phone, String gender, String ageStr) {
        if (!isNotEmpty(name)) return "Full Name is required.";
        if (name.trim().length() < 2) return "Name must be at least 2 characters long.";
        if (!isValidEmail(email)) return "Please enter a valid email address (e.g. user@example.com).";
        if (!isValidPassword(password)) return "Password must be at least 4 characters long.";
        if (!isValidPhone(phone)) return "Please enter a valid phone number (e.g. +1-555-0199 or 1234567890).";

        if (isNotEmpty(ageStr)) {
            try {
                int age = Integer.parseInt(ageStr.trim());
                if (!isValidAge(age)) {
                    return "Age must be between 0 and 130.";
                }
            } catch (NumberFormatException e) {
                return "Age must be a valid number.";
            }
        }
        return null; // Valid
    }

    public static String validateDoctorForm(String name, String email, String phone, String specialization, String department, String availableDays, String availableTime) {
        if (!isNotEmpty(name)) return "Doctor Name is required.";
        if (!isNotEmpty(specialization)) return "Specialization is required.";
        if (!isNotEmpty(department)) return "Department is required.";
        if (!isValidPhone(phone)) return "Please enter a valid contact phone number.";
        if (isNotEmpty(email) && !isValidEmail(email)) return "Please enter a valid email address.";
        if (!isNotEmpty(availableDays)) return "At least one available day must be selected.";
        if (!isNotEmpty(availableTime)) return "Available working hours must be specified.";
        return null; // Valid
    }
}
