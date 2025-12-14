package com.jbank.validator;

/**
 * Validator for BusinessClient-specific fields.
 * Uses ClientValidator for common fields + validates business name and EIN.
 */
public class BusinessClientValidator {
    
    private BusinessClientValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a BusinessClientEntity.
     * Returns true if all fields are valid, false otherwise.
     */
    // TODO: Add validate method once BusinessClientEntity is created
    
    /**
     * Validates business name.
     */
    public static boolean isValidBusinessName(String businessName) {
        // TODO: Implement
        return false;
    }
    
    /**
     * Validates business tax ID (EIN).
     */
    public static boolean isValidEIN(String ein) {
        // TODO: Implement (EIN format validation)
        return false;
    }
}
