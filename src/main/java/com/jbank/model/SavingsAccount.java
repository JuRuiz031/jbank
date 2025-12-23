package com.jbank.model;

/**
 *
 * @author juanfruiz
 */
public class SavingsAccount extends AbstractAccount implements Depositable, Withdrawable {
    private double interestRate;
    private int withdrawalLimit;
    private int withdrawalCounter;

    public SavingsAccount(int customerID, int accountID, double initialDeposit, String accountName,
            double interestRate, int withdrawalLimit) {
        super(customerID, accountID, initialDeposit, accountName);
        // Input validation
        if(interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative.");
        }
        if(withdrawalLimit < 0) {
            throw new IllegalArgumentException("Withdrawal limit cannot be negative.");
        }
        if(withdrawalCounter < 0) {
            throw new IllegalArgumentException("Withdrawal counter cannot be negative.");
        }

        this.interestRate = interestRate;
        this.withdrawalLimit = withdrawalLimit;
        this.withdrawalCounter = 0;
    }

    // Interface Implementations
    @Override
    public void deposit (double depositAmount) {
        if(depositAmount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        double newBalance = this.getBalance() + depositAmount;
        this.setBalance(newBalance);
    }

    @Override
    public void withdraw (double withdrawAmount) {
        if(withdrawAmount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (withdrawalCounter >= withdrawalLimit) {
            throw new IllegalArgumentException("Withdrawal limit reached.");
        }
        double newBalance = this.getBalance() - withdrawAmount;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal.");
        }
        this.setBalance(newBalance);
        withdrawalCounter++;
    }

    // Setters / Mutators
    public void resetWithdrawalCounter() {
        this.withdrawalCounter = 0;
    }
    public void setInterestRate(double interestRate) {
        if(interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative.");
        }
        this.interestRate = interestRate;
    }
    public void setWithdrawalLimit(int withdrawalLimit) {
        if(withdrawalLimit < 0) {
            throw new IllegalArgumentException("Withdrawal limit cannot be negative.");
        }
        this.withdrawalLimit = withdrawalLimit;
    }

    public void applyInterest() {
        double interestEarned = this.getBalance() * (interestRate / 100.0);
        double newBalance = this.getBalance() + interestEarned;
        this.setBalance(newBalance);
    }


    // Getter
    public double getInterestRate() {
        return interestRate;
    }

    public int getWithdrawalLimit() {
        return withdrawalLimit;
    }

    public int getWithdrawalCounter() {
        return withdrawalCounter;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "accountID=" + getAccountID() +
                ", customerID=" + getCustomerID() +
                ", accountName='" + getAccountName() + '\'' +
                ", interestRate=" + interestRate +
                ", withdrawalLimit=" + withdrawalLimit +
                ", withdrawalCounter=" + withdrawalCounter +
                '}';
    }

}
