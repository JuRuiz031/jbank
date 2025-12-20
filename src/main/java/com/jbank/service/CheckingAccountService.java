package com.jbank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbank.model.CheckingAccount;
import com.jbank.repository.DAO.CheckingAccountDAO;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.entities.CheckingAccountEntity;
import com.jbank.validator.CheckingAccountValidator;

/**
 * Service layer for CheckingAccount operations.
 * Handles business logic, validation, and DAO orchestration.
 * 
 * @author juanf
 */
public class CheckingAccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckingAccountService.class);

    private final CheckingAccountDAO checkingAccountDAO;
    private final ClientAccountDAO clientAccountDAO;

    public CheckingAccountService() {
        this(new CheckingAccountDAO(), new ClientAccountDAO());
    }

    public CheckingAccountService(CheckingAccountDAO checkingAccountDAO, ClientAccountDAO clientAccountDAO) {
        this.checkingAccountDAO = checkingAccountDAO;
        this.clientAccountDAO = clientAccountDAO;
    }

    /**
     * Create a new checking account and assign it to a client as PRIMARY owner.
     */
    public Integer create(CheckingAccount model, int clientId) {
        try {
            // Validate the account model
            if (!CheckingAccountValidator.validate(model)) {
                LOGGER.warn("Invalid CheckingAccount data: account validation failed");
                return null;
            }
            
            Optional<CheckingAccountEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert CheckingAccount model to entity");
                return null;
            }
            
            Integer accountId = checkingAccountDAO.create(entityOpt.get());
            if (accountId != null) {
                // Assign account to client as PRIMARY owner
                boolean assigned = clientAccountDAO.assignAccountToClient(clientId, accountId, "PRIMARY");
                if (!assigned) {
                    // Rollback: delete the account we just created to prevent orphaned data
                    checkingAccountDAO.deleteByID(accountId);
                    LOGGER.error("Failed to assign checking account {} to client {}, rolling back account creation", accountId, clientId);
                    return null;
                }
            }
            return accountId;
        } catch (SQLException e) {
            LOGGER.warn("Database error creating CheckingAccount: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get checking account by ID.
     */
    public Optional<CheckingAccount> getById(Integer id) {
        try {
            Optional<CheckingAccountEntity> entityOpt = checkingAccountDAO.getByID(id);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("CheckingAccount not found with ID {}", id);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving CheckingAccount by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get all checking accounts.
     */
    public List<CheckingAccount> getAll() {
        try {
            List<CheckingAccountEntity> entities = checkingAccountDAO.getAll();
            return entities.stream()
                    .map(this::convertEntityToModel)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving all CheckingAccounts: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Update checking account.
     */
    public CheckingAccount update(Integer id, CheckingAccount model) {
        try {
            // Validate the account model
            if (!CheckingAccountValidator.validate(model)) {
                LOGGER.warn("Invalid CheckingAccount data: account validation failed");
                return null;
            }
            
            Optional<CheckingAccountEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert CheckingAccount model to entity");
                return null;
            }
            
            CheckingAccountEntity updated = checkingAccountDAO.updateByID(entityOpt.get());
            return convertEntityToModel(updated).orElse(null);
        } catch (SQLException e) {
            LOGGER.warn("Database error updating CheckingAccount with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Delete checking account.
     */
    public boolean delete(Integer id) {
        try {
            // Remove all client-account relationships first
            clientAccountDAO.removeAllClientsFromAccount(id);
            return checkingAccountDAO.deleteByID(id);
        } catch (SQLException e) {
            LOGGER.warn("Database error deleting CheckingAccount with ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Deposit funds into checking account.
     * Updates balance in both model and database.
     */
    public boolean deposit(CheckingAccount account, double depositAmount) {
        try {
            account.deposit(depositAmount);
            
            // Update in database
            CheckingAccountEntity entity = new CheckingAccountEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                account.getOverdraftFee()
            );
            checkingAccountDAO.updateByID(entity);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid deposit: {}", e.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.warn("Database error during deposit: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Withdraw funds from checking account.
     * Applies overdraft fee if balance goes negative.
     * Updates balance in both model and database.
     */
    public boolean withdraw(CheckingAccount account, double withdrawAmount) {
        try {
            account.withdraw(withdrawAmount);
            
            // Update in database
            CheckingAccountEntity entity = new CheckingAccountEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                account.getOverdraftFee()
            );
            checkingAccountDAO.updateByID(entity);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid withdrawal: {}", e.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.warn("Database error during withdrawal: {}", e.getMessage());
            return false;
        }
    }

    // Conversion methods
    public Optional<CheckingAccount> convertEntityToModel(CheckingAccountEntity entity) {
        try {
            CheckingAccount account = new CheckingAccount(
                entity.getCustomerID(),
                entity.getAccountID(),
                entity.getBalance(),
                "Checking Account",
                entity.getOverdraftFee(),
                0.0 // overdraft limit not in entity yet
            );
            return Optional.of(account);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid data in entity, cannot convert to model: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<CheckingAccountEntity> convertModelToEntity(CheckingAccount model) {
        try {
            CheckingAccountEntity entity = new CheckingAccountEntity(
                model.getAccountID(),
                model.getCustomerID(),
                model.getBalance(),
                model.getOverdraftFee()
            );
            return Optional.of(entity);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid model data, cannot convert to entity: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
