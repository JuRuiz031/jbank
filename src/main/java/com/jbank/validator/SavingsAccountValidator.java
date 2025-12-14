package com.jbank.validator;

/**
 * Validator for SavingsAccount-specific fields.
 * Uses AccountValidator for common fields + validates interest rate and withdrawal limit.
 */
public class SavingsAccountValidator {
    
    private SavingsAccountValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a SavingsAccountEntity.
     * Returns true if all fields are valid, false otherwise.
     */
    // TODO: Add validate method once SavingsAccountEntity is created
    
    /**
     * Validates interest rate.
     */
    public static boolean isValidInterestRate(double interestRate) {
        // TODO: Implement (use ValidationUtils.isValidPercentage)
        return false;
    }
    
    /**
     * Validates withdrawal limit.
     */
    public static boolean isValidWithdrawalLimit(int withdrawalLimit) {
        // TODO: Implement (positive integer)
        return false;
    }
}
