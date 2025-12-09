package com.jbank.model;

/**
 *
 * @author juanfruiz
 */
public class AbstractAccount {
    // Base variables
    private int accountID;
    private int customerID;
    private double balance;
    private final String accountName;


    // Constructor
    public AbstractAccount (int customerID, int accountID, double balance, String accountName) {
        // Input validation
        if(balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        if(accountName == null || accountName.isEmpty() || accountName.length() < 3 || accountName.length() > 30) {
            throw new IllegalArgumentException("Account name must be between 3 and 30 characters.");

        }

        this.customerID = customerID;
        this.accountID = accountID;
        this.balance = balance;
        this.accountName = accountName;
    }

    // Mutator
    public void updateAccountID(int newID) {
        if(newID <= 0) {
            throw new IllegalArgumentException("Account ID must be positive.");
        }
        this.accountID = newID;
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
