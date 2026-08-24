package com.hospital.model;

/**
 * Enumeration of system user roles.
 */
public enum Role {
    PATIENT("Patient"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) return PATIENT;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(roleStr) || r.displayName.equalsIgnoreCase(roleStr)) {
                return r;
            }
        }
        return PATIENT;
    }
}
