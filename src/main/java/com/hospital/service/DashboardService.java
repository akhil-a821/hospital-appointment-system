package com.hospital.service;

import com.hospital.model.AppointmentStatus;
import com.hospital.model.Role;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public static class AdminStats {
        public long totalPatients;
        public long totalDoctors;
        public long totalAppointments;
        public long pendingAppointments;
        public long confirmedAppointments;
        public long cancelledAppointments;
    }

    public static class PatientStats {
        public long totalBookings;
        public long pendingBookings;
        public long confirmedBookings;
        public long cancelledBookings;
    }

    public AdminStats getAdminStats() {
        AdminStats s = new AdminStats();
        s.totalPatients = userRepository.countByRole(Role.PATIENT);
        s.totalDoctors = doctorRepository.count();
        s.totalAppointments = appointmentRepository.count();
        s.pendingAppointments = appointmentRepository.countByStatus(AppointmentStatus.PENDING);
        s.confirmedAppointments = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
        s.cancelledAppointments = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
        return s;
    }

    public PatientStats getPatientStats(Integer patientId) {
        PatientStats s = new PatientStats();
        s.totalBookings = appointmentRepository.countByPatientId(patientId);
        s.pendingBookings = appointmentRepository.countByPatientIdAndStatus(patientId, AppointmentStatus.PENDING);
        s.confirmedBookings = appointmentRepository.countByPatientIdAndStatus(patientId, AppointmentStatus.CONFIRMED);
        s.cancelledBookings = appointmentRepository.countByPatientIdAndStatus(patientId, AppointmentStatus.CANCELLED);
        return s;
    }
}
