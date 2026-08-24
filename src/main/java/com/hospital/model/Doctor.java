package com.hospital.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor entity representing medical staff.
 */
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "available_days", nullable = false, length = 100)
    private String availableDays = "Monday,Tuesday,Wednesday,Thursday,Friday";

    @Column(name = "available_time", nullable = false, length = 100)
    private String availableTime = "09:00 - 17:00";

    @Column(name = "room_no", length = 50)
    private String roomNo = "Room 101";

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    private BigDecimal consultationFee = new BigDecimal("50.00");

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Doctor() {
    }

    public Doctor(String name, String email, String phone, String specialization, Integer departmentId,
                  String department, String availableDays, String availableTime, String roomNo, BigDecimal consultationFee) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.departmentId = departmentId;
        this.department = department;
        this.availableDays = (availableDays != null && !availableDays.isBlank()) ? availableDays : "Monday,Tuesday,Wednesday,Thursday,Friday";
        this.availableTime = (availableTime != null && !availableTime.isBlank()) ? availableTime : "09:00 - 17:00";
        this.roomNo = (roomNo != null && !roomNo.isBlank()) ? roomNo : "Room 101";
        this.consultationFee = consultationFee != null ? consultationFee : new BigDecimal("50.00");
        this.createdAt = LocalDateTime.now();
    }

    public Doctor(int id, String name, String email, String phone, String specialization, Integer departmentId,
                  String department, String availableDays, String availableTime, String roomNo, BigDecimal consultationFee) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.departmentId = departmentId;
        this.department = department;
        this.availableDays = (availableDays != null && !availableDays.isBlank()) ? availableDays : "Monday,Tuesday,Wednesday,Thursday,Friday";
        this.availableTime = (availableTime != null && !availableTime.isBlank()) ? availableTime : "09:00 - 17:00";
        this.roomNo = (roomNo != null && !roomNo.isBlank()) ? roomNo : "Room 101";
        this.consultationFee = consultationFee != null ? consultationFee : new BigDecimal("50.00");
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp ts) {
        if (ts != null) {
            this.createdAt = ts.toLocalDateTime();
        }
    }

    public List<String> getAvailableDaysList() {
        List<String> list = new ArrayList<>();
        if (availableDays != null && !availableDays.isBlank()) {
            for (String day : availableDays.split(",")) {
                list.add(day.trim());
            }
        }
        return list;
    }

    public boolean isAvailableOnDay(String dayName) {
        if (dayName == null || availableDays == null) return false;
        for (String d : getAvailableDaysList()) {
            if (d.equalsIgnoreCase(dayName)) {
                return true;
            }
        }
        return false;
    }
}
