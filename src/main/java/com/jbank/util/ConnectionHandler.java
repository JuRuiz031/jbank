package com.jbank.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionHandler {

    private static final Connection connection;

    static {
        Connection tempConnection = null;
        
        try {
            Properties properties = new Properties();

            try(InputStream input = ConnectionHandler.class.getClassLoader()
                    .getResourceAsStream("database.properties")) {

                if(input == null) {
                    throw new IOException("Unable to find database.properties");
                }
                
                properties.load(input);
            }

            // Load JDBC driver
            Class.forName(properties.getProperty("db.driver"));

            // Get connection
            tempConnection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
            );
            
            System.out.println("Database connection established");
            
            // Initialize database schema if not already initialized
            try {
                initializeSchema(tempConnection);
            } catch (Exception e) {
                System.err.println("Schema initialization failed: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (IOException | ClassNotFoundException | SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to establish database connection", e);
        }
        
        connection = tempConnection;
    }

    public static Connection getConnection() {
        if(connection == null) {
            throw new RuntimeException("Connection failed to set up properly.");
        }
        return connection;
    }

    private static void initializeSchema(Connection conn) throws SQLException, IOException {
        try (InputStream input = ConnectionHandler.class.getClassLoader()
                .getResourceAsStream("schema.sql")) {
            
            if (input == null) {
                throw new IOException("Unable to find schema.sql");
            }

            // Read the schema file
            String schema = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            
            // Check if tables already exist
            if (tablesExist(conn)) {
                System.out.println("Database schema already initialized.");
                return;
            }

            System.out.println("Initializing database schema...");

            // Replace CREATE TABLE with CREATE TABLE IF NOT EXISTS
            String schemaWithIfNotExists = schema.replaceAll(
                "CREATE TABLE ", 
                "CREATE TABLE IF NOT EXISTS "
            );

            // Execute each SQL statement separately
            try (Statement stmt = conn.createStatement()) {
                String[] statements = schemaWithIfNotExists.split(";");
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty()) {
                        try {
                            stmt.execute(trimmed);
                        } catch (SQLException e) {
                            // Log but continue with other statements
                            System.err.println("Warning executing statement: " + e.getMessage());
                        }
                    }
                }
                System.out.println("Database schema initialization completed.");
            }
        }
    }

    private static boolean tablesExist(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Check using a query that works across schema boundaries
            try {
                stmt.executeQuery(
                    "SELECT 1 FROM information_schema.tables " +
                    "WHERE table_schema = 'public' AND table_name = 'clients' LIMIT 1"
                );
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }
}
