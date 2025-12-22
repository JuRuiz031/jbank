package com.jbank.repository.entities;

import java.util.Objects;

/**
 * Entity class representing a checking account in the database.
 * Maps to accounts and checking_accounts tables.
 * 
 * @author juanf
 */
public class CheckingAccountEntity {
    private int accountID;
    private int customerID;
    private double balance;
    private double overdraftFee;
    private String accountName;

    public CheckingAccountEntity() {
    }

    public CheckingAccountEntity(int accountID, int customerID, double balance, double overdraftFee) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.overdraftFee = overdraftFee;
    }

    public CheckingAccountEntity(int accountID, int customerID, double balance, double overdraftFee, String accountName) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.overdraftFee = overdraftFee;
        this.accountName = accountName;
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

    public double getOverdraftFee() {
        return overdraftFee;
    }

    public String getAccountName() {
        return accountName;
    }

    // Setters
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setOverdraftFee(double overdraftFee) {
        this.overdraftFee = overdraftFee;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckingAccountEntity that = (CheckingAccountEntity) o;
        return accountID == that.accountID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID);
    }

    @Override
    public String toString() {
        return "CheckingAccountEntity{" +
                "accountID=" + accountID +
                ", customerID=" + customerID +
                ", balance=" + balance +
                ", overdraftFee=" + overdraftFee +
                '}';
    }
}
