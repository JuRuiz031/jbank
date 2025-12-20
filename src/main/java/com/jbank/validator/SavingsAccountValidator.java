package com.jbank.validator;

import com.jbank.model.SavingsAccount;

/**
 * Validator for SavingsAccount-specific fields.
 * Uses AccountValidator for common fields + validates interest rate and withdrawal limit.
 */
public class SavingsAccountValidator {
    
    private SavingsAccountValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a SavingsAccount model.
     * Returns true if all fields are valid, false otherwise.
     */
    public static boolean validate(SavingsAccount account) {
        return account != null &&
               AccountValidator.isValidClientId(account.getCustomerID()) &&
               AccountValidator.isValidAccountId(account.getAccountID()) &&
               AccountValidator.isValidBalance(account.getBalance()) &&
               isValidInterestRate(account.getInterestRate()) &&
               isValidWithdrawalLimit(account.getWithdrawalLimit());
    }
    
    /**
     * Validates interest rate (percentage between 0 and 100).
     */
    public static boolean isValidInterestRate(double interestRate) {
        return ValidationUtils.isValidPercentage(interestRate);
    }
    
    /**
     * Validates withdrawal limit (positive integer).
     */
    public static boolean isValidWithdrawalLimit(int withdrawalLimit) {
        return withdrawalLimit > 0;
    }
}
