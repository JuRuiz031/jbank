package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        try {
            // First, insert into clients table
            String clientSql = "INSERT INTO clients (client_type, phone_number, address, name) VALUES (?, ?, ?, ?)";
            try(PreparedStatement clientStmt = connection.prepareStatement(clientSql, Statement.RETURN_GENERATED_KEYS)){
                clientStmt.setString(1, "BUSINESS");
                clientStmt.setString(2, businessClientEntity.getPhoneNumber());
                clientStmt.setString(3, businessClientEntity.getAddress());
                clientStmt.setString(4, businessClientEntity.getName());
                clientStmt.executeUpdate();
                
                // Get the generated customer_id
                int customerId;
                try(ResultSet rs = clientStmt.getGeneratedKeys()){
                    if(rs.next()) {
                        customerId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert into clients table");
                    }
                }
                
                // Now insert into business_clients table
                String businessSql = "INSERT INTO business_clients (customer_id, ein, business_type, contact_person_name, contact_person_title, total_asset_value, annual_revenue, annual_profit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try(PreparedStatement businessStmt = connection.prepareStatement(businessSql)){
                    businessStmt.setInt(1, customerId);
                    businessStmt.setString(2, businessClientEntity.getEIN());
                    businessStmt.setString(3, businessClientEntity.getBusinessType());
                    businessStmt.setString(4, businessClientEntity.getContactPersonName());
                    businessStmt.setString(5, businessClientEntity.getContactPersonTitle());
                    businessStmt.setDouble(6, businessClientEntity.getTotalAssetValue());
                    businessStmt.setDouble(7, businessClientEntity.getAnnualRevenue());
                    businessStmt.setDouble(8, businessClientEntity.getAnnualProfit());
                    businessStmt.executeUpdate();
                }
                
                return customerId;
            }
        } catch (SQLException e) {
            System.err.println("Error creating BusinessClient: " + e.getMessage());
            return null;
        }
    }

    // Read by ID
    @Override
    public Optional<BusinessClientEntity> getByID(Integer id) throws SQLException {
        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, bc.ein, bc.business_type, bc.contact_person_name, bc.contact_person_title, bc.total_asset_value, bc.annual_revenue, bc.annual_profit " +
                     "FROM clients c JOIN business_clients bc ON c.customer_id = bc.customer_id " +
                     "WHERE c.customer_id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    BusinessClientEntity businessClient = new BusinessClientEntity(
                        rs.getInt("customer_id"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("name"),
                        rs.getString("ein"),
                        rs.getString("business_type"),
                        rs.getString("contact_person_name"),
                        rs.getString("contact_person_title"),
                        rs.getDouble("total_asset_value"),
                        rs.getDouble("annual_revenue"),
                        rs.getDouble("annual_profit"));
                    return Optional.of(businessClient);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    // Read by EIN
    public Optional<BusinessClientEntity> getByEIN(String ein) throws SQLException {
        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, bc.ein, bc.business_type, bc.contact_person_name, bc.contact_person_title, bc.total_asset_value, bc.annual_revenue, bc.annual_profit " +
                     "FROM clients c JOIN business_clients bc ON c.customer_id = bc.customer_id " +
                     "WHERE bc.ein = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, ein);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    BusinessClientEntity businessClient = new BusinessClientEntity(
                        rs.getInt("customer_id"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("name"),
                        rs.getString("ein"),
                        rs.getString("business_type"),
                        rs.getString("contact_person_name"),
                        rs.getString("contact_person_title"),
                        rs.getDouble("total_asset_value"),
                        rs.getDouble("annual_revenue"),
                        rs.getDouble("annual_profit"));
                    return Optional.of(businessClient);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    // Read by Name
    public List<BusinessClientEntity> getByName(String name) throws SQLException {
        List<BusinessClientEntity> businessClients = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, bc.ein, bc.business_type, bc.contact_person_name, bc.contact_person_title, bc.total_asset_value, bc.annual_revenue, bc.annual_profit " +
                     "FROM clients c JOIN business_clients bc ON c.customer_id = bc.customer_id " +
                     "WHERE c.name = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, name);
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    BusinessClientEntity businessClient = new BusinessClientEntity(
                        rs.getInt("customer_id"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("name"),
                        rs.getString("ein"),
                        rs.getString("business_type"),
                        rs.getString("contact_person_name"),
                        rs.getString("contact_person_title"),
                        rs.getDouble("total_asset_value"),
                        rs.getDouble("annual_revenue"),
                        rs.getDouble("annual_profit"));
                    businessClients.add(businessClient);
                }
            }
        }
        return businessClients;
    }

    // Read all
    @Override
    public List<BusinessClientEntity> getAll() throws SQLException {
        List<BusinessClientEntity> businessClients = new ArrayList<>();

        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, bc.ein, bc.business_type, bc.contact_person_name, bc.contact_person_title, bc.total_asset_value, bc.annual_revenue, bc.annual_profit " +
                     "FROM clients c JOIN business_clients bc ON c.customer_id = bc.customer_id";
        try(PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while (rs.next()) {
                BusinessClientEntity businessClient = new BusinessClientEntity(
                    rs.getInt("customer_id"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("name"),
                    rs.getString("ein"),
                    rs.getString("business_type"),
                    rs.getString("contact_person_name"),
                    rs.getString("contact_person_title"),
                    rs.getDouble("total_asset_value"),
                    rs.getDouble("annual_revenue"),
                    rs.getDouble("annual_profit"));
                businessClients.add(businessClient);
            }
        }
        return businessClients;
    }

    // Update by ID
    @Override
    public BusinessClientEntity updateByID(BusinessClientEntity businessClientEntity) throws SQLException {
        String clientSql = "UPDATE clients SET phone_number = ?, address = ?, name = ? WHERE customer_id = ?";
        try(PreparedStatement clientStmt = connection.prepareStatement(clientSql)){
            clientStmt.setString(1, businessClientEntity.getPhoneNumber());
            clientStmt.setString(2, businessClientEntity.getAddress());
            clientStmt.setString(3, businessClientEntity.getName());
            clientStmt.setInt(4, businessClientEntity.getCustomerID());
            clientStmt.executeUpdate();
        }

        String businessSql = "UPDATE business_clients SET ein = ?, business_type = ?, contact_person_name = ?, contact_person_title = ?, total_asset_value = ?, annual_revenue = ?, annual_profit = ? WHERE customer_id = ?";
        try(PreparedStatement businessStmt = connection.prepareStatement(businessSql)){
            businessStmt.setString(1, businessClientEntity.getEIN());
            businessStmt.setString(2, businessClientEntity.getBusinessType());
            businessStmt.setString(3, businessClientEntity.getContactPersonName());
            businessStmt.setString(4, businessClientEntity.getContactPersonTitle());
            businessStmt.setDouble(5, businessClientEntity.getTotalAssetValue());
            businessStmt.setDouble(6, businessClientEntity.getAnnualRevenue());
            businessStmt.setDouble(7, businessClientEntity.getAnnualProfit());
            businessStmt.setInt(8, businessClientEntity.getCustomerID());
            businessStmt.executeUpdate();
        }

        return businessClientEntity;
    }

    // Delete by ID
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        String businessSql = "DELETE FROM business_clients WHERE customer_id = ?";
        try(PreparedStatement businessStmt = connection.prepareStatement(businessSql)){
            businessStmt.setInt(1, id);
            businessStmt.executeUpdate();
        }

        String clientSql = "DELETE FROM clients WHERE customer_id = ?";
        try(PreparedStatement clientStmt = connection.prepareStatement(clientSql)){
            clientStmt.setInt(1, id);
            int rowsAffected = clientStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Delete by EIN
    public boolean deleteByEIN(String ein) throws SQLException {
        String getIdSql = "SELECT customer_id FROM business_clients WHERE ein = ?";
        int customerId;
        try(PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)){
            getIdStmt.setString(1, ein);
            try(ResultSet rs = getIdStmt.executeQuery()){
                if (rs.next()) {
                    customerId = rs.getInt("customer_id");
                } else {
                    return false; // No such EIN
                }
            }
        }

        return deleteByID(customerId);
    }
}
