package com.jbank.validator;

/**
 * Validator for common account fields shared by all account types.
 * Contains static methods for balance, accountID, and clientID validation.
 */
public class AccountValidator {
    
    private AccountValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates account balance.
     */
    public static boolean isValidBalance(double balance) {
        // TODO: Implement (use ValidationUtils)
        return false;
    }
    
    /**
     * Validates account ID.
     */
    public static boolean isValidAccountId(Integer accountId) {
        // TODO: Implement (use ValidationUtils.isValidId)
        return false;
    }
    
    /**
     * Validates client ID.
     */
    public static boolean isValidClientId(Integer clientId) {
        // TODO: Implement (use ValidationUtils.isValidId)
        return false;
    }
}
