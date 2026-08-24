package com.hospital.repository;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDescIdDesc(Integer patientId);

    @Query("SELECT a.appointmentTime FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date AND a.status != 'CANCELLED'")
    List<String> findBookedTimesForDoctorAndDate(@Param("doctorId") Integer doctorId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date AND a.appointmentTime = :time AND a.status != 'CANCELLED'")
    boolean isSlotBooked(@Param("doctorId") Integer doctorId, @Param("date") LocalDate date, @Param("time") String time);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctorId = :doctorId AND a.status != 'CANCELLED' AND a.appointmentDate >= CURRENT_DATE")
    boolean hasActiveUpcomingAppointmentsForDoctor(@Param("doctorId") Integer doctorId);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:status IS NULL OR :status = '' OR :status = 'All' OR a.status = :statusEnum) AND " +
           "(:doctorId IS NULL OR a.doctorId = :doctorId) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC, a.id DESC")
    List<Appointment> findAllWithFilters(@Param("status") String status, @Param("statusEnum") AppointmentStatus statusEnum, @Param("doctorId") Integer doctorId);

    long countByStatus(AppointmentStatus status);

    long countByPatientId(Integer patientId);

    long countByPatientIdAndStatus(Integer patientId, AppointmentStatus status);
}
