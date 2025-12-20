package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.CreditLineEntity;
import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for CreditLine operations.
 * Handles database CRUD operations for credit_lines table.
 * Manages both accounts and credit_lines tables (parent-child relationship).
 * 
 * @author juanf
 */
public class CreditLineDAO implements DAOinterface<CreditLineEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(CreditLineEntity creditLineEntity) throws SQLException {
        try {
            // First, insert into accounts table
            String accountSql = "INSERT INTO accounts (account_type, account_name, balance) VALUES (?, ?, ?)";
            try (PreparedStatement accountStmt = connection.prepareStatement(accountSql, Statement.RETURN_GENERATED_KEYS)) {
                accountStmt.setString(1, "CREDIT_LINE");
                accountStmt.setString(2, "Credit Line");
                accountStmt.setDouble(3, creditLineEntity.getBalance());
                accountStmt.executeUpdate();
                
                // Get the generated account_id
                int accountId;
                try (ResultSet rs = accountStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        accountId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert into accounts table");
                    }
                }
                
                // Now insert into credit_lines table
                String creditSql = "INSERT INTO credit_lines (account_id, credit_limit, interest_rate, min_payment_percentage) VALUES (?, ?, ?, ?)";
                try (PreparedStatement creditStmt = connection.prepareStatement(creditSql)) {
                    creditStmt.setInt(1, accountId);
                    creditStmt.setDouble(2, creditLineEntity.getCreditLimit());
                    creditStmt.setDouble(3, creditLineEntity.getInterestRate());
                    creditStmt.setDouble(4, creditLineEntity.getMinPaymentPercentage());
                    creditStmt.executeUpdate();
                }
                
                return accountId;
            }
        } catch (SQLException e) {
            System.err.println("Error creating CreditLine: " + e.getMessage());
            return null;
        }
    }

    // Read by ID
    @Override
    public Optional<CreditLineEntity> getByID(Integer id) throws SQLException {
        String sql = "SELECT a.account_id, a.balance, cl.credit_limit, cl.interest_rate, cl.min_payment_percentage " +
                     "FROM accounts a " +
                     "JOIN credit_lines cl ON a.account_id = cl.account_id " +
                     "WHERE a.account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CreditLineEntity creditLine = new CreditLineEntity(
                        rs.getInt("account_id"),
                        0,
                        rs.getDouble("balance"),
                        rs.getDouble("credit_limit"),
                        rs.getDouble("interest_rate"),
                        rs.getDouble("min_payment_percentage")
                    );
                    return Optional.of(creditLine);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    // Read all
    @Override
    public List<CreditLineEntity> getAll() throws SQLException {
        List<CreditLineEntity> creditLines = new ArrayList<>();
        String sql = "SELECT a.account_id, a.balance, cl.credit_limit, cl.interest_rate, cl.min_payment_percentage " +
                     "FROM accounts a " +
                     "JOIN credit_lines cl ON a.account_id = cl.account_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CreditLineEntity creditLine = new CreditLineEntity(
                    rs.getInt("account_id"),
                    0,
                    rs.getDouble("balance"),
                    rs.getDouble("credit_limit"),
                    rs.getDouble("interest_rate"),
                    rs.getDouble("min_payment_percentage")
                );
                creditLines.add(creditLine);
            }
        }
        return creditLines;
    }

    // Update by ID
    @Override
    public CreditLineEntity updateByID(CreditLineEntity creditLineEntity) throws SQLException {
        String accountSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement accountStmt = connection.prepareStatement(accountSql)) {
            accountStmt.setDouble(1, creditLineEntity.getBalance());
            accountStmt.setInt(2, creditLineEntity.getAccountID());
            accountStmt.executeUpdate();
        }
        
        String creditSql = "UPDATE credit_lines SET credit_limit = ?, interest_rate = ?, min_payment_percentage = ? WHERE account_id = ?";
        try (PreparedStatement creditStmt = connection.prepareStatement(creditSql)) {
            creditStmt.setDouble(1, creditLineEntity.getCreditLimit());
            creditStmt.setDouble(2, creditLineEntity.getInterestRate());
            creditStmt.setDouble(3, creditLineEntity.getMinPaymentPercentage());
            creditStmt.setInt(4, creditLineEntity.getAccountID());
            creditStmt.executeUpdate();
        }
        
        return creditLineEntity;
    }

    // Delete by ID
    @Override
    public boolean deleteByID(Integer id) throws SQLException {
        // First delete from client_accounts junction table
        String junctionSql = "DELETE FROM client_accounts WHERE account_id = ?";
        try (PreparedStatement junctionStmt = connection.prepareStatement(junctionSql)) {
            junctionStmt.setInt(1, id);
            junctionStmt.executeUpdate();
        }
        
        // Then delete from credit_lines (child table)
        String creditSql = "DELETE FROM credit_lines WHERE account_id = ?";
        try (PreparedStatement creditStmt = connection.prepareStatement(creditSql)) {
            creditStmt.setInt(1, id);
            creditStmt.executeUpdate();
        }
        
        // Finally delete from accounts (parent table)
        String accountSql = "DELETE FROM accounts WHERE account_id = ?";
        try (PreparedStatement accountStmt = connection.prepareStatement(accountSql)) {
            accountStmt.setInt(1, id);
            int rowsAffected = accountStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
