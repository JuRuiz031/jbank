package com.jbank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbank.model.CreditLine;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.DAO.CreditLineDAO;
import com.jbank.repository.entities.CreditLineEntity;
import com.jbank.validator.CreditLineValidator;

/**
 * Service layer for CreditLine operations.
 * Handles business logic, validation, and DAO orchestration.
 * Manages credit usage, payments, and credit limit increases.
 * 
 * @author juanf
 */
public class CreditLineService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditLineService.class);

    private final CreditLineDAO creditLineDAO;
    private final ClientAccountDAO clientAccountDAO;

    public CreditLineService() {
        this(new CreditLineDAO(), new ClientAccountDAO());
    }

    public CreditLineService(CreditLineDAO creditLineDAO, ClientAccountDAO clientAccountDAO) {
        this.creditLineDAO = creditLineDAO;
        this.clientAccountDAO = clientAccountDAO;
    }

    /**
     * Create a new credit line and assign it to a client as PRIMARY owner.
     */
    public Integer create(CreditLine model, int clientId) {
        try {
            // Validate the account model
            if (!CreditLineValidator.validate(model)) {
                LOGGER.warn("Invalid CreditLine data: account validation failed");
                return null;
            }
            
            Optional<CreditLineEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert CreditLine model to entity");
                return null;
            }
            
            Integer accountId = creditLineDAO.create(entityOpt.get());
            if (accountId != null) {
                // Assign account to client as PRIMARY owner
                boolean assigned = clientAccountDAO.assignAccountToClient(clientId, accountId, "PRIMARY");
                if (!assigned) {
                    // Rollback: delete the account we just created to prevent orphaned data
                    creditLineDAO.deleteByID(accountId);
                    LOGGER.error("Failed to assign credit line {} to client {}, rolling back account creation", accountId, clientId);
                    return null;
                }
            }
            return accountId;
        } catch (SQLException e) {
            LOGGER.warn("Database error creating CreditLine: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get credit line by ID.
     */
    public Optional<CreditLine> getById(Integer id) {
        try {
            Optional<CreditLineEntity> entityOpt = creditLineDAO.getByID(id);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("CreditLine not found with ID {}", id);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving CreditLine by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get all credit lines.
     */
    public List<CreditLine> getAll() {
        try {
            List<CreditLineEntity> entities = creditLineDAO.getAll();
            return entities.stream()
                    .map(this::convertEntityToModel)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving all CreditLines: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Update credit line.
     */
    public CreditLine update(Integer id, CreditLine model) {
        try {
            // Validate the account model
            if (!CreditLineValidator.validate(model)) {
                LOGGER.warn("Invalid CreditLine data: account validation failed");
                return null;
            }
            
            Optional<CreditLineEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert CreditLine model to entity");
                return null;
            }
            
            CreditLineEntity updated = creditLineDAO.updateByID(entityOpt.get());
            return convertEntityToModel(updated).orElse(null);
        } catch (SQLException e) {
            LOGGER.warn("Database error updating CreditLine with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Delete credit line.
     */
    public boolean delete(Integer id) {
        try {
            // Remove all client-account relationships first
            clientAccountDAO.removeAllClientsFromAccount(id);
            return creditLineDAO.deleteByID(id);
        } catch (SQLException e) {
            LOGGER.warn("Database error deleting CreditLine with ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Charge the credit line (increase balance owed).
     * Updates balance and verifies it doesn't exceed credit limit.
     */
    public boolean chargeCredit(CreditLine account, double chargeAmount) {
        try {
            if (chargeAmount <= 0) {
                LOGGER.warn("Invalid charge amount: {}", chargeAmount);
                return false;
            }
            
            double newBalance = account.getBalance() + chargeAmount;
            
            // Verify charge doesn't exceed credit limit
            if (newBalance > account.getCreditLimit()) {
                LOGGER.warn("Charge would exceed credit limit of {} for account {}", 
                           account.getCreditLimit(), account.getAccountID());
                return false;
            }
            
            // Update balance directly through protected setter (need another approach)
            // For now, we'll use a workaround - create a new entity and update via DAO
            CreditLineEntity entity = new CreditLineEntity(
                account.getAccountID(),
                account.getCustomerID(),
                newBalance,
                account.getCreditLimit(),
                account.getInterestRate(),
                account.getMinPaymentPercentage()
            );
            creditLineDAO.updateByID(entity);
            return true;
        } catch (SQLException e) {
            LOGGER.warn("Database error during charge: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Make a payment on the credit line.
     * Reduces balance and applies interest on remaining balance.
     */
    public boolean makePayment(CreditLine account, double paymentAmount) {
        try {
            account.makePayment(paymentAmount);
            
            // Apply interest on remaining balance
            double interest = account.getBalance() * (account.getInterestRate() / 100.0);
            double newBalance = account.getBalance() + interest;
            
            // Update in database
            CreditLineEntity entity = new CreditLineEntity(
                account.getAccountID(),
                account.getCustomerID(),
                newBalance,
                account.getCreditLimit(),
                account.getInterestRate(),
                account.getMinPaymentPercentage()
            );
            creditLineDAO.updateByID(entity);
            
            LOGGER.info("Payment of {} and interest of {} applied to account {}", 
                       paymentAmount, interest, account.getAccountID());
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid payment: {}", e.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.warn("Database error during payment: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Calculate minimum payment required (percentage of balance).
     */
    public double calculateMinimumPayment(CreditLine account) {
        return account.getBalance() * (account.getMinPaymentPercentage() / 100.0);
    }

    /**
     * Increase credit limit based on payment history.
     * Assumes at least 12 on-time payments before increasing.
     * Currently increases limit by 10%.
     */
    public boolean increaseCreditLimit(CreditLine account) {
        try {
            double newLimit = account.getCreditLimit() * 1.10; // 10% increase
            account.setCreditLimit(newLimit);
            
            // Update in database
            CreditLineEntity entity = new CreditLineEntity(
                account.getAccountID(),
                account.getCustomerID(),
                account.getBalance(),
                newLimit,
                account.getInterestRate(),
                account.getMinPaymentPercentage()
            );
            creditLineDAO.updateByID(entity);
            
            LOGGER.info("Increased credit limit to {} for account {}", newLimit, account.getAccountID());
            return true;
        } catch (SQLException e) {
            LOGGER.warn("Database error increasing credit limit: {}", e.getMessage());
            return false;
        }
    }

    // Conversion methods
    public Optional<CreditLine> convertEntityToModel(CreditLineEntity entity) {
        try {
            CreditLine account = new CreditLine(
                entity.getCustomerID(),
                entity.getAccountID(),
                entity.getBalance(),
                "Credit Line",
                entity.getCreditLimit(),
                entity.getInterestRate(),
                entity.getMinPaymentPercentage() * 100 // Convert from decimal to percentage
            );
            return Optional.of(account);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid data in entity, cannot convert to model: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<CreditLineEntity> convertModelToEntity(CreditLine model) {
        try {
            CreditLineEntity entity = new CreditLineEntity(
                model.getAccountID(),
                model.getCustomerID(),
                model.getBalance(),
                model.getCreditLimit(),
                model.getInterestRate(),
                model.getMinPaymentPercentage() / 100 // Convert from percentage to decimal
            );
            return Optional.of(entity);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid model data, cannot convert to entity: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
