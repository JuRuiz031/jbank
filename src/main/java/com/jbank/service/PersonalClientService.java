package com.jbank.service;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jbank.model.PersonalClient;
import com.jbank.repository.DAO.PersonalClientDAO;
import com.jbank.repository.entities.PersonalClientEntity;
/**
 *
 * @author juanf
 */
public class PersonalClientService implements ServiceInterface<PersonalClientEntity, PersonalClient>{
    // Logger for the class
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalClientService.class);

    // DAO instance
    private final PersonalClientDAO personalClientDAO = new PersonalClientDAO();

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
            LOGGER.error("Error creating PersonalClient: {}", e.getMessage());
            return null;
        }
    }

    // Get PersonalClient by ID
    @Override
    public Optional<PersonalClient> getById(Integer id) {
        try {
            Optional<PersonalClientEntity> entityOpt = personalClientDAO.getByID(id);
            if (entityOpt.isEmpty()) {
                return Optional.empty();
            }
            return convertEntityToModel(entityOpt.get());
        } catch (SQLException e) {
            LOGGER.error("Error retrieving PersonalClient by ID {}: {}", id, e.getMessage());
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
            LOGGER.error("Error retrieving all PersonalClients: {}", e.getMessage());
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
            LOGGER.error("Error updating PersonalClient with ID {}: {}", id, e.getMessage());
            return null;
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
            LOGGER.error("Invalid data in entity, cannot convert to model: {}", e.getMessage());
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
            LOGGER.error("Error converting PersonalClient model to entity: {}", e.getMessage());
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