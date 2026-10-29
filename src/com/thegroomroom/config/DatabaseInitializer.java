package com.thegroomroom.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static class InitResult {
        public final boolean success;
        public final String message;

        public InitResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static InitResult initializeDatabase(String host, int port, String user, String password) {
        // Step 1: Connect to MySQL server (without specifying DB name yet) to create database
        String serverUrl = "jdbc:mysql://" + host + ":" + port +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return new InitResult(false, "MySQL JDBC Driver not found in classpath.");
        }

        try (Connection conn = java.sql.DriverManager.getConnection(serverUrl, user, password);
             Statement stmt = conn.createStatement()) {

            // Create DB if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DBConnection.getDbName() +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");

        } catch (Exception e) {
            return new InitResult(false, "Failed to connect to MySQL server or create database: " + e.getMessage());
        }

        // Step 2: Now connect to the_groom_room and execute schema.sql
        File schemaFile = new File("database/schema.sql");
        if (!schemaFile.exists()) {
            schemaFile = new File("../database/schema.sql");
        }

        if (!schemaFile.exists()) {
            return new InitResult(false, "Could not find schema.sql in database/ directory.");
        }

        String dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + DBConnection.getDbName() +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&allowMultiQueries=true";

        try (Connection conn = java.sql.DriverManager.getConnection(dbUrl, user, password);
             BufferedReader reader = new BufferedReader(new FileReader(schemaFile));
             Statement stmt = conn.createStatement()) {

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.isEmpty()) {
                    continue;
                }
                sql.append(line).append(" ");
                if (line.endsWith(";")) {
                    String query = sql.toString().trim();
                    if (!query.isEmpty() && !query.toUpperCase().startsWith("CREATE DATABASE") && !query.toUpperCase().startsWith("USE ")) {
                        try {
                            stmt.execute(query);
                        } catch (Exception ex) {
                            System.err.println("[DatabaseInitializer] Notice executing: " + query + " -> " + ex.getMessage());
                        }
                    }
                    sql.setLength(0);
                }
            }

            return new InitResult(true, "Database '" + DBConnection.getDbName() + "' created and initialized successfully with tables and Indian salon demo records!");

        } catch (Exception e) {
            return new InitResult(false, "Error executing SQL schema: " + e.getMessage());
        }
    }
}
