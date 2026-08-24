package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Doctors.
 */
public class DoctorDAO {

    public boolean addDoctor(Doctor doctor) throws SQLException {
        String sql = "INSERT INTO doctors (name, email, phone, specialization, department_id, department, available_days, available_time, room_no, consultation_fee) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getEmail());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getSpecialization());
            if (doctor.getDepartmentId() != null) {
                pstmt.setInt(5, doctor.getDepartmentId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setString(6, doctor.getDepartment());
            pstmt.setString(7, doctor.getAvailableDays());
            pstmt.setString(8, doctor.getAvailableTime());
            pstmt.setString(9, doctor.getRoomNo());
            pstmt.setBigDecimal(10, doctor.getConsultationFee() != null ? doctor.getConsultationFee() : new BigDecimal("50.00"));

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        doctor.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor) throws SQLException {
        String sql = "UPDATE doctors SET name = ?, email = ?, phone = ?, specialization = ?, department_id = ?, department = ?, "
                   + "available_days = ?, available_time = ?, room_no = ?, consultation_fee = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getEmail());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getSpecialization());
            if (doctor.getDepartmentId() != null) {
                pstmt.setInt(5, doctor.getDepartmentId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setString(6, doctor.getDepartment());
            pstmt.setString(7, doctor.getAvailableDays());
            pstmt.setString(8, doctor.getAvailableTime());
            pstmt.setString(9, doctor.getRoomNo());
            pstmt.setBigDecimal(10, doctor.getConsultationFee() != null ? doctor.getConsultationFee() : new BigDecimal("50.00"));
            pstmt.setInt(11, doctor.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteDoctor(int doctorId) throws SQLException {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public Doctor findById(int id) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDoctor(rs);
                }
            }
        }
        return null;
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToDoctor(rs));
            }
        }
        return list;
    }

    public List<Doctor> searchDoctors(String keyword, String department) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM doctors WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(name) LIKE ? OR LOWER(specialization) LIKE ?) ");
            String kwPattern = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kwPattern);
            params.add(kwPattern);
        }

        if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("All Departments") && !department.equalsIgnoreCase("All")) {
            sql.append("AND LOWER(department) = LOWER(?) ");
            params.add(department.trim());
        }

        sql.append("ORDER BY name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToDoctor(rs));
                }
            }
        }
        return list;
    }

    public int countDoctors() throws SQLException {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Doctor mapRowToDoctor(ResultSet rs) throws SQLException {
        Doctor doc = new Doctor(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("specialization"),
                rs.getObject("department_id") != null ? rs.getInt("department_id") : null,
                rs.getString("department"),
                rs.getString("available_days"),
                rs.getString("available_time"),
                rs.getString("room_no"),
                rs.getBigDecimal("consultation_fee")
        );
        doc.setCreatedAt(rs.getTimestamp("created_at"));
        return doc;
    }
}
