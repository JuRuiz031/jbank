package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.SavingsAccountEntity;
import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for SavingsAccount operations.
 * Handles database CRUD operations for savings_accounts table.
 * 
 * @author juanf
 */
public class SavingsAccountDAO implements DAOinterface<SavingsAccountEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(SavingsAccountEntity savingsAccountEntity) throws SQLException {
        // TODO: Insert into accounts table first (account_type = 'SAVINGS')
        // TODO: Then insert into savings_accounts table
        // TODO: Return generated account_id
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read by ID
    @Override
    public Optional<SavingsAccountEntity> getByID(Integer id) throws SQLException {
        // TODO: JOIN accounts and savings_accounts tables on account_id
        // TODO: Return Optional.empty() if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read all
    @Override
    public List<SavingsAccountEntity> getAll() throws SQLException {
        // TODO: JOIN accounts and savings_accounts tables
        // TODO: Return all savings accounts as a list
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Update
    @Override
    public SavingsAccountEntity updateByID(SavingsAccountEntity savingsAccountEntity) throws SQLException {
        // TODO: Update both accounts and savings_accounts tables
        // TODO: Return the updated entity or null if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Delete
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        // TODO: Delete from savings_accounts first (child table)
        // TODO: Then delete from accounts (parent table)
        // TODO: Return true if successful, false otherwise
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Additional helper methods
    public List<SavingsAccountEntity> getByCustomerID(Integer customerID) throws SQLException {
        // TODO: Find all savings accounts for a specific customer
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
