package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.BusinessClientEntity;
import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for BusinessClient operations.
 * Handles database CRUD operations for business_clients table.
 * 
 * @author juanf
 */
public class BusinessClientDAO implements DAOinterface<BusinessClientEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(BusinessClientEntity businessClientEntity) throws SQLException {
        // TODO: Insert into clients table first (client_type = 'BUSINESS')
        // TODO: Then insert into business_clients table
        // TODO: Return generated customer_id
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read by ID
    @Override
    public Optional<BusinessClientEntity> getByID(Integer id) throws SQLException {
        // TODO: JOIN clients and business_clients tables on customer_id
        // TODO: Return Optional.empty() if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Read all
    @Override
    public List<BusinessClientEntity> getAll() throws SQLException {
        // TODO: JOIN clients and business_clients tables
        // TODO: Return all business clients as a list
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Update
    @Override
    public BusinessClientEntity updateByID(BusinessClientEntity businessClientEntity) throws SQLException {
        // TODO: Update both clients and business_clients tables
        // TODO: Return the updated entity or null if not found
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Delete
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        // TODO: Delete from business_clients first (child table)
        // TODO: Then delete from clients (parent table)
        // TODO: Return true if successful, false otherwise
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Additional helper methods
    public Optional<BusinessClientEntity> getByEIN(String ein) throws SQLException {
        // TODO: Find business client by EIN
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Optional<BusinessClientEntity> getByBusinessName(String businessName) throws SQLException {
        // TODO: Find business client by business name
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
