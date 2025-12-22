package com.jbank.repository.entities;

import java.util.Objects;

/**
 * Entity class representing a credit line in the database.
 * Maps to accounts and credit_lines tables.
 * 
 * @author juanf
 */
public class CreditLineEntity {
    private int accountID;
    private int customerID;
    private double balance;
    private double creditLimit;
    private double interestRate;
    private double minPaymentPercentage;
    private String accountName;

    public CreditLineEntity() {
    }

    public CreditLineEntity(int accountID, int customerID, double balance, double creditLimit, double interestRate, double minPaymentPercentage) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.minPaymentPercentage = minPaymentPercentage;
    }

    public CreditLineEntity(int accountID, int customerID, double balance, double creditLimit, double interestRate, double minPaymentPercentage, String accountName) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.minPaymentPercentage = minPaymentPercentage;
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

    public double getCreditLimit() {
        return creditLimit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getMinPaymentPercentage() {
        return minPaymentPercentage;
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

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void setMinPaymentPercentage(double minPaymentPercentage) {
        this.minPaymentPercentage = minPaymentPercentage;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditLineEntity that = (CreditLineEntity) o;
        return accountID == that.accountID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID);
    }

    @Override
    public String toString() {
        return "CreditLineEntity{" +
                "accountID=" + accountID +
                ", customerID=" + customerID +
                ", balance=" + balance +
                ", creditLimit=" + creditLimit +
                ", interestRate=" + interestRate +
                ", minPaymentPercentage=" + minPaymentPercentage +
                '}';
    }
}
