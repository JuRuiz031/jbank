package com.jbank.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jbank.util.ConnectionHandler;

/**
 * Data Access Object for Client-Account relationships.
 * Manages the many-to-many relationship through the client_accounts junction table.
 * Handles account ownership (PRIMARY, JOINT) and supports multiple owners per account.
 * 
 * @author juanf
 */
public class ClientAccountDAO {

    private final Connection connection = ConnectionHandler.getConnection();

    /**
     * Assigns an account to a client with specified ownership type.
     * @param clientId Customer ID
     * @param accountId Account ID
     * @param ownershipType "PRIMARY" or "JOINT"
     * @return true if assignment was successful
     */
    public boolean assignAccountToClient(int clientId, int accountId, String ownershipType) throws SQLException {
        String sql = "INSERT INTO client_accounts (customer_id, account_id, ownership_type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, accountId);
            stmt.setString(3, ownershipType);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning account to client: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all accounts for a specific client.
     * @param clientId Customer ID
     * @return Map of account IDs to their ownership types (PRIMARY/JOINT)
     */
    public Map<Integer, String> getAccountsByClient(int clientId) throws SQLException {
        Map<Integer, String> accountMap = new HashMap<>();
        String sql = "SELECT account_id, ownership_type FROM client_accounts WHERE customer_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int accountId = rs.getInt("account_id");
                    String ownershipType = rs.getString("ownership_type");
                    accountMap.put(accountId, ownershipType);
                }
            }
        }
        return accountMap;
    }

    /**
     * Retrieves all clients who own a specific account.
     * @param accountId Account ID
     * @return Map of client IDs to their ownership types
     */
    public Map<Integer, String> getClientsByAccount(int accountId) throws SQLException {
        Map<Integer, String> clientMap = new HashMap<>();
        String sql = "SELECT customer_id, ownership_type FROM client_accounts WHERE account_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int clientId = rs.getInt("customer_id");
                    String ownershipType = rs.getString("ownership_type");
                    clientMap.put(clientId, ownershipType);
                }
            }
        }
        return clientMap;
    }

    /**
     * Removes a client-account relationship.
     * @param clientId Customer ID
     * @param accountId Account ID
     * @return true if removal was successful
     */
    public boolean removeAccountFromClient(int clientId, int accountId) throws SQLException {
        String sql = "DELETE FROM client_accounts WHERE customer_id = ? AND account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, accountId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing account from client: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an account is a joint account (has multiple owners).
     * @param accountId Account ID
     * @return true if account has multiple owners
     */
    public boolean isJointAccount(int accountId) throws SQLException {
        String sql = "SELECT COUNT(*) as owner_count FROM client_accounts WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("owner_count") > 1;
                }
            }
        }
        return false;
    }

    /**
     * Gets all accounts that are joint accounts (multiple owners).
     * @return List of account IDs that have multiple owners
     */
    public List<Integer> getAllJointAccounts() throws SQLException {
        List<Integer> jointAccounts = new ArrayList<>();
        String sql = "SELECT account_id FROM client_accounts GROUP BY account_id HAVING COUNT(*) > 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                jointAccounts.add(rs.getInt("account_id"));
            }
        }
        return jointAccounts;
    }

    /**
     * Checks if a client has ownership of a specific account.
     * @param clientId Customer ID
     * @param accountId Account ID
     * @return true if client owns the account
     */
    public boolean clientOwnsAccount(int clientId, int accountId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM client_accounts WHERE customer_id = ? AND account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Removes all client-account relationships for an account (cascade delete).
     * Used when deleting an account entirely.
     * @param accountId Account ID
     * @return true if removal was successful
     */
    public boolean removeAllClientsFromAccount(int accountId) throws SQLException {
        String sql = "DELETE FROM client_accounts WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing all clients from account: " + e.getMessage());
            return false;
        }
    }
}
