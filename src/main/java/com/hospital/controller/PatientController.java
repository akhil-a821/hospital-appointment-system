package com.hospital.controller;

import com.hospital.model.Appointment;
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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "department", required = false) String department,
                            @RequestParam(value = "tab", required = false, defaultValue = "doctors") String activeTab,
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isPatient()) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("stats", dashboardService.getPatientStats(user.getId()));
        model.addAttribute("departments", departmentRepository.findAllByOrderByNameAsc());
        model.addAttribute("doctors", doctorService.searchDoctors(search, department));
        model.addAttribute("appointments", appointmentService.getPatientAppointments(user.getId()));
        model.addAttribute("selectedDept", department != null ? department : "All Departments");
        model.addAttribute("searchTerm", search != null ? search : "");
        model.addAttribute("activeTab", activeTab);

        return "patient-dashboard";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam("doctorId") Integer doctorId,
                                  @RequestParam("department") String department,
                                  @RequestParam("appointmentDate") String dateStr,
                                  @RequestParam("appointmentTime") String timeSlot,
                                  @RequestParam("reason") String reason,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isPatient()) {
            return "redirect:/login";
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format.");
            return "redirect:/patient/dashboard?tab=doctors";
        }

        String error = appointmentService.bookAppointment(user.getId(), doctorId, department, date, timeSlot, reason);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/patient/dashboard?tab=doctors";
        }

        redirectAttributes.addFlashAttribute("success", "Appointment successfully booked for " + date + " at " + timeSlot + "!");
        return "redirect:/patient/dashboard?tab=appointments";
    }

    @PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Integer appointmentId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null || !user.isPatient()) {
            return "redirect:/login";
        }

        String error = appointmentService.cancelAppointment(appointmentId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Appointment #" + appointmentId + " cancelled successfully. The slot is now free.");
        }

        return "redirect:/patient/dashboard?tab=appointments";
    }
}
