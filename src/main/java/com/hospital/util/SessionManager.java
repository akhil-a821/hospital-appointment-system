package com.hospital.util;

import com.hospital.model.Admin;
import com.hospital.model.Patient;
import com.hospital.model.Role;
import com.hospital.model.User;

/**
 * Manages the current logged-in user session.
 */
public class SessionManager {

    private static User currentUser = null;

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public static synchronized boolean isAdmin() {
        return currentUser != null && Role.ADMIN.equals(currentUser.getRole());
    }

    public static synchronized boolean isPatient() {
        return currentUser != null && Role.PATIENT.equals(currentUser.getRole());
    }

    public static synchronized Patient getCurrentPatient() {
        if (currentUser instanceof Patient) {
            return (Patient) currentUser;
        } else if (currentUser != null && currentUser.isPatient()) {
            return Patient.fromUser(currentUser);
        }
        return null;
    }

    public static synchronized Admin getCurrentAdmin() {
        if (currentUser instanceof Admin) {
            return (Admin) currentUser;
        } else if (currentUser != null && currentUser.isAdmin()) {
            return Admin.fromUser(currentUser);
        }
        return null;
    }

    public static synchronized void logout() {
        currentUser = null;
    }
}
