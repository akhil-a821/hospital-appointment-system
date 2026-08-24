package com.hospital;

import com.hospital.model.Role;
import com.hospital.service.AuthService;
import com.hospital.util.DatabaseInitializer;

public class DiagnosticTest {
    public static void main(String[] args) {
        System.out.println("=== Diagnostic Database Test ===");
        boolean init = DatabaseInitializer.initializeDatabase();
        System.out.println("Init success: " + init);

        AuthService auth = new AuthService();
        String patientLogin = auth.login("patient@hospital.com", "patient123", Role.PATIENT);
        System.out.println("Patient login: " + (patientLogin == null ? "SUCCESS (Logged in as Patient)" : "FAILED: " + patientLogin));

        String adminLogin = auth.login("admin@hospital.com", "admin123", Role.ADMIN);
        System.out.println("Admin login:   " + (adminLogin == null ? "SUCCESS (Logged in as Admin)" : "FAILED: " + adminLogin));
    }
}
