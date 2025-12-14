package com.jbank.service;

import java.util.List;
import java.util.Optional;
/**
 *
 * @author juanf
 */
public interface ServiceInterface<T,U> {

    // CRUD operations
    Integer createEntity(T entity);
    Optional<T> getEntityById(Integer id);
    List<T> getAllEntities();
    T updateEntity(Integer id, T newEntity);

    // Conversion methods and tools
    Optional<U> convertEntityToModel(T entity);
    Optional<T> convertModelToEntity(U model);
    Optional<U> getModelById(Integer id);
}
