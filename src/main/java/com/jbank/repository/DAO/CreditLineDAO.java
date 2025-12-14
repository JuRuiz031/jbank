package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.CreditLineEntity;
import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for CreditLine operations.
 * Handles database CRUD operations for credit_lines table.
 * 
 * @author juanf
 */
public class CreditLineDAO implements DAOinterface<CreditLineEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(CreditLineEntity creditLineEntity) throws SQLException {
        // TODO: Insert into accounts table first (account_type = 'CREDIT_LINE')
        // TODO: Then insert into credit_lines table
        // TODO: Return generated account_id
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read by ID
    @Override
    public Optional<CreditLineEntity> getByID(Integer id) throws SQLException {
        // TODO: JOIN accounts and credit_lines tables on account_id
        // TODO: Return Optional.empty() if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read all
    @Override
    public List<CreditLineEntity> getAll() throws SQLException {
        // TODO: JOIN accounts and credit_lines tables
        // TODO: Return all credit lines as a list
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Update
    @Override
    public CreditLineEntity updateByID(CreditLineEntity creditLineEntity) throws SQLException {
        // TODO: Update both accounts and credit_lines tables
        // TODO: Return the updated entity or null if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Delete
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        // TODO: Delete from credit_lines first (child table)
        // TODO: Then delete from accounts (parent table)
        // TODO: Return true if successful, false otherwise
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Additional helper methods
    public List<CreditLineEntity> getByCustomerID(Integer customerID) throws SQLException {
        // TODO: Find all credit lines for a specific customer
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
