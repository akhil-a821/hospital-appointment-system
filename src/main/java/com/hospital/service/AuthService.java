package com.hospital.service;

import com.hospital.model.Role;
import com.hospital.model.User;
import com.hospital.repository.UserRepository;
import com.hospital.util.PasswordUtils;
import com.hospital.util.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public String registerPatient(String name, String email, String password, String phone, String gender, String ageStr) {
        String err = ValidationUtils.validatePatientRegistration(name, email, password, phone, gender, ageStr);
        if (err != null) return err;

        if (userRepository.existsByEmailIgnoreCase(email.trim())) {
            return "An account with email '" + email.trim() + "' already exists. Please login instead.";
        }

        int age = 0;
        if (ValidationUtils.isNotEmpty(ageStr)) {
            try {
                age = Integer.parseInt(ageStr.trim());
            } catch (Exception ignored) {}
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        User user = new User(name.trim(), email.trim().toLowerCase(), hashedPassword, Role.PATIENT, phone.trim(), gender, age);
        userRepository.save(user);
        return null; // Success
    }

    public String login(String email, String password, Role expectedRole) {
        if (!ValidationUtils.isNotEmpty(email) || !ValidationUtils.isNotEmpty(password)) {
            return "Please enter both email and password.";
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            return "Invalid email or password.";
        }

        User user = userOpt.get();
        if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
            return "Invalid email or password.";
        }

        if (expectedRole != null && !user.getRole().equals(expectedRole)) {
            return "Access denied: Account is not authorized for " + expectedRole.getDisplayName() + " portal.";
        }

        return null;
    }

    public String login(String email, String password, Role expectedRole, HttpSession session) {
        if (!ValidationUtils.isNotEmpty(email) || !ValidationUtils.isNotEmpty(password)) {
            return "Please enter both email and password.";
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            return "Invalid email or password.";
        }

        User user = userOpt.get();
        if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
            return "Invalid email or password.";
        }

        if (expectedRole != null && !user.getRole().equals(expectedRole)) {
            return "Access denied: Account is not authorized for " + expectedRole.getDisplayName() + " portal.";
        }

        session.setAttribute("currentUser", user);
        return null; // Success
    }

    public void logout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.invalidate();
    }
}
