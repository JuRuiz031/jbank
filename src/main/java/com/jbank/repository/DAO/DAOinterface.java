package com.jbank.repository.DAO;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAOinterface<T> {
    // CRUD Operations (Create, Read, Update, Delete)

    // Create
    public Integer create(T client) throws SQLException;

    // Read by ID
    public Optional<T> getByID(Integer id) throws SQLException;

    // Read all
    public List<T> getAll() throws SQLException;

    // Update
    public T updateByID(T client) throws SQLException;

    // Delete
    public boolean deleteByID(Integer id) throws SQLException;

}
