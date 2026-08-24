package com.hospital.controller;

import com.hospital.model.Doctor;
import com.hospital.model.User;
import com.hospital.repository.DepartmentRepository;
import com.hospital.service.AppointmentService;
import com.hospital.service.DashboardService;
import com.hospital.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "tab", required = false, defaultValue = "appointments") String activeTab,
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("stats", dashboardService.getAdminStats());
        model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("appointments", appointmentService.getAllAppointments(status, null, search));
        model.addAttribute("selectedStatus", status != null ? status : "All");
        model.addAttribute("searchTerm", search != null ? search : "");
        model.addAttribute("activeTab", activeTab);

        return "admin-dashboard";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@RequestParam(value = "id", required = false) Integer id,
                             @RequestParam("name") String name,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam("phone") String phone,
                             @RequestParam("specialization") String specialization,
                             @RequestParam("department") String department,
                             @RequestParam("availableDays") String availableDays,
                             @RequestParam("availableTime") String availableTime,
                             @RequestParam(value = "roomNo", required = false, defaultValue = "Room 101") String roomNo,
                             @RequestParam(value = "consultationFee", required = false, defaultValue = "50.00") String feeStr,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        Doctor doc = new Doctor();
        if (id != null && id > 0) {
            doc = doctorService.getDoctorById(id).orElse(new Doctor());
        }

        doc.setName(name);
        doc.setEmail(email);
        doc.setPhone(phone);
        doc.setSpecialization(specialization);
        doc.setDepartment(department);
        doc.setAvailableDays(availableDays);
        doc.setAvailableTime(availableTime);
        doc.setRoomNo(roomNo);

        try {
            doc.setConsultationFee(new BigDecimal(feeStr.trim()));
        } catch (Exception e) {
            doc.setConsultationFee(new BigDecimal("50.00"));
        }

        String error = doctorService.saveDoctor(doc);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Doctor profile saved successfully!");
        }

        return "redirect:/admin/dashboard?tab=doctors";
    }

    @PostMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable("id") Integer doctorId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        String error = doctorService.deleteDoctor(doctorId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Doctor successfully removed.");
        }

        return "redirect:/admin/dashboard?tab=doctors";
    }

    @PostMapping("/appointments/confirm/{id}")
    public String confirmAppointment(@PathVariable("id") Integer appointmentId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        String error = appointmentService.confirmAppointment(appointmentId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Appointment #" + appointmentId + " confirmed successfully!");
        }

        return "redirect:/admin/dashboard?tab=appointments";
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Integer appointmentId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        String error = appointmentService.cancelAppointment(appointmentId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Appointment #" + appointmentId + " cancelled. Slot is now free.");
        }

        return "redirect:/admin/dashboard?tab=appointments";
    }
}
