package com.jbank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import com.jbank.repository.DAO.BusinessClientDAO;
// import com.jbank.repository.entities.BusinessClientEntity;

/**
 * Service layer for BusinessClient operations.
 * Handles business logic, validation, and DAO orchestration.
 * 
 * @author juanf
 */
public class BusinessClientService /* implements ServiceInterface<BusinessClientEntity, BusinessClient> */ {
    // Logger for the class
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessClientService.class);

    // DAO instance
    // TODO: Uncomment when BusinessClientDAO and BusinessClientEntity are created
    // private final BusinessClientDAO businessClientDAO = new BusinessClientDAO();

    // TODO: Implement ServiceInterface methods once BusinessClientEntity is created
    /*
    @Override
    public Integer createEntity(BusinessClientEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<BusinessClientEntity> getEntityById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<BusinessClientEntity> getAllEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BusinessClientEntity updateEntity(Integer id, BusinessClientEntity newEntity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Conversion methods and tools
    @Override
    public Optional<BusinessClient> convertEntityToModel(BusinessClientEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<BusinessClientEntity> convertModelToEntity(BusinessClient model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<BusinessClient> getModelById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    */
}
