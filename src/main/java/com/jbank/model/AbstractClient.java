package com.jbank.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juanfruiz
 */
public abstract class AbstractClient implements ChangeAddress, ChangeCustomerName, ChangePhoneNumber{
    // Static long to keep track of next available Account ID number
    private static long nextID = 1;

    // Base variables
    private final long customerID;
    private String phoneNumber;
    private String address;
    private String name;

    // ArrayList of accounts belonging to this customer
    private List<AbstractAccount> accounts = new ArrayList<>();

    // Constructor
    protected AbstractClient(String phoneNumber, String address, String name) {
        this.customerID = nextID++;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.name = name;
    }

    // Getters
    public long getCustomerID() {
        return customerID;
    }

    public String getNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
