package com.jbank.model;

/**
 *
 * @author juanfruiz
 */
public class CreditLine extends AbstractAccount {
    private double creditLimit;
    private double interestRate;
    private double minPaymentPercentage;

    public CreditLine(int customerID, int accountID, double initialDeposit, String accountName,
            double creditLimit, double interestRate, double minPaymentPercentage) {
        super(customerID, accountID, initialDeposit, accountName);
        // Input validation
        if(creditLimit < 0) {
            throw new IllegalArgumentException("Credit limit cannot be negative.");
        }
        if(interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative.");
        }
        if(minPaymentPercentage < 0 || minPaymentPercentage > 100) {
            throw new IllegalArgumentException("Minimum payment percentage must be between 0 and 100.");
        }

        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.minPaymentPercentage = minPaymentPercentage;
    }

    // Mutators
    public void updateCreditLimit(double creditLimit) {
        if(creditLimit < 0) {
            throw new IllegalArgumentException("Credit limit cannot be negative.");
        }
        this.creditLimit = creditLimit;
    }
    public void updateInterestRate(double interestRate) {
        if(interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative.");
        }
        this.interestRate = interestRate;
    }
    public void updateMinPaymentPercentage(double minPaymentPercentage) {
        if(minPaymentPercentage < 0 || minPaymentPercentage > 100) {
            throw new IllegalArgumentException("Minimum payment percentage must be between 0 and 100.");
        }
        this.minPaymentPercentage = minPaymentPercentage / 100;
    }
    public void makePayment(double paymentAmount) {
        if(paymentAmount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        double newBalance = this.getBalance() - paymentAmount;
        if (newBalance < -creditLimit) {
            throw new IllegalArgumentException("Payment would exceed credit limit. Would you like to pay off the full balance?");
        }
        this.updateBalance(newBalance);
    }


    // Getters
    public double getCreditLimit() {
        return creditLimit;
    }
    public double getInterestRate() {
        return interestRate;
    }
    public double getMinPaymentPercentage() {
        return minPaymentPercentage * 100;
    }
}
