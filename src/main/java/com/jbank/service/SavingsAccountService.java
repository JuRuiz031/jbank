package com.jbank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbank.model.SavingsAccount;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.DAO.SavingsAccountDAO;
import com.jbank.repository.entities.SavingsAccountEntity;
import com.jbank.validator.SavingsAccountValidator;

/**
 * Service layer for SavingsAccount operations.
 * Handles business logic, validation, and DAO orchestration.
 * Manages withdrawal counters and interest calculations.
 * 
 * @author juanf
 */
public class SavingsAccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SavingsAccountService.class);

    private final SavingsAccountDAO savingsAccountDAO;
    private final ClientAccountDAO clientAccountDAO;

    public SavingsAccountService() {
        this(new SavingsAccountDAO(), new ClientAccountDAO());
    }

    public SavingsAccountService(SavingsAccountDAO savingsAccountDAO, ClientAccountDAO clientAccountDAO) {
        this.savingsAccountDAO = savingsAccountDAO;
        this.clientAccountDAO = clientAccountDAO;
    }

    // Create a new savings account and assign it to a client as PRIMARY owner
    public Integer create(SavingsAccount model, int clientId) {
        try {
            // Validate the account model
            if (!SavingsAccountValidator.validate(model)) {
                LOGGER.warn("Invalid SavingsAccount data: account validation failed");
                return null;
            }
            
            Optional<SavingsAccountEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert SavingsAccount model to entity");
                return null;
            }
            
            Integer accountId = savingsAccountDAO.create(entityOpt.get());
            if (accountId != null) {
                // Assign account to client as PRIMARY owner
                boolean assigned = clientAccountDAO.assignAccountToClient(clientId, accountId, "PRIMARY");
                if (!assigned) {
                    // Rollback: delete the account we just created to prevent orphaned data
                    savingsAccountDAO.deleteByID(accountId);
                    LOGGER.error("Failed to assign savings account {} to client {}, rolling back account creation", accountId, clientId);
                    return null;
                }
            }
            return accountId;
        } catch (SQLException e) {
            LOGGER.warn("Database error creating SavingsAccount: {}", e.getMessage());
            return null;
        }
    }

    // Get savings account by ID
    public Optional<SavingsAccount> getById(Integer id) {
        try {
            Optional<SavingsAccountEntity> entityOpt = savingsAccountDAO.getByID(id);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("SavingsAccount not found with ID {}", id);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving SavingsAccount by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // Get all savings accounts
    public List<SavingsAccount> getAll() {
        try {
            List<SavingsAccountEntity> entities = savingsAccountDAO.getAll();
            return entities.stream()
                    .map(this::convertEntityToModel)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving all SavingsAccounts: {}", e.getMessage());
            return List.of();
        }
    }

    // Update savings account
    public SavingsAccount update(Integer id, SavingsAccount model) {
        try {
            // Validate the account model
            if (!SavingsAccountValidator.validate(model)) {
                LOGGER.warn("Invalid SavingsAccount data: account validation failed");
                return null;
            }
            
            Optional<SavingsAccountEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert SavingsAccount model to entity");
                return null;
            }
            
            SavingsAccountEntity updated = savingsAccountDAO.updateByID(entityOpt.get());
            return convertEntityToModel(updated).orElse(null);
        } catch (SQLException e) {
            LOGGER.warn("Database error updating SavingsAccount with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    // Delete savings account
    public boolean delete(Integer id) {
        try {
            // Remove all client-account relationships first
            clientAccountDAO.removeAllClientsFromAccount(id);
            return savingsAccountDAO.deleteByID(id);
        } catch (SQLException e) {
            LOGGER.warn("Database error deleting SavingsAccount with ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    // Deposit funds into savings account, updates balance in both model and database
    public boolean deposit(SavingsAccount account, double depositAmount) {
        try {
            account.deposit(depositAmount);
            
            // Update in database
            SavingsAccountEntity entity = new SavingsAccountEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                account.getInterestRate(),
                account.getWithdrawalLimit(),
                account.getWithdrawalCounter()
            );
            savingsAccountDAO.updateByID(entity);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid deposit: {}", e.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.warn("Database error during deposit: {}", e.getMessage());
            return false;
        }
    }

    // Withdraw funds from savings account, checks withdrawal limit (6 per month) and increments counter
    public boolean withdraw(SavingsAccount account, double withdrawAmount) {
        try {
            account.withdraw(withdrawAmount);
            
            // Update in database
            SavingsAccountEntity entity = new SavingsAccountEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                account.getInterestRate(),
                account.getWithdrawalLimit(),
                account.getWithdrawalCounter()
            );
            savingsAccountDAO.updateByID(entity);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid withdrawal: {}", e.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.warn("Database error during withdrawal: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Apply interest to savings account.
     * Calculates and adds interest based on current balance and interest rate.
     */
    public boolean applyInterest(SavingsAccount account) {
        try {
            account.applyInterest();
            
            // Update in database
            SavingsAccountEntity entity = new SavingsAccountEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                account.getInterestRate(),
                account.getWithdrawalLimit(),
                account.getWithdrawalCounter()
            );
            savingsAccountDAO.updateByID(entity);
            
            LOGGER.info("Applied interest to account {}", account.getAccountID());
            return true;
        } catch (SQLException e) {
            LOGGER.warn("Database error applying interest: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Reset withdrawal counter (typically done monthly).
     */
    public boolean resetWithdrawalCounter(SavingsAccount account) {
        try {
            account.resetWithdrawalCounter();
            
            // Update in database
            SavingsAccountEntity entity = new SavingsAccountEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                account.getInterestRate(),
                account.getWithdrawalLimit(),
                0
            );
            savingsAccountDAO.updateByID(entity);
            
            LOGGER.info("Reset withdrawal counter for account {}", account.getAccountID());
            return true;
        } catch (SQLException e) {
            LOGGER.warn("Database error resetting withdrawal counter: {}", e.getMessage());
            return false;
        }
    }

    // Conversion methods
    public Optional<SavingsAccount> convertEntityToModel(SavingsAccountEntity entity) {
        try {
            SavingsAccount account = new SavingsAccount(
                entity.getCustomerID(),
                entity.getAccountID(),
                entity.getBalance(),
                entity.getAccountName(),
                entity.getInterestRate(),
                entity.getWithdrawalLimit()
            );
            return Optional.of(account);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid data in entity, cannot convert to model: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<SavingsAccountEntity> convertModelToEntity(SavingsAccount model) {
        try {
            SavingsAccountEntity entity = new SavingsAccountEntity(
                model.getAccountID(),
                model.getCustomerID(),
                model.getBalance(),
                model.getInterestRate(),
                model.getWithdrawalLimit(),
                model.getWithdrawalCounter(),
                model.getAccountName()
            );
            return Optional.of(entity);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid model data, cannot convert to entity: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
