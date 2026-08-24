package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointments.
 * Handles booking, status updates, cancellations, queries, and double-booking prevention.
 */
public class AppointmentDAO {

    /**
     * Checks if a doctor already has an active (Pending or Confirmed) appointment for the given date & time slot.
     * Cancelled appointments do NOT block the slot.
     */
    public boolean isSlotBooked(int doctorId, LocalDate date, String time) throws SQLException {
        String sql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setString(3, time.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Retrieves all booked timeslots for a doctor on a specific date.
     */
    public List<String> getBookedSlotsForDoctor(int doctorId, LocalDate date) throws SQLException {
        List<String> booked = new ArrayList<>();
        String sql = "SELECT appointment_time FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND status != 'Cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    booked.add(rs.getString("appointment_time"));
                }
            }
        }
        return booked;
    }

    public boolean bookAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, department, appointment_date, appointment_time, reason, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setString(3, appointment.getDepartment());
            pstmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setString(5, appointment.getAppointmentTime());
            pstmt.setString(6, appointment.getReason());
            pstmt.setString(7, appointment.getStatus() != null ? appointment.getStatus().name() : AppointmentStatus.PENDING.name());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, u.name AS patient_name, u.phone AS patient_phone, u.email AS patient_email, "
                   + "d.name AS doctor_name, d.specialization AS doctor_specialization "
                   + "FROM appointments a "
                   + "JOIN users u ON a.patient_id = u.id "
                   + "JOIN doctors d ON a.doctor_id = d.id "
                   + "WHERE a.patient_id = ? "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time DESC, a.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    public List<Appointment> getAllAppointments(String statusFilter, Integer doctorId, String searchKeyword) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, u.name AS patient_name, u.phone AS patient_phone, u.email AS patient_email, "
                + "d.name AS doctor_name, d.specialization AS doctor_specialization "
                + "FROM appointments a "
                + "JOIN users u ON a.patient_id = u.id "
                + "JOIN doctors d ON a.doctor_id = d.id "
                + "WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("All")) {
            sql.append("AND a.status = ? ");
            params.add(statusFilter.trim());
        }

        if (doctorId != null && doctorId > 0) {
            sql.append("AND a.doctor_id = ? ");
            params.add(doctorId);
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            sql.append("AND (LOWER(u.name) LIKE ? OR LOWER(d.name) LIKE ? OR LOWER(a.department) LIKE ? OR LOWER(a.reason) LIKE ?) ");
            String kw = "%" + searchKeyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append("ORDER BY a.appointment_date DESC, a.appointment_time DESC, a.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    public Appointment findById(int id) throws SQLException {
        String sql = "SELECT a.*, u.name AS patient_name, u.phone AS patient_phone, u.email AS patient_email, "
                   + "d.name AS doctor_name, d.specialization AS doctor_specialization "
                   + "FROM appointments a "
                   + "JOIN users u ON a.patient_id = u.id "
                   + "JOIN doctors d ON a.doctor_id = d.id "
                   + "WHERE a.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }
        }
        return null;
    }

    public boolean updateStatus(int appointmentId, AppointmentStatus newStatus) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, appointmentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean cancelAppointment(int appointmentId) throws SQLException {
        return updateStatus(appointmentId, AppointmentStatus.CANCELLED);
    }

    public int countAppointments() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countAppointmentsByStatus(AppointmentStatus status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countPatientAppointments(int patientId, AppointmentStatus status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM appointments WHERE patient_id = ?");
        if (status != null) {
            sql.append(" AND status = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            pstmt.setInt(1, patientId);
            if (status != null) {
                pstmt.setString(2, status.name());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean hasActiveAppointmentsForDoctor(int doctorId) throws SQLException {
        String sql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND status != 'Cancelled' AND appointment_date >= CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        Appointment appt = new Appointment(
                rs.getInt("id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getString("department"),
                rs.getDate("appointment_date").toLocalDate(),
                rs.getString("appointment_time"),
                rs.getString("reason"),
                AppointmentStatus.fromString(rs.getString("status"))
        );

        appt.setPatientName(rs.getString("patient_name"));
        appt.setPatientPhone(rs.getString("patient_phone"));
        appt.setPatientEmail(rs.getString("patient_email"));
        appt.setDoctorName(rs.getString("doctor_name"));
        appt.setDoctorSpecialization(rs.getString("doctor_specialization"));
        appt.setCreatedAt(rs.getTimestamp("created_at"));
        appt.setUpdatedAt(rs.getTimestamp("updated_at"));

        return appt;
    }
}
