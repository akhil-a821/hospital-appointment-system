package com.hospital.service;

import com.hospital.model.*;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import com.hospital.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        // Seed Departments
        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department("Cardiology", "Specialized care for heart and cardiovascular disorders"));
            departmentRepository.save(new Department("Neurology", "Diagnosis and treatment of nervous system and brain conditions"));
            departmentRepository.save(new Department("Orthopedics", "Treatment of musculoskeletal system, bones, and joints"));
            departmentRepository.save(new Department("Pediatrics", "Comprehensive medical care for infants, children, and adolescents"));
            departmentRepository.save(new Department("Dermatology", "Treatment of skin, hair, and nail conditions"));
            departmentRepository.save(new Department("General Medicine", "Primary healthcare, diagnosis, and preventative medical care"));
            departmentRepository.save(new Department("Ophthalmology", "Complete eye care, vision exams, and optical surgeries"));
            departmentRepository.save(new Department("ENT (Otolaryngology)", "Ear, nose, throat, and head & neck medical care"));
        }

        // Seed Users
        if (userRepository.count() == 0) {
            String adminHash = PasswordUtils.hashPassword("admin123");
            String patientHash = PasswordUtils.hashPassword("patient123");

            User admin = new User("Hospital Administrator", "admin@hospital.com", adminHash, Role.ADMIN, "+1-555-0100", "Other", 35);
            User patient1 = new User("John Doe", "patient@hospital.com", patientHash, Role.PATIENT, "+1-555-0199", "Male", 29);
            User patient2 = new User("Sarah Connor", "sarah.smith@example.com", patientHash, Role.PATIENT, "+1-555-0188", "Female", 34);

            userRepository.save(admin);
            userRepository.save(patient1);
            userRepository.save(patient2);
        }

        // Seed Doctors
        if (doctorRepository.count() == 0) {
            doctorRepository.save(new Doctor("Dr. Robert Harrison", "robert.harrison@hospital.com", "+1-555-0111", "Senior Interventional Cardiologist", 1, "Cardiology", "Monday,Wednesday,Friday", "09:00 - 16:00", "Room 301", new BigDecimal("80.00")));
            doctorRepository.save(new Doctor("Dr. Elena Rostova", "elena.rostova@hospital.com", "+1-555-0112", "Consultant Neurologist", 2, "Neurology", "Tuesday,Thursday,Saturday", "10:00 - 17:00", "Room 402", new BigDecimal("90.00")));
            doctorRepository.save(new Doctor("Dr. Marcus Vance", "marcus.vance@hospital.com", "+1-555-0113", "Orthopedic & Joint Surgeon", 3, "Orthopedics", "Monday,Tuesday,Thursday", "09:00 - 15:00", "Room 205", new BigDecimal("75.00")));
            doctorRepository.save(new Doctor("Dr. Maya Patel", "maya.patel@hospital.com", "+1-555-0114", "Pediatric Specialist", 4, "Pediatrics", "Monday,Tuesday,Wednesday,Thursday,Friday", "08:30 - 16:30", "Room 108", new BigDecimal("65.00")));
            doctorRepository.save(new Doctor("Dr. James Chen", "james.chen@hospital.com", "+1-555-0115", "Clinical Dermatologist", 5, "Dermatology", "Wednesday,Friday,Saturday", "11:00 - 18:00", "Room 214", new BigDecimal("70.00")));
            doctorRepository.save(new Doctor("Dr. Claire Bennett", "claire.bennett@hospital.com", "+1-555-0116", "General Physician & Internist", 6, "General Medicine", "Monday,Tuesday,Wednesday,Thursday,Friday", "08:00 - 17:00", "Room 102", new BigDecimal("50.00")));
            doctorRepository.save(new Doctor("Dr. Alexander Wright", "alex.wright@hospital.com", "+1-555-0117", "Ophthalmic Surgeon", 7, "Ophthalmology", "Tuesday,Wednesday,Friday", "09:30 - 16:00", "Room 312", new BigDecimal("75.00")));
            doctorRepository.save(new Doctor("Dr. Sophia Morales", "sophia.morales@hospital.com", "+1-555-0118", "ENT Head & Neck Specialist", 8, "ENT (Otolaryngology)", "Monday,Thursday,Friday", "10:00 - 16:30", "Room 220", new BigDecimal("70.00")));
        }

        // Seed Appointments
        if (appointmentRepository.count() == 0) {
            User patient = userRepository.findByEmailIgnoreCase("patient@hospital.com").orElse(null);
            Doctor doc1 = doctorRepository.findById(1).orElse(null);
            Doctor doc6 = doctorRepository.findById(6).orElse(null);

            if (patient != null && doc1 != null) {
                Appointment a1 = new Appointment(patient.getId(), doc1.getId(), "Cardiology", LocalDate.now().plusDays(2), "10:00 AM", "Routine cardiovascular health checkup and ECG review");
                a1.setStatus(AppointmentStatus.CONFIRMED);
                appointmentRepository.save(a1);
            }
            if (patient != null && doc6 != null) {
                Appointment a2 = new Appointment(patient.getId(), doc6.getId(), "General Medicine", LocalDate.now().plusDays(4), "09:00 AM", "Seasonal flu symptoms and general consultation");
                a2.setStatus(AppointmentStatus.PENDING);
                appointmentRepository.save(a2);
            }
        }
    }
}
