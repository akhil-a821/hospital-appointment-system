package com.hospital.model;

/**
 * Lifecycle states of an appointment.
 */
public enum AppointmentStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AppointmentStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        for (AppointmentStatus s : AppointmentStatus.values()) {
            if (s.name().equalsIgnoreCase(statusStr) || s.displayName.equalsIgnoreCase(statusStr)) {
                return s;
            }
        }
        return PENDING;
    }
}
