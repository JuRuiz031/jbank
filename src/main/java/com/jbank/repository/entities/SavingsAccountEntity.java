package com.jbank.repository.entities;

import java.util.Objects;

/**
 * Entity class representing a savings account in the database.
 * Maps to accounts and savings_accounts tables.
 * 
 * @author juanf
 */
public class SavingsAccountEntity {
    private int accountID;
    private int customerID;
    private double balance;
    private double interestRate;
    private int withdrawalLimit;
    private int withdrawalCounter;
    private String accountName;

    public SavingsAccountEntity() {
    }

    public SavingsAccountEntity(int accountID, int customerID, double balance, double interestRate, int withdrawalLimit) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.interestRate = interestRate;
        this.withdrawalLimit = withdrawalLimit;
        this.withdrawalCounter = 0;
    }

    public SavingsAccountEntity(int accountID, int customerID, double balance, double interestRate, int withdrawalLimit, int withdrawalCounter) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.interestRate = interestRate;
        this.withdrawalLimit = withdrawalLimit;
        this.withdrawalCounter = withdrawalCounter;
    }

    public SavingsAccountEntity(int accountID, int customerID, double balance, double interestRate, int withdrawalLimit, int withdrawalCounter, String accountName) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.interestRate = interestRate;
        this.withdrawalLimit = withdrawalLimit;
        this.withdrawalCounter = withdrawalCounter;
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

    public double getInterestRate() {
        return interestRate;
    }

    public int getWithdrawalLimit() {
        return withdrawalLimit;
    }

    public int getWithdrawalCounter() {
        return withdrawalCounter;
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

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void setWithdrawalLimit(int withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }

    public void setWithdrawalCounter(int withdrawalCounter) {
        this.withdrawalCounter = withdrawalCounter;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavingsAccountEntity that = (SavingsAccountEntity) o;
        return accountID == that.accountID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID);
    }

    @Override
    public String toString() {
        return "SavingsAccountEntity{" +
                "accountID=" + accountID +
                ", customerID=" + customerID +
                ", balance=" + balance +
                ", interestRate=" + interestRate +
                ", withdrawalLimit=" + withdrawalLimit +
                ", withdrawalCounter=" + withdrawalCounter +
                '}';
    }
}
