package com.jbank.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
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
            
        } catch (IOException | ClassNotFoundException | SQLException e) {
            logger.error("Failed to establish database connection", e);
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
}
