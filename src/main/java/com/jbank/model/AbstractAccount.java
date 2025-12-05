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
        // Input validation
        if(customerID < 0) { // This should never happen if created through AbstractClient, but just in case
            throw new IllegalArgumentException("Customer ID invalid, cannot be negative.");
        }
        if((nextID) < 0) {
            throw new IllegalArgumentException("Account ID creation is not possible, number of accounts has exceeded maximum limit.");
        }
        if(balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        if(accountName == null || accountName.isEmpty() || accountName.length() < 3 || accountName.length() > 30) {
            throw new IllegalArgumentException("Account name must be between 3 and 30 characters.");

        }

        this.accountID = nextID++;
        this.customerID = customerID;
        this.balance = balance;
        this.accountName = accountName;
    }

    // Protected Mutator for balance to be used by subclasses
    protected void updateBalance(double newBalance) {
        this.balance = newBalance;
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
