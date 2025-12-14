package com.jbank.service;


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
public class PersonalClientService implements ServiceInterface <PersonalClientEntity, PersonalClient>{
    // Logger for the class
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalClientService.class);

    // DAO instance
    private final PersonalClientDAO personalClientDAO = new PersonalClientDAO();

    @Override
    public Integer createEntity(PersonalClientEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<PersonalClientEntity> getEntityById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PersonalClientEntity> getAllEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PersonalClientEntity updateEntity(Integer id, PersonalClientEntity newEntity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Conversion methods and tools
    @Override
    public Optional<PersonalClient> convertEntityToModel(PersonalClientEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<PersonalClientEntity> convertModelToEntity(PersonalClient model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<PersonalClient> getModelById(Integer id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
