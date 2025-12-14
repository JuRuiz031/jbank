package com.jbank.validator;

/**
 * Validator for common client fields shared by all client types.
 * Contains static methods for name, address, and phone validation.
 */
public class ClientValidator {
    
    private ClientValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates client name.
     */
    public static boolean isValidName(String name) {
        // TODO: Implement (use ValidationUtils.isValidString + specific rules)
        return false;
    }
    
    /**
     * Validates client address.
     */
    public static boolean isValidAddress(String address) {
        // TODO: Implement
        return false;
    }
    
    /**
     * Validates client phone number.
     */
    public static boolean isValidPhone(String phone) {
        // TODO: Implement
        return false;
    }
}
