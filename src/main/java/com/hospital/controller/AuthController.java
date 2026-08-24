package com.hospital.controller;

import com.hospital.model.Role;
import com.hospital.model.User;
import com.hospital.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String root(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/patient/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/patient/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              @RequestParam("role") String roleStr,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Role expectedRole = Role.fromString(roleStr);
        String error = authService.login(email, password, expectedRole, session);

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("selectedRole", roleStr);
            return "redirect:/login";
        }

        return Role.ADMIN.equals(expectedRole) ? "redirect:/admin/dashboard" : "redirect:/patient/dashboard";
    }

    @GetMapping("/demo/patient")
    public String demoPatientLogin(HttpSession session) {
        authService.login("patient@hospital.com", "patient123", Role.PATIENT, session);
        return "redirect:/patient/dashboard";
    }

    @GetMapping("/demo/admin")
    public String demoAdminLogin(HttpSession session) {
        authService.login("admin@hospital.com", "admin123", Role.ADMIN, session);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            return currentUser.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/patient/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("password") String password,
                                 @RequestParam("phone") String phone,
                                 @RequestParam(value = "gender", required = false) String gender,
                                 @RequestParam(value = "age", required = false) String age,
                                 RedirectAttributes redirectAttributes) {

        String error = authService.registerPatient(name, email, password, phone, gender, age);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("gender", gender);
            redirectAttributes.addFlashAttribute("age", age);
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("success", "Account successfully created! Please sign in.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/login";
    }
}
