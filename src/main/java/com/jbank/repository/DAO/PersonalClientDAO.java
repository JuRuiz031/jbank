package com.jbank.repository.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.PersonalClientEntity;
import com.jbank.util.ConnectionHandler;



/**
 *
 * @author juanf
 */
public class PersonalClientDAO implements DAOinterface<PersonalClientEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(PersonalClientEntity personalClientEntity) throws SQLException {
        try {
            // First, insert into clients table
            String clientSql = "INSERT INTO clients (client_type, phone_number, address, name) VALUES (?, ?, ?, ?)";
            try(PreparedStatement clientStmt = connection.prepareStatement(clientSql, Statement.RETURN_GENERATED_KEYS)){
                clientStmt.setString(1, "PERSONAL");
                clientStmt.setString(2, personalClientEntity.getPhoneNumber());
                clientStmt.setString(3, personalClientEntity.getAddress());
                clientStmt.setString(4, personalClientEntity.getName());
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
                
                // Now insert into personal_clients table
                String personalSql = "INSERT INTO personal_clients (customer_id, tax_id, credit_score, yearly_income, total_debt) VALUES (?, ?, ?, ?, ?)";
                try(PreparedStatement personalStmt = connection.prepareStatement(personalSql)){
                    personalStmt.setInt(1, customerId);
                    personalStmt.setString(2, personalClientEntity.getTaxID());
                    personalStmt.setInt(3, personalClientEntity.getCreditScore());
                    personalStmt.setDouble(4, personalClientEntity.getYearlyIncome());
                    personalStmt.setDouble(5, personalClientEntity.getTotalDebt());
                    personalStmt.executeUpdate();
                }
                
                return customerId;
            }
        } catch (SQLException e) {
            System.err.println("Error creating PersonalClient: " + e.getMessage());
            return null;
        }
    }

    // Read by ID
    @Override
    public Optional<PersonalClientEntity> getByID(Integer id) throws SQLException {
        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, pc.tax_id, pc.credit_score, pc.yearly_income, pc.total_debt " +
                     "FROM clients c JOIN personal_clients pc ON c.customer_id = pc.customer_id " +
                     "WHERE c.customer_id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    PersonalClientEntity personalClient = new PersonalClientEntity(
                        rs.getInt("customer_id"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("name"),
                        rs.getString("tax_id"),
                        rs.getInt("credit_score"),
                        rs.getDouble("yearly_income"),
                        rs.getDouble("total_debt"));
                    return Optional.of(personalClient);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    // Read by taxID
    public Optional<PersonalClientEntity> getByTaxID(String taxID) throws SQLException {
        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, pc.tax_id, pc.credit_score, pc.yearly_income, pc.total_debt " +
                     "FROM clients c JOIN personal_clients pc ON c.customer_id = pc.customer_id " +
                     "WHERE pc.tax_id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, taxID);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    PersonalClientEntity personalClient = new PersonalClientEntity(
                        rs.getInt("customer_id"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("name"),
                        rs.getString("tax_id"),
                        rs.getInt("credit_score"),
                        rs.getDouble("yearly_income"),
                        rs.getDouble("total_debt"));
                    return Optional.of(personalClient);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    // Read by Name
    public List<PersonalClientEntity> getByName(String name) throws SQLException {
        List<PersonalClientEntity> personalClients = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, pc.tax_id, pc.credit_score, pc.yearly_income, pc.total_debt " +
                     "FROM clients c JOIN personal_clients pc ON c.customer_id = pc.customer_id " +
                     "WHERE c.name = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, name);
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    PersonalClientEntity personalClient = new PersonalClientEntity(
                        rs.getInt("customer_id"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("name"),
                        rs.getString("tax_id"),
                        rs.getInt("credit_score"),
                        rs.getDouble("yearly_income"),
                        rs.getDouble("total_debt"));
                    personalClients.add(personalClient);
                }
            }
        }
        return personalClients;
    }

    // Read all
    @Override
    public List<PersonalClientEntity> getAll() throws SQLException {
        List<PersonalClientEntity> personalClients = new ArrayList<>();

        String sql = "SELECT c.customer_id, c.phone_number, c.address, c.name, pc.tax_id, pc.credit_score, pc.yearly_income, pc.total_debt " +
                     "FROM clients c JOIN personal_clients pc ON c.customer_id = pc.customer_id";
        try(PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while (rs.next()) {
                PersonalClientEntity personalClient = new PersonalClientEntity(
                    rs.getInt("customer_id"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("name"),
                    rs.getString("tax_id"),
                    rs.getInt("credit_score"),
                    rs.getDouble("yearly_income"),
                    rs.getDouble("total_debt"));
                personalClients.add(personalClient);
            }
        }
        return personalClients;
    }

    // Update by ID
    @Override
    public PersonalClientEntity updateByID(PersonalClientEntity personalClientEntity) throws SQLException {
        String clientSql = "UPDATE clients SET phone_number = ?, address = ?, name = ? WHERE customer_id = ?";
        try(PreparedStatement clientStmt = connection.prepareStatement(clientSql)){
            clientStmt.setString(1, personalClientEntity.getPhoneNumber());
            clientStmt.setString(2, personalClientEntity.getAddress());
            clientStmt.setString(3, personalClientEntity.getName());
            clientStmt.setInt(4, personalClientEntity.getCustomerID());
            clientStmt.executeUpdate();
        }

        String personalSql = "UPDATE personal_clients SET tax_id = ?, credit_score = ?, yearly_income = ?, total_debt = ? WHERE customer_id = ?";
        try(PreparedStatement personalStmt = connection.prepareStatement(personalSql)){
            personalStmt.setString(1, personalClientEntity.getTaxID());
            personalStmt.setInt(2, personalClientEntity.getCreditScore());
            personalStmt.setDouble(3, personalClientEntity.getYearlyIncome());
            personalStmt.setDouble(4, personalClientEntity.getTotalDebt());
            personalStmt.setInt(5, personalClientEntity.getCustomerID());
            personalStmt.executeUpdate();
        }

        return personalClientEntity;
    }

    // Update by taxID
    public PersonalClientEntity updateByTaxID(PersonalClientEntity personalClientEntity) throws SQLException {
        String getIdSql = "SELECT customer_id FROM personal_clients WHERE tax_id = ?";
        int customerId;
        try(PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)){
            getIdStmt.setString(1, personalClientEntity.getTaxID());
            try(ResultSet rs = getIdStmt.executeQuery()){
                if (rs.next()) {
                    customerId = rs.getInt("customer_id");
                } else {
                    throw new SQLException("No personal client found with tax ID: " + personalClientEntity.getTaxID());
                }
            }
        }

        personalClientEntity = new PersonalClientEntity(
            customerId,
            personalClientEntity.getPhoneNumber(),
            personalClientEntity.getAddress(),
            personalClientEntity.getName(),
            personalClientEntity.getTaxID(),
            personalClientEntity.getCreditScore(),
            personalClientEntity.getYearlyIncome(),
            personalClientEntity.getTotalDebt()
        );

        return updateByID(personalClientEntity);
    }

    // Delete by ID
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        String personalSql = "DELETE FROM personal_clients WHERE customer_id = ?";
        try(PreparedStatement personalStmt = connection.prepareStatement(personalSql)){
            personalStmt.setInt(1, id);
            personalStmt.executeUpdate();
        }

        String clientSql = "DELETE FROM clients WHERE customer_id = ?";
        try(PreparedStatement clientStmt = connection.prepareStatement(clientSql)){
            clientStmt.setInt(1, id);
            int rowsAffected = clientStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Delete by taxID
    public boolean deleteByTaxID(String taxID) throws SQLException {
        String getIdSql = "SELECT customer_id FROM personal_clients WHERE tax_id = ?";
        int customerId;
        try(PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)){
            getIdStmt.setString(1, taxID);
            try(ResultSet rs = getIdStmt.executeQuery()){
                if (rs.next()) {
                    customerId = rs.getInt("customer_id");
                } else {
                    return false; // No such taxID
                }
            }
        }

        return deleteByID(customerId);
    }
}