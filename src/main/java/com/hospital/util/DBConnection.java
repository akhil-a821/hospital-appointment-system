package com.hospital.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton Database Connection Manager for MySQL with Automatic Embedded Fallback.
 * If MySQL is offline or not installed, seamlessly falls back to embedded H2 (MySQL Mode).
 */
public class DBConnection {

    private static final String DEFAULT_PROPERTIES_FILE = "db.properties";
    private static Properties properties = new Properties();
    private static boolean initialized = false;

    private static String host = "localhost";
    private static int port = 3306;
    private static String dbName = "hospital_db";
    private static String user = "root";
    private static String password = "";
    private static boolean useSSL = false;
    private static boolean allowPublicKeyRetrieval = true;
    private static String serverTimezone = "UTC";

    private static boolean usingFallback = false;

    static {
        loadProperties();
    }

    public static synchronized void loadProperties() {
        try {
            File externalFile = new File(DEFAULT_PROPERTIES_FILE);
            if (externalFile.exists()) {
                try (InputStream fis = new FileInputStream(externalFile)) {
                    properties.load(fis);
                }
            } else {
                try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE)) {
                    if (is != null) {
                        properties.load(is);
                    }
                }
            }

            host = properties.getProperty("db.host", host);
            port = Integer.parseInt(properties.getProperty("db.port", String.valueOf(port)));
            dbName = properties.getProperty("db.name", dbName);
            user = properties.getProperty("db.user", user);
            password = properties.getProperty("db.password", password);
            useSSL = Boolean.parseBoolean(properties.getProperty("db.useSSL", String.valueOf(useSSL)));
            allowPublicKeyRetrieval = Boolean.parseBoolean(properties.getProperty("db.allowPublicKeyRetrieval", String.valueOf(allowPublicKeyRetrieval)));
            serverTimezone = properties.getProperty("db.serverTimezone", serverTimezone);

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {}

            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ignored) {}

            initialized = true;
        } catch (Exception e) {
            System.err.println("[DBConnection] Properties load warning: " + e.getMessage());
        }
    }

    public static synchronized void saveProperties(String newHost, int newPort, String newDbName, String newUser, String newPassword) {
        host = newHost;
        port = newPort;
        dbName = newDbName;
        user = newUser;
        password = newPassword;

        properties.setProperty("db.host", host);
        properties.setProperty("db.port", String.valueOf(port));
        properties.setProperty("db.name", dbName);
        properties.setProperty("db.user", user);
        properties.setProperty("db.password", password);
        properties.setProperty("db.useSSL", String.valueOf(useSSL));
        properties.setProperty("db.allowPublicKeyRetrieval", String.valueOf(allowPublicKeyRetrieval));
        properties.setProperty("db.serverTimezone", serverTimezone);

        try (FileOutputStream fos = new FileOutputStream(DEFAULT_PROPERTIES_FILE)) {
            properties.store(fos, "Hospital Appointment System Database Configuration");
        } catch (Exception e) {
            System.err.println("[DBConnection] Could not save db.properties: " + e.getMessage());
        }
        usingFallback = false;
    }

    public static String getBaseUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/?useSSL=" + useSSL
                + "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval
                + "&serverTimezone=" + serverTimezone + "&connectTimeout=2000";
    }

    public static String getDatabaseUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=" + useSSL
                + "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval
                + "&serverTimezone=" + serverTimezone + "&connectTimeout=2000";
    }

    public static String getFallbackUrl() {
        // H2 embedded database running in MySQL compatibility mode
        return "jdbc:h2:./hospital_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;AUTO_SERVER=TRUE";
    }

    /**
     * Obtains an active connection to the MySQL database, or falls back to embedded mode if MySQL is not reachable.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (!initialized) {
            loadProperties();
        }

        if (!usingFallback) {
            try {
                return DriverManager.getConnection(getDatabaseUrl(), user, password);
            } catch (SQLException e) {
                // Fallback to embedded DB
                usingFallback = true;
                System.out.println("[DBConnection] MySQL server is offline. Seamlessly switching to local embedded database...");
            }
        }

        return DriverManager.getConnection(getFallbackUrl(), "sa", "");
    }

    public static Connection getServerConnection() throws SQLException {
        if (!initialized) {
            loadProperties();
        }
        return DriverManager.getConnection(getBaseUrl(), user, password);
    }

    public static boolean testConnection(String testHost, int testPort, String testDb, String testUser, String testPass) {
        String url = "jdbc:mysql://" + testHost + ":" + testPort + "/" + (testDb.isBlank() ? "" : testDb)
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=2000";
        try (Connection conn = DriverManager.getConnection(url, testUser, testPass)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void ensureDatabaseExists() throws SQLException {
        if (!usingFallback) {
            try (Connection serverConn = getServerConnection();
                 Statement stmt = serverConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            } catch (SQLException e) {
                // Ignore if server unreachable, fallback handles it
            }
        }
    }

    public static boolean isUsingFallback() {
        return usingFallback;
    }

    public static void setUsingFallback(boolean fallback) {
        usingFallback = fallback;
    }

    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }
}
