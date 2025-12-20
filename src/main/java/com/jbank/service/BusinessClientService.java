package com.jbank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbank.model.BusinessClient;
import com.jbank.repository.DAO.BusinessClientDAO;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.entities.BusinessClientEntity;

/**
 * Service layer for BusinessClient operations.
 * Handles business logic, validation, and DAO orchestration.
 * 
 * @author juanf
 */
public class BusinessClientService implements ServiceInterface<BusinessClientEntity, BusinessClient> {
    // Logger for the class
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessClientService.class);

    // DAO instance (constructor injected for testability)
    private final BusinessClientDAO businessClientDAO;
    private final ClientAccountDAO clientAccountDAO;

    public BusinessClientService() {
        this(new BusinessClientDAO(), new ClientAccountDAO());
    }

    public BusinessClientService(BusinessClientDAO businessClientDAO, ClientAccountDAO clientAccountDAO) {
        this.businessClientDAO = businessClientDAO;
        this.clientAccountDAO = clientAccountDAO;
    }

    // Create BusinessClient
    @Override
    public Integer create(BusinessClient model) {
        try {
            Optional<BusinessClientEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert BusinessClient model to entity");
                return null;
            }
            Integer newID = businessClientDAO.create(entityOpt.get());
            return newID;
        } catch (SQLException e) {
            LOGGER.warn("Database error creating BusinessClient: {}", e.getMessage());
            return null;
        }
    }

    // Get BusinessClient by ID
    @Override
    public Optional<BusinessClient> getById(Integer id) {
        try {
            Optional<BusinessClientEntity> entityOpt = businessClientDAO.getByID(id);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("BusinessClient not found with ID {}", id);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving BusinessClient by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // Get BusinessClient by EIN
    public Optional<BusinessClient> getByEIN(String ein) {
        try {
            Optional<BusinessClientEntity> entityOpt = businessClientDAO.getByEIN(ein);
            if (entityOpt.isEmpty()) {
                LOGGER.debug("BusinessClient not found with EIN {}", ein);
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving BusinessClient by EIN {}: {}", ein, e.getMessage());
            return Optional.empty();
        }
    }

    // Get all BusinessClients
    @Override
    public List<BusinessClient> getAll() {
        try {
            List<BusinessClientEntity> entities = businessClientDAO.getAll();
            return entities.stream()
                    .map(this::convertEntityToModel)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (SQLException e) {
            LOGGER.warn("Database error retrieving all BusinessClients: {}", e.getMessage());
            return List.of();
        }
    }

    // Update BusinessClient
    @Override
    public BusinessClient update(Integer id, BusinessClient model) {
        try {
            Optional<BusinessClientEntity> entityOpt = convertModelToEntity(model);
            if (entityOpt.isEmpty()) {
                LOGGER.error("Failed to convert BusinessClient model to entity");
                return null;
            }
            BusinessClientEntity updated = businessClientDAO.updateByID(entityOpt.get());
            return convertEntityToModel(updated).orElse(null);
        } catch (SQLException e) {
            LOGGER.warn("Database error updating BusinessClient with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Delete BusinessClient by ID.
     * The database CASCADE will automatically remove client_accounts entries.
     * However, accounts themselves are NOT deleted - they may be joint accounts.
     * Primary accounts without other owners will become orphaned (this is intentional).
     */
    public boolean delete(Integer id) {
        try {
            return businessClientDAO.deleteByID(id);
        } catch (SQLException e) {
            LOGGER.warn("Database error deleting BusinessClient with ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Get all accounts owned by this client.
     * @param clientId Customer ID
     * @return Map of account IDs to ownership types (PRIMARY/JOINT)
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
    public Optional<BusinessClient> convertEntityToModel(BusinessClientEntity entity) {
        try {
            BusinessClient model = new BusinessClient(
                entity.getCustomerID(),
                entity.getName(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.getEIN(),
                entity.getBusinessType(),
                entity.getContactPersonName(),
                entity.getContactPersonTitle(),
                entity.getTotalAssetValue(),
                entity.getAnnualRevenue(),
                entity.getAnnualProfit()
            );
            return Optional.of(model);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid data in entity, cannot convert to model: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<BusinessClientEntity> convertModelToEntity(BusinessClient model) {
        try {
            String formattedPhone = formatPhoneNumber(model.getPhoneNumber());
            String formattedEIN = formatEIN(model.getEin());
            
            BusinessClientEntity entity = new BusinessClientEntity(
                model.getCustomerID(),
                formattedPhone,
                model.getAddress(),
                model.getName(),
                formattedEIN,
                model.getBusinessType(),
                model.getContactName(),
                model.getContactTitle(),
                model.getTotalAssetValue(),
                model.getAnnualRevenue(),
                model.getAnnualProfit()
            );
            return Optional.of(entity);
        } catch (Exception e) {
            LOGGER.warn("Error converting BusinessClient model to entity: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private String formatPhoneNumber(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return String.format("(%s) %s-%s", digits.substring(0, 3), digits.substring(3, 6), digits.substring(6));
    }

    private String formatEIN(String ein) {
        String digits = ein.replaceAll("[^0-9]", "");
        return String.format("%s-%s", digits.substring(0, 2), digits.substring(2));
    }
}
