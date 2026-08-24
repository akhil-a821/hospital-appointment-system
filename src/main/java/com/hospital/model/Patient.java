package com.hospital.model;

/**
 * Patient specialized model.
 */
public class Patient extends User {

    public Patient() {
        super();
        this.role = Role.PATIENT;
    }

    public Patient(int id, String name, String email, String password, String phone, String gender, int age) {
        super(id, name, email, password, Role.PATIENT, phone, gender, age);
    }

    public Patient(String name, String email, String password, String phone, String gender, int age) {
        super(name, email, password, Role.PATIENT, phone, gender, age);
    }

    public static Patient fromUser(User u) {
        if (u == null) return null;
        if (u instanceof Patient p) return p;
        Patient p = new Patient(
                u.getId() != null ? u.getId() : 0,
                u.getName(),
                u.getEmail(),
                u.getPassword(),
                u.getPhone(),
                u.getGender(),
                u.getAge() != null ? u.getAge() : 0
        );
        p.setRole(u.getRole());
        p.setCreatedAt(u.getCreatedAt());
        return p;
    }
}
