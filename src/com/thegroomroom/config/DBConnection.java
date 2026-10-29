package com.thegroomroom.config;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String CONFIG_FILE = "db_config.properties";
    private static Properties properties = new Properties();

    private static String host = "localhost";
    private static int port = 3306;
    private static String dbName = "the_groom_room";
    private static String user = "root";
    private static String password = "";
    private static boolean useSSL = false;
    private static boolean allowPublicKeyRetrieval = true;

    private static boolean driverLoaded = false;

    static {
        loadConfig();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] Warning: MySQL Driver not loaded yet or lib missing. " + e.getMessage());
        }
    }

    public static synchronized void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                properties.load(in);
                host = properties.getProperty("db.host", "localhost");
                try {
                    port = Integer.parseInt(properties.getProperty("db.port", "3306"));
                } catch (NumberFormatException ignored) {}
                dbName = properties.getProperty("db.name", "the_groom_room");
                user = properties.getProperty("db.user", "root");
                password = properties.getProperty("db.password", "");
                useSSL = Boolean.parseBoolean(properties.getProperty("db.use_ssl", "false"));
                allowPublicKeyRetrieval = Boolean.parseBoolean(properties.getProperty("db.allow_public_key_retrieval", "true"));
            } catch (IOException e) {
                System.err.println("[DBConnection] Error loading config: " + e.getMessage());
            }
        }
    }

    public static synchronized void saveConfig(String newHost, int newPort, String newDbName, String newUser, String newPassword) {
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
        properties.setProperty("db.use_ssl", String.valueOf(useSSL));
        properties.setProperty("db.allow_public_key_retrieval", String.valueOf(allowPublicKeyRetrieval));

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            properties.store(out, "The Groom Room - Database Configuration");
        } catch (IOException e) {
            System.err.println("[DBConnection] Failed to save config: " + e.getMessage());
        }
    }

    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useSSL=" + useSSL +
                "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval +
                "&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    public static String getRootServerUrl() {
        return "jdbc:mysql://" + host + ":" + port +
                "?useSSL=" + useSSL +
                "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval +
                "&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found in classpath.", e);
            }
        }
        DriverManager.setLoginTimeout(3); // 3 seconds timeout for responsiveness
        return DriverManager.getConnection(getUrl(), user, password);
    }

    public static Connection getServerConnection(String customHost, int customPort, String customUser, String customPassword) throws SQLException {
        String testUrl = "jdbc:mysql://" + customHost + ":" + customPort +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        DriverManager.setLoginTimeout(3);
        return DriverManager.getConnection(testUrl, customUser, customPassword);
    }

    public static boolean isAvailable() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    // Getters
    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }
}
