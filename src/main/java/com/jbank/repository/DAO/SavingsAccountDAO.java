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
 * Manages both accounts and savings_accounts tables (parent-child relationship).
 * 
 * @author juanf
 */
public class SavingsAccountDAO implements DAOinterface<SavingsAccountEntity> {

    private final Connection connection = ConnectionHandler.getConnection();

    // Create
    @Override
    public Integer create(SavingsAccountEntity savingsAccountEntity) throws SQLException {
        try {
            // First, insert into accounts table
            String accountSql = "INSERT INTO accounts (account_type, account_name, balance) VALUES (?, ?, ?)";
            try (PreparedStatement accountStmt = connection.prepareStatement(accountSql, Statement.RETURN_GENERATED_KEYS)) {
                accountStmt.setString(1, "SAVINGS");
                accountStmt.setString(2, savingsAccountEntity.getAccountName());
                accountStmt.setDouble(3, savingsAccountEntity.getBalance());
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
                
                // Now insert into savings_accounts table
                String savingsSql = "INSERT INTO savings_accounts (account_id, interest_rate, withdrawal_limit, withdrawal_counter) VALUES (?, ?, ?, ?)";
                try (PreparedStatement savingsStmt = connection.prepareStatement(savingsSql)) {
                    savingsStmt.setInt(1, accountId);
                    savingsStmt.setDouble(2, savingsAccountEntity.getInterestRate());
                    savingsStmt.setInt(3, savingsAccountEntity.getWithdrawalLimit());
                    savingsStmt.setInt(4, 0); // Start with 0 withdrawals
                    savingsStmt.executeUpdate();
                }
                
                return accountId;
            }
        } catch (SQLException e) {
            System.err.println("Error creating SavingsAccount: " + e.getMessage());
            return null;
        }
    }

    // Read by ID
    @Override
    public Optional<SavingsAccountEntity> getByID(Integer id) throws SQLException {
        String sql = "SELECT a.account_id, a.account_name, a.balance, sa.interest_rate, sa.withdrawal_limit, sa.withdrawal_counter " +
                     "FROM accounts a " +
                     "JOIN savings_accounts sa ON a.account_id = sa.account_id " +
                     "WHERE a.account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SavingsAccountEntity account = new SavingsAccountEntity(
                        rs.getInt("account_id"),
                        0,
                        rs.getDouble("balance"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("withdrawal_limit"),
                        rs.getInt("withdrawal_counter"),
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
    public List<SavingsAccountEntity> getAll() throws SQLException {
        List<SavingsAccountEntity> accounts = new ArrayList<>();
        String sql = "SELECT a.account_id, a.account_name, a.balance, sa.interest_rate, sa.withdrawal_limit, sa.withdrawal_counter " +
                     "FROM accounts a " +
                     "JOIN savings_accounts sa ON a.account_id = sa.account_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SavingsAccountEntity account = new SavingsAccountEntity(
                    rs.getInt("account_id"),
                    0,
                    rs.getDouble("balance"),
                    rs.getDouble("interest_rate"),
                    rs.getInt("withdrawal_limit"),
                    rs.getInt("withdrawal_counter"),
                    rs.getString("account_name")
                );
                accounts.add(account);
            }
        }
        return accounts;
    }

    // Update by ID
    @Override
    public SavingsAccountEntity updateByID(SavingsAccountEntity savingsAccountEntity) throws SQLException {
        String accountSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement accountStmt = connection.prepareStatement(accountSql)) {
            accountStmt.setDouble(1, savingsAccountEntity.getBalance());
            accountStmt.setInt(2, savingsAccountEntity.getAccountID());
            accountStmt.executeUpdate();
        }
        
        String savingsSql = "UPDATE savings_accounts SET interest_rate = ?, withdrawal_limit = ?, withdrawal_counter = ? WHERE account_id = ?";
        try (PreparedStatement savingsStmt = connection.prepareStatement(savingsSql)) {
            savingsStmt.setDouble(1, savingsAccountEntity.getInterestRate());
            savingsStmt.setInt(2, savingsAccountEntity.getWithdrawalLimit());
            savingsStmt.setInt(3, savingsAccountEntity.getWithdrawalCounter());
            savingsStmt.setInt(4, savingsAccountEntity.getAccountID());
            savingsStmt.executeUpdate();
        }
        
        return savingsAccountEntity;
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
        
        // Then delete from savings_accounts (child table)
        String savingsSql = "DELETE FROM savings_accounts WHERE account_id = ?";
        try (PreparedStatement savingsStmt = connection.prepareStatement(savingsSql)) {
            savingsStmt.setInt(1, id);
            savingsStmt.executeUpdate();
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
