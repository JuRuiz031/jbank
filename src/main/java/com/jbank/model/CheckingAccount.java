package com.jbank.model;

public class CheckingAccount extends AbstractAccount implements Depositable, Withdrawable {
    private double overdraftFee;
    private double overdraftLimit;

    public CheckingAccount(long customerID, double initialDeposit, String accountName,
            double overdraftFee, double overdraftLimit) {
        super(customerID, initialDeposit, accountName);
        // Input validation
        if(overdraftFee < 0) {
            throw new IllegalArgumentException("Overdraft fee cannot be negative.");
        }
        if(overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }



        this.overdraftFee = overdraftFee;
        this.overdraftLimit = overdraftLimit;
    }

    // Interface implementations
    @Override
    public void deposit (double depositAmount) {
        if(depositAmount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        double newBalance = this.getBalance() + depositAmount;
        this.updateBalance(newBalance);
    }

    @Override
    public void withdraw (double withdrawAmount) {
        if(withdrawAmount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        double newBalance = this.getBalance() - withdrawAmount;
        if (newBalance < 0) {
            if(newBalance < -overdraftLimit) {
                throw new IllegalArgumentException("Withdrawal would exceed overdraft limit.");
            } else {
                newBalance -= overdraftFee;
            }
        }
        this.updateBalance(newBalance);
    }

    // Setter
    public void setOverdraftFee(double overdraftFee) {
        if(overdraftFee < 0) {
            throw new IllegalArgumentException("Overdraft fee cannot be negative.");
        }
        this.overdraftFee = overdraftFee;
    }

    // Getter
    public double getOverdraftFee() {
        return overdraftFee;
    }

}
