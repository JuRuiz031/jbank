package com.jbank.service;

import java.util.List;
import java.util.Optional;
/**
 *
 * @author juanf
 * @param <T> Entity type
 * @param <U> Model type
 */
public interface ServiceInterface<T, U> {

    // CRUD operations (model-based) - Controller-facing API
    Integer create(U model);
    Optional<U> getById(Integer id);
    List<U> getAll();
    U update(Integer id, U model);

    // Conversion methods
    Optional<U> convertEntityToModel(T entity);
    Optional<T> convertModelToEntity(U model);
}
