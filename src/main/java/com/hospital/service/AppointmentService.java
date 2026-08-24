package com.hospital.service;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Doctor;
import com.hospital.model.User;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import com.hospital.util.DateUtils;
import com.hospital.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    public String bookAppointment(Integer patientId, Integer doctorId, String department, LocalDate date, String timeSlot, String reason) {
        if (patientId == null || patientId <= 0) return "Invalid patient account.";
        if (doctorId == null || doctorId <= 0) return "Please select a doctor.";
        if (date == null) return "Please select an appointment date.";
        if (!ValidationUtils.isNotEmpty(timeSlot)) return "Please select an appointment time slot.";
        if (!ValidationUtils.isNotEmpty(reason)) return "Please enter a reason for the medical visit.";

        if (DateUtils.isPastDate(date)) {
            return "Appointment date cannot be in the past. Please select today or a future date.";
        }

        Optional<Doctor> docOpt = doctorRepository.findById(doctorId);
        if (docOpt.isEmpty()) {
            return "Selected doctor does not exist.";
        }
        Doctor doctor = docOpt.get();

        String dayName = DateUtils.getDayOfWeekName(date);
        if (!doctor.isAvailableOnDay(dayName)) {
            return doctor.getName() + " is not available on " + dayName + "s. Available days: " + doctor.getAvailableDays();
        }

        // Double-Booking Prevention
        if (appointmentRepository.isSlotBooked(doctorId, date, timeSlot.trim())) {
            return "Conflict: " + doctor.getName() + " already has an active appointment booked for " + date + " at " + timeSlot + ". Please choose another slot.";
        }

        Appointment appt = new Appointment(
                patientId,
                doctorId,
                (department != null && !department.isBlank()) ? department : doctor.getDepartment(),
                date,
                timeSlot.trim(),
                reason.trim()
        );
        appt.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appt);
        return null; // Success
    }

    public String cancelAppointment(Integer appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return "Appointment not found.";

        Appointment appt = opt.get();
        if (appt.isCancelled()) return "Appointment is already cancelled.";

        appt.setStatus(AppointmentStatus.CANCELLED);
        appt.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appt);
        return null;
    }

    public String confirmAppointment(Integer appointmentId) {
        Optional<Appointment> opt = appointmentRepository.findById(appointmentId);
        if (opt.isEmpty()) return "Appointment not found.";

        Appointment appt = opt.get();
        if (appt.isConfirmed()) return "Appointment is already confirmed.";
        if (appt.isCancelled()) return "Cannot confirm a cancelled appointment.";

        appt.setStatus(AppointmentStatus.CONFIRMED);
        appt.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appt);
        return null;
    }

    public List<Appointment> getPatientAppointments(Integer patientId) {
        List<Appointment> list = appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDescIdDesc(patientId);
        for (Appointment a : list) {
            enrichAppointmentDetails(a);
        }
        return list;
    }

    public List<Appointment> getAllAppointments(String status, Integer doctorId, String searchKeyword) {
        AppointmentStatus statusEnum = null;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("All")) {
            statusEnum = AppointmentStatus.fromString(status);
        }

        List<Appointment> list = appointmentRepository.findAllWithFilters(status, statusEnum, doctorId);
        for (Appointment a : list) {
            enrichAppointmentDetails(a);
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String kw = searchKeyword.toLowerCase().trim();
            list = list.stream().filter(a ->
                    (a.getPatientName() != null && a.getPatientName().toLowerCase().contains(kw)) ||
                    (a.getDoctorName() != null && a.getDoctorName().toLowerCase().contains(kw)) ||
                    (a.getDepartment() != null && a.getDepartment().toLowerCase().contains(kw)) ||
                    (a.getReason() != null && a.getReason().toLowerCase().contains(kw))
            ).toList();
        }

        return list;
    }

    private void enrichAppointmentDetails(Appointment a) {
        userRepository.findById(a.getPatientId()).ifPresent(u -> {
            a.setPatientName(u.getName());
            a.setPatientEmail(u.getEmail());
            a.setPatientPhone(u.getPhone());
        });
        doctorRepository.findById(a.getDoctorId()).ifPresent(d -> {
            a.setDoctorName(d.getName());
            a.setDoctorSpecialization(d.getSpecialization());
        });
    }
}
