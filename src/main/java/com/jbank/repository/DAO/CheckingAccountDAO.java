package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.CheckingAccountEntity;
import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for CheckingAccount operations.
 * Handles database CRUD operations for checking_accounts table.
 * 
 * @author juanf
 */
public class CheckingAccountDAO implements DAOinterface<CheckingAccountEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(CheckingAccountEntity checkingAccountEntity) throws SQLException {
        // TODO: Insert into accounts table first (account_type = 'CHECKING')
        // TODO: Then insert into checking_accounts table
        // TODO: Return generated account_id
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read by ID
    @Override
    public Optional<CheckingAccountEntity> getByID(Integer id) throws SQLException {
        // TODO: JOIN accounts and checking_accounts tables on account_id
        // TODO: Return Optional.empty() if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read all
    @Override
    public List<CheckingAccountEntity> getAll() throws SQLException {
        // TODO: JOIN accounts and checking_accounts tables
        // TODO: Return all checking accounts as a list
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Update
    @Override
    public CheckingAccountEntity updateByID(CheckingAccountEntity checkingAccountEntity) throws SQLException {
        // TODO: Update both accounts and checking_accounts tables
        // TODO: Return the updated entity or null if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Delete
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        // TODO: Delete from checking_accounts first (child table)
        // TODO: Then delete from accounts (parent table)
        // TODO: Return true if successful, false otherwise
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Additional helper methods
    public List<CheckingAccountEntity> getByCustomerID(Integer customerID) throws SQLException {
        // TODO: Find all checking accounts for a specific customer
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
