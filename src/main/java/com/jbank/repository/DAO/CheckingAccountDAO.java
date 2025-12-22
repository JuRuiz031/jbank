package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jbank.repository.entities.CheckingAccountEntity;
import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for CheckingAccount operations.
 * Handles database CRUD operations for checking_accounts table.
 * Manages both accounts and checking_accounts tables (parent-child relationship).
 * 
 * @author juanf
 */
public class CheckingAccountDAO implements DAOinterface<CheckingAccountEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(CheckingAccountEntity checkingAccountEntity) throws SQLException {
        try {
            // First, insert into accounts table
            String accountSql = "INSERT INTO accounts (account_type, account_name, balance) VALUES (?, ?, ?)";
            try (PreparedStatement accountStmt = connection.prepareStatement(accountSql, Statement.RETURN_GENERATED_KEYS)) {
                accountStmt.setString(1, "CHECKING");
                accountStmt.setString(2, checkingAccountEntity.getAccountName());
                accountStmt.setDouble(3, checkingAccountEntity.getBalance()); // Use the initial deposit from entity
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
                
                // Now insert into checking_accounts table
                String checkingSql = "INSERT INTO checking_accounts (account_id, overdraft_fee, overdraft_limit) VALUES (?, ?, ?)";
                try (PreparedStatement checkingStmt = connection.prepareStatement(checkingSql)) {
                    checkingStmt.setInt(1, accountId);
                    checkingStmt.setDouble(2, checkingAccountEntity.getOverdraftFee());
                    checkingStmt.setDouble(3, 0.0); // Overdraft limit not in entity yet
                    checkingStmt.executeUpdate();
                }
                
                return accountId;
            }
        } catch (SQLException e) {
            System.err.println("Error creating CheckingAccount: " + e.getMessage());
            return null;
        }
    }

    // Read by ID
    @Override
    public Optional<CheckingAccountEntity> getByID(Integer id) throws SQLException {
        String sql = "SELECT a.account_id, a.account_name, a.balance, ca.overdraft_fee " +
                     "FROM accounts a " +
                     "JOIN checking_accounts ca ON a.account_id = ca.account_id " +
                     "WHERE a.account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CheckingAccountEntity account = new CheckingAccountEntity(
                        rs.getInt("account_id"),
                        0, // customerID not stored here, retrieved from client_accounts
                        rs.getDouble("balance"),
                        rs.getDouble("overdraft_fee"),
                        rs.getString("account_name")
                    );
                    return Optional.of(account);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    // Read all
    @Override
    public List<CheckingAccountEntity> getAll() throws SQLException {
        List<CheckingAccountEntity> accounts = new ArrayList<>();
        String sql = "SELECT a.account_id, a.account_name, a.balance, ca.overdraft_fee " +
                     "FROM accounts a " +
                     "JOIN checking_accounts ca ON a.account_id = ca.account_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CheckingAccountEntity account = new CheckingAccountEntity(
                    rs.getInt("account_id"),
                    0,
                    rs.getDouble("balance"),
                    rs.getDouble("overdraft_fee"),
                    rs.getString("account_name")
                );
                accounts.add(account);
            }
        }
        return accounts;
    }

    // Update by ID
    @Override
    public CheckingAccountEntity updateByID(CheckingAccountEntity checkingAccountEntity) throws SQLException {
        String accountSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement accountStmt = connection.prepareStatement(accountSql)) {
            accountStmt.setDouble(1, checkingAccountEntity.getBalance());
            accountStmt.setInt(2, checkingAccountEntity.getAccountID());
            accountStmt.executeUpdate();
        }
        
        String checkingSql = "UPDATE checking_accounts SET overdraft_fee = ? WHERE account_id = ?";
        try (PreparedStatement checkingStmt = connection.prepareStatement(checkingSql)) {
            checkingStmt.setDouble(1, checkingAccountEntity.getOverdraftFee());
            checkingStmt.setInt(2, checkingAccountEntity.getAccountID());
            checkingStmt.executeUpdate();
        }
        
        return checkingAccountEntity;
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
        
        // Then delete from checking_accounts (child table)
        String checkingSql = "DELETE FROM checking_accounts WHERE account_id = ?";
        try (PreparedStatement checkingStmt = connection.prepareStatement(checkingSql)) {
            checkingStmt.setInt(1, id);
            checkingStmt.executeUpdate();
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
