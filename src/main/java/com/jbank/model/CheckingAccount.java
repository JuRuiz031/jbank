package com.jbank.model;

/**
 *
 * @author juanfruiz
 */
public class CheckingAccount extends AbstractAccount implements Depositable, Withdrawable {
    private double overdraftFee;
    private double overdraftLimit;

    public CheckingAccount(int customerID, int accountID, double initialDeposit, String accountName,
            double overdraftFee, double overdraftLimit) {
        super(customerID, accountID, initialDeposit, accountName);
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
        this.setBalance(newBalance);
    }

    @Override
    public void withdraw (double withdrawAmount) {
        if(withdrawAmount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        double newBalance = this.getBalance() - withdrawAmount;
        
        // Check if withdrawal would exceed overdraft limit
        // Account can go negative down to -overdraftLimit
        if (newBalance < -overdraftLimit) {
            throw new IllegalArgumentException("Withdrawal would exceed overdraft limit.");
        }
        
        // If balance goes negative, apply overdraft fee
        if (newBalance < 0) {
            newBalance -= overdraftFee;
        }
        
        this.setBalance(newBalance);
    }

    // Setter
    public void setOverdraftFee(double overdraftFee) {
        if(overdraftFee < 0) {
            throw new IllegalArgumentException("Overdraft fee cannot be negative.");
        }
        this.overdraftFee = overdraftFee;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        if(overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    // Getter
    public double getOverdraftFee() {
        return overdraftFee;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public String toString() {
        return "CheckingAccount{" +
                "accountID=" + getAccountID() +
                ", customerID=" + getCustomerID() +
                ", accountName='" + getAccountName() + '\'' +
                ", overdraftFee=" + overdraftFee +
                ", overdraftLimit=" + overdraftLimit +
                '}';
    }

}