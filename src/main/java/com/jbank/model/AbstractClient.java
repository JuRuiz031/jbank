package com.jbank.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanfruiz
 */
public abstract class AbstractClient {
    // Base variables
    private int customerID;
    private String phoneNumber;
    private String address;
    private String name;

    // ArrayList of accounts belonging to this customer
    private List<AbstractAccount> accounts = new ArrayList<>();

    // Constructor
    protected AbstractClient(int customerID, String phoneNumber, String address, String name) {
        // Input validation
        if(phoneNumber == null || phoneNumber.replaceAll("[^0-9]", "").length() != 10) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        if(address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (name == null || name.isEmpty() || name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 3 and 50 characters.");
        }

        this.customerID = customerID;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.name = name;
    }


    // Setters
    public void setCustomerID(int newID) {
        if(newID <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        this.customerID = newID;
    }

    public void setPhoneNumber(String newNumber) {
        if(newNumber == null || newNumber.replaceAll("[^0-9]", "").length() != 10) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        this.phoneNumber = newNumber;
    }

    public void setAddress(String newAddress) {
        if(newAddress == null || newAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = newAddress;
    }

    public void setName(String newName) {
        if(newName == null || newName.isEmpty() || newName.length() < 3 || newName.length() > 50) {
            throw new IllegalArgumentException("Name must be between 3 and 50 characters.");
        }
        this.name = newName;
    }

    // Getters
    public int getCustomerID() {
        return customerID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

}