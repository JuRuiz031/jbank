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

    // Setter
    public void setAccountID(int newID) {
        if(newID <= 0) {
            throw new IllegalArgumentException("Account ID must be positive.");
        }
        this.accountID = newID;
    }
    // Protected setter for balance to be used by subclasses
    // Rounds to 2 decimal places to prevent floating-point precision issues
    protected void setBalance(double newBalance) {
        this.balance = Math.round(newBalance * 100.0) / 100.0;
    }

    // Getters
    public int getAccountID() {
        return accountID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", customerID=" + customerID +
                ", accountName='" + accountName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAccount that = (AbstractAccount) o;
        // Use only database identity for accounts
        return this.accountID > 0 && this.accountID == that.accountID;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(accountID);
    }
}
