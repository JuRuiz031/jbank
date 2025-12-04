package com.jbank.model;

/**
 *
 * @author juanfruiz
 */
public class AbstractAccount {
    // Static long to keep track of next available Account ID number
    private static long nextID = 1;

    // Base variables
    private final long accountID;
    private final long customerID;
    private double balance;
    private final String accountName;


    // Constructor
    public AbstractAccount (long customerID, double balance, String accountName) {
        this.accountID = nextID++;
        this.customerID = customerID;
        this.balance = balance;
        this.accountName = accountName;
    }

    // Getters
    public long getAccountID() {
        return accountID;
    }

    public long getCustomerID() {
        return customerID;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountName() {
        return accountName;
    }


}
