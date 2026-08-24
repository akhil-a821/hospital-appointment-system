package com.hospital.model;

/**
 * Admin specialized model.
 */
public class Admin extends User {

    public Admin() {
        super();
        this.role = Role.ADMIN;
    }

    public Admin(int id, String name, String email, String password, String phone) {
        super(id, name, email, password, Role.ADMIN, phone, "N/A", 0);
    }

    public Admin(String name, String email, String password, String phone) {
        super(name, email, password, Role.ADMIN, phone, "N/A", 0);
    }

    public static Admin fromUser(User u) {
        if (u == null) return null;
        if (u instanceof Admin a) return a;
        Admin a = new Admin(
                u.getId() != null ? u.getId() : 0,
                u.getName(),
                u.getEmail(),
                u.getPassword(),
                u.getPhone()
        );
        a.setRole(u.getRole());
        a.setCreatedAt(u.getCreatedAt());
        return a;
    }
}
