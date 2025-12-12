package com.jbank.util;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ConnectionHandler
 */
public class ConnectionHandlerTest {

    @Test
    public void testConnectionIsNotNull() {
        Connection connection = ConnectionHandler.getConnection();
        assertNotNull(connection, "Connection should not be null");
    }

    @Test
    public void testConnectionIsValid() throws Exception {
        Connection connection = ConnectionHandler.getConnection();
        assertFalse(connection.isClosed(), "Connection should be open and not closed");
    }

    @Test
    public void testCanExecuteQuery() throws Exception {
        Connection connection = ConnectionHandler.getConnection();
        try ( // Simple test query to verify connection works
        java.sql.Statement statement = connection.createStatement(); java.sql.ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            assertNotNull(resultSet, "ResultSet should not be null");
        }
    }
}
