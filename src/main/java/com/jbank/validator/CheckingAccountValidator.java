package com.jbank.validator;

/**
 * Validator for CheckingAccount-specific fields.
 * Uses AccountValidator for common fields + validates overdraft fee.
 */
public class CheckingAccountValidator {
    
    private CheckingAccountValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a CheckingAccountEntity.
     * Returns true if all fields are valid, false otherwise.
     */
    // TODO: Add validate method once CheckingAccountEntity is created
    
    /**
     * Validates overdraft fee.
     */
    public static boolean isValidOverdraftFee(double overdraftFee) {
        // TODO: Implement (non-negative, use ValidationUtils)
        return false;
    }
}
