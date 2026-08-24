package com.hospital.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Date and Time utilities for formatting, slot generation, and validation.
 */
public class DateUtils {

    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FRIENDLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy");
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(FRIENDLY_DATE_FORMATTER);
    }

    public static String formatIsoDate(LocalDate date) {
        if (date == null) return "";
        return date.format(ISO_DATE_FORMATTER);
    }

    public static LocalDate parseIsoDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), ISO_DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDayOfWeekName(LocalDate date) {
        if (date == null) return "";
        DayOfWeek dow = date.getDayOfWeek();
        return dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public static boolean isPastDate(LocalDate date) {
        if (date == null) return true;
        return date.isBefore(LocalDate.now());
    }

    /**
     * Default list of consultation time slots.
     */
    public static List<String> getDefaultTimeSlots() {
        return Arrays.asList(
                "09:00 AM",
                "09:30 AM",
                "10:00 AM",
                "10:30 AM",
                "11:00 AM",
                "11:30 AM",
                "02:00 PM",
                "02:30 PM",
                "03:00 PM",
                "03:30 PM",
                "04:00 PM",
                "04:30 PM",
                "05:00 PM"
        );
    }

    /**
     * Generates a tailored list of available appointment time slots based on doctor's working hours.
     */
    public static List<String> generateTimeSlotsForDoctor(String workingTimeRange) {
        if (workingTimeRange == null || workingTimeRange.isBlank()) {
            return getDefaultTimeSlots();
        }

        // e.g. "09:00 - 17:00" or "08:30 - 16:30"
        try {
            String[] parts = workingTimeRange.split("-");
            if (parts.length == 2) {
                int startHour = parseHour(parts[0].trim());
                int endHour = parseHour(parts[1].trim());

                if (startHour < endHour && startHour >= 0 && endHour <= 24) {
                    List<String> slots = new ArrayList<>();
                    for (int h = startHour; h < endHour; h++) {
                        // Skip typical lunch break 13:00 - 14:00 (1 PM - 2 PM)
                        if (h == 13) continue;

                        slots.add(formatHourTo12H(h, 0));
                        slots.add(formatHourTo12H(h, 30));
                    }
                    if (!slots.isEmpty()) {
                        return slots;
                    }
                }
            }
        } catch (Exception e) {
            // fallback
        }

        return getDefaultTimeSlots();
    }

    private static int parseHour(String timeStr) {
        // format "09:00" or "9:00" or "09"
        String[] p = timeStr.split(":");
        return Integer.parseInt(p[0].replaceAll("[^0-9]", ""));
    }

    private static String formatHourTo12H(int hour24, int minute) {
        String ampm = hour24 >= 12 ? "PM" : "AM";
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format("%02d:%02d %s", hour12, minute, ampm);
    }
}
