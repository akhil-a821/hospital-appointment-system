package com.hospital.service;

import com.hospital.model.Doctor;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.util.DateUtils;
import com.hospital.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllByOrderByNameAsc();
    }

    public Optional<Doctor> getDoctorById(Integer id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> searchDoctors(String keyword, String department) {
        return doctorRepository.searchDoctors(keyword, department);
    }

    public String saveDoctor(Doctor doctor) {
        String err = ValidationUtils.validateDoctorForm(
                doctor.getName(), doctor.getEmail(), doctor.getPhone(),
                doctor.getSpecialization(), doctor.getDepartment(),
                doctor.getAvailableDays(), doctor.getAvailableTime()
        );
        if (err != null) return err;

        doctorRepository.save(doctor);
        return null;
    }

    public String deleteDoctor(Integer doctorId) {
        if (appointmentRepository.hasActiveUpcomingAppointmentsForDoctor(doctorId)) {
            return "Cannot delete doctor: Active upcoming appointments are currently booked with this doctor. Please reassign or cancel them first.";
        }
        doctorRepository.deleteById(doctorId);
        return null;
    }

    public static class TimeSlotInfo {
        private final String timeSlot;
        private final boolean isBooked;

        public TimeSlotInfo(String timeSlot, boolean isBooked) {
            this.timeSlot = timeSlot;
            this.isBooked = isBooked;
        }

        public String getTimeSlot() { return timeSlot; }
        public boolean isBooked() { return isBooked; }
    }

    public List<String> getDoctorSlotsForDate(Doctor doctor, LocalDate date) {
        List<TimeSlotInfo> slotInfos = getAvailableSlotsForDoctorAndDate(doctor, date);
        List<String> list = new ArrayList<>();
        for (TimeSlotInfo s : slotInfos) {
            list.add(s.getTimeSlot());
        }
        return list;
    }

    public List<TimeSlotInfo> getAvailableSlotsForDoctorAndDate(Doctor doctor, LocalDate date) {
        if (doctor == null || date == null) return Collections.emptyList();

        String dayName = DateUtils.getDayOfWeekName(date);
        if (!doctor.isAvailableOnDay(dayName)) {
            return Collections.emptyList();
        }

        List<String> bookedTimes = appointmentRepository.findBookedTimesForDoctorAndDate(doctor.getId(), date);
        List<String> candidateSlots = DateUtils.generateTimeSlotsForDoctor(doctor.getAvailableTime());

        List<TimeSlotInfo> slotInfos = new ArrayList<>();
        for (String slot : candidateSlots) {
            boolean isBooked = bookedTimes.stream().anyMatch(b -> b.equalsIgnoreCase(slot.trim()));
            slotInfos.add(new TimeSlotInfo(slot, isBooked));
        }

        return slotInfos;
    }
}
