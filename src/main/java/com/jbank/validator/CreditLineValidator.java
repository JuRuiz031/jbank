package com.jbank.validator;

/**
 * Validator for CreditLine-specific fields.
 * Uses AccountValidator for common fields + validates credit limit, interest rate, and minimum payment.
 */
public class CreditLineValidator {
    
    private CreditLineValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a CreditLineEntity.
     * Returns true if all fields are valid, false otherwise.
     */
    // TODO: Add validate method once CreditLineEntity is created
    
    /**
     * Validates credit limit.
     */
    public static boolean isValidCreditLimit(double creditLimit) {
        // TODO: Implement (use ValidationUtils.isPositive)
        return false;
    }
    
    /**
     * Validates interest rate.
     */
    public static boolean isValidInterestRate(double interestRate) {
        // TODO: Implement (use ValidationUtils.isValidPercentage)
        return false;
    }
    
    /**
     * Validates minimum payment percentage.
     */
    public static boolean isValidMinPaymentPercentage(double minPaymentPercentage) {
        // TODO: Implement (use ValidationUtils.isValidPercentage)
        return false;
    }
}
