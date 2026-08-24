package com.hospital.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for hashing and verifying user passwords using SHA-256.
 */
public class PasswordUtils {

    /**
     * Hashes a raw password string using SHA-256.
     */
    public static String hashPassword(String rawPassword) {
        if (rawPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a raw password matches a stored hash or plain password.
     */
    public static boolean verifyPassword(String rawPassword, String storedPasswordOrHash) {
        if (rawPassword == null || storedPasswordOrHash == null) return false;
        String rawHashed = hashPassword(rawPassword);
        return rawHashed.equalsIgnoreCase(storedPasswordOrHash) || rawPassword.equals(storedPasswordOrHash);
    }
}
