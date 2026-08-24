package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User entity representing Patients and Administrators.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected Role role = Role.PATIENT;

    @Column(length = 20)
    private String phone;

    @Column(length = 10)
    private String gender;

    private Integer age;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {
    }

    public User(String name, String email, String password, Role role, String phone, String gender, Integer age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.PATIENT;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }

    public User(int id, String name, String email, String password, Role role, String phone, String gender, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.PATIENT;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isPatient() {
        return Role.PATIENT.equals(this.role);
    }
}
