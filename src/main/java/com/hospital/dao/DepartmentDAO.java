package com.hospital.dao;

import com.hospital.model.Department;
import com.hospital.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Departments.
 */
public class DepartmentDAO {

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Department dept = new Department(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                dept.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(dept);
            }
        }
        return list;
    }

    public Department findById(int id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Department dept = new Department(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                    dept.setCreatedAt(rs.getTimestamp("created_at"));
                    return dept;
                }
            }
        }
        return null;
    }

    public Department findByName(String name) throws SQLException {
        String sql = "SELECT * FROM departments WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Department dept = new Department(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                    dept.setCreatedAt(rs.getTimestamp("created_at"));
                    return dept;
                }
            }
        }
        return null;
    }

    public boolean addDepartment(Department department) throws SQLException {
        String sql = "INSERT INTO departments (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getDescription());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        department.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
}
