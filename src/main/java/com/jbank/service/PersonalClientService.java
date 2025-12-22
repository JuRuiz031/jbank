package com.jbank.service;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbank.model.PersonalClient;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.DAO.PersonalClientDAO;
import com.jbank.repository.entities.PersonalClientEntity;
import com.jbank.util.AccountDeletionException;
/**
 *
 * @author juanf
 */

public class PersonalClientService implements ServiceInterface<PersonalClientEntity, PersonalClient> {
    // Logger for the class
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalClientService.class);

    // DAO instance (constructor injected for testability)
    private final PersonalClientDAO personalClientDAO;
    private final ClientAccountDAO clientAccountDAO;

    public PersonalClientService() {
        this(new PersonalClientDAO(), new ClientAccountDAO());
    }

    public PersonalClientService(PersonalClientDAO personalClientDAO, ClientAccountDAO clientAccountDAO) {
        this.personalClientDAO = personalClientDAO;
        this.clientAccountDAO = clientAccountDAO;
    }

    // Create PersonalClient
    @Override
    public Integer create(PersonalClient model) {
        try {
            Optional<PersonalClientEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert PersonalClient model to entity");
                return null;
            }
            Integer newID = personalClientDAO.create(entityOpt.get());
            return newID;
        } catch (SQLException e) {
            LOGGER.warn("Database error creating PersonalClient: {}", e.getMessage());
            return null;
        }
    }

    // Get PersonalClient by ID
    @Override
    public Optional<PersonalClient> getById(Integer id) {
        try {
            Optional<PersonalClientEntity> entityOpt = personalClientDAO.getByID(id);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("PersonalClient not found with ID {}", id);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving PersonalClient by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // Get PersonalClient by Tax ID
    public Optional<PersonalClient> getByTaxID(String taxID) {
        try {
            Optional<PersonalClientEntity> entityOpt = personalClientDAO.getByTaxID(taxID);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("PersonalClient not found with Tax ID {}", taxID);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving PersonalClient by Tax ID {}: {}", taxID, e.getMessage());
            return Optional.empty();
        }
    }

    // Get all PersonalClients
    @Override
    public List<PersonalClient> getAll() {
        try {
            List<PersonalClientEntity> entities = personalClientDAO.getAll();
            return entities.stream()
                    .map(this::convertEntityToModel)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving all PersonalClients: {}", e.getMessage());
            return List.of();
        }
    }

    // Delete PersonalClient by ID
    @Override
    public PersonalClient update(Integer id, PersonalClient model) {
        try {
            Optional<PersonalClientEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert PersonalClient model to entity");
                return null;
            }
            PersonalClientEntity updated = personalClientDAO.updateByID(entityOpt.get());
            return convertEntityToModel(updated).orElse(null);
        } catch (SQLException e) {
            LOGGER.warn("Database error updating PersonalClient with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Delete PersonalClient by ID with validation.
     * Validation logic:
     * - For each sole-owned account, validate it can be deleted (balance = 0, credit paid off)
     * - If any sole-owned account fails validation, throw AccountDeletionException with details
     * - Joint accounts are simply unlinked from the client
     * - Only deletes the client if all validations pass
     * 
     * @throws AccountDeletionException if any sole-owned account fails validation
     */
    public boolean delete(Integer id) throws AccountDeletionException {
        try {
            // Get all accounts owned by this client
            java.util.Map<Integer, String> clientAccounts = getClientAccounts(id);
            
            // First pass: Validate all sole-owned accounts before attempting deletion
            StringBuilder validationErrors = new StringBuilder();
            for (Integer accountId : clientAccounts.keySet()) {
                try {
                    boolean isJoint = clientAccountDAO.isJointAccount(accountId);
                    
                    if (!isJoint) {
                        // This client is the sole owner - validate it can be deleted
                        String validationError = validateAccountForDeletion(accountId);
                        if (validationError != null) {
                            validationErrors.append(validationError).append("\n");
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.warn("Error validating account {} for client deletion: {}", accountId, e.getMessage());
                    validationErrors.append("Error validating account ").append(accountId).append(": ").append(e.getMessage()).append("\n");
                }
            }
            
            // If any validation errors, throw exception before attempting deletion
            if (validationErrors.length() > 0) {
                throw new AccountDeletionException(
                    "Cannot delete client: one or more sole-owned accounts have outstanding balances.",
                    validationErrors.toString()
                );
            }
            
            // Second pass: Delete all sole-owned accounts that passed validation
            for (Integer accountId : clientAccounts.keySet()) {
                try {
                    boolean isJoint = clientAccountDAO.isJointAccount(accountId);
                    
                    if (!isJoint) {
                        // This client is the sole owner - delete the account
                        deleteAccountByIdAndType(accountId);
                    }
                    // If it's a joint account, leave it - it will be unlinked when client is deleted
                } catch (SQLException e) {
                    LOGGER.warn("Error deleting account {} for client deletion: {}", accountId, e.getMessage());
                    // Continue deleting other accounts even if one fails
                }
            }
            
            // Now delete the client (CASCADE will remove client_accounts entries)
            return personalClientDAO.deleteByID(id);
        } catch (SQLException e) {
            LOGGER.warn("Database error deleting PersonalClient with ID {}: {}", id, e.getMessage());
            throw new AccountDeletionException("Database error during client deletion: " + e.getMessage());
        }
    }
    
    /**
     * Validates if an account can be deleted based on its balance.
     * For checking/savings: balance must be 0
     * For credit lines: balance must be fully paid off (0)
     * 
     * @return null if valid, error message if invalid
     */
    private String validateAccountForDeletion(Integer accountId) throws SQLException {
        // Try to validate as a CheckingAccount
        CheckingAccountService checkingService = new CheckingAccountService();
        var checkingAccount = checkingService.getById(accountId)
            .filter(account -> account instanceof com.jbank.model.CheckingAccount)
            .map(account -> (com.jbank.model.CheckingAccount) account);
        
        if (checkingAccount.isPresent()) {
            com.jbank.model.CheckingAccount checking = checkingAccount.get();
            if (checking.getBalance() != 0) {
                return String.format("Checking Account #%d has balance $%.2f. Please withdraw all funds before deleting account.", 
                    accountId, checking.getBalance());
            }
            return null;
        }
        
        // Try to validate as a SavingsAccount
        SavingsAccountService savingsService = new SavingsAccountService();
        var savingsAccount = savingsService.getById(accountId)
            .filter(account -> account instanceof com.jbank.model.SavingsAccount)
            .map(account -> (com.jbank.model.SavingsAccount) account);
        
        if (savingsAccount.isPresent()) {
            com.jbank.model.SavingsAccount savings = savingsAccount.get();
            if (savings.getBalance() != 0) {
                return String.format("Savings Account #%d has balance $%.2f. Please withdraw all funds before deleting account.", 
                    accountId, savings.getBalance());
            }
            return null;
        }
        
        // Try to validate as a CreditLine
        CreditLineService creditLineService = new CreditLineService();
        var creditLineAccount = creditLineService.getById(accountId)
            .filter(account -> account instanceof com.jbank.model.CreditLine)
            .map(account -> (com.jbank.model.CreditLine) account);
        
        if (creditLineAccount.isPresent()) {
            com.jbank.model.CreditLine creditLine = creditLineAccount.get();
            if (creditLine.getBalance() != 0) {
                return String.format("Credit Line #%d has outstanding balance of $%.2f. Please pay off the balance before deleting account.", 
                    accountId, creditLine.getBalance());
            }
            return null;
        }
        
        LOGGER.warn("Could not find account {} in any account type for validation", accountId);
        return null;
    }
    
    /**
     * Helper method to delete an account by ID.
     * Tries each account type DAO to find and delete the account.
     */
    private void deleteAccountByIdAndType(Integer accountId) throws SQLException {
        // Try to delete as a CheckingAccount
        CheckingAccountService checkingService = new CheckingAccountService();
        if (checkingService.getById(accountId).isPresent()) {
            checkingService.delete(accountId);
            return;
        }
        
        // Try to delete as a SavingsAccount
        SavingsAccountService savingsService = new SavingsAccountService();
        if (savingsService.getById(accountId).isPresent()) {
            savingsService.delete(accountId);
            return;
        }
        
        // Try to delete as a CreditLine
        CreditLineService creditLineService = new CreditLineService();
        if (creditLineService.getById(accountId).isPresent()) {
            creditLineService.delete(accountId);
            return;
        }
        
        LOGGER.warn("Could not find account {} in any account type", accountId);
    }

    /**
     * Get all accounts owned by this client.
     * @param clientId Customer ID
     * @return Map of account IDs to ownership types
     */
    public java.util.Map<Integer, String> getClientAccounts(int clientId) {
        try {
            return clientAccountDAO.getAccountsByClient(clientId);
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving accounts for client {}: {}", clientId, e.getMessage());
            return java.util.Map.of();
        }
    }

    // Conversion methods (required by interface)
    @Override
    public Optional<PersonalClient> convertEntityToModel(PersonalClientEntity entity) {
        try {
            PersonalClient model = new PersonalClient(
                entity.getCustomerID(),
                entity.getName(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.getTaxID(),
                entity.getCreditScore(),
                entity.getYearlyIncome(),
                entity.getTotalDebt()
            );
            return Optional.of(model);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid data in entity, cannot convert to model: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<PersonalClientEntity> convertModelToEntity(PersonalClient model) {
        try {
            String formattedPhone = formatPhoneNumber(model.getPhoneNumber());
            String formattedTaxID = formatTaxID(model.getTaxID());
            
            PersonalClientEntity entity = new PersonalClientEntity(
                model.getCustomerID(),
                formattedPhone,
                model.getAddress(),
                model.getName(),
                formattedTaxID,
                model.getCreditScore(),
                model.getYearlyIncome(),
                model.getTotalDebt()
            );
            return Optional.of(entity);
        } catch (Exception e) {
            LOGGER.warn("Error converting PersonalClient model to entity: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private String formatPhoneNumber(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return String.format("(%s) %s-%s", digits.substring(0, 3), digits.substring(3, 6), digits.substring(6));
    }

    private String formatTaxID(String taxID) {
        String digits = taxID.replaceAll("[^0-9]", "");
        return String.format("%s-%s-%s", digits.substring(0, 3), digits.substring(3, 5), digits.substring(5));
    }

}