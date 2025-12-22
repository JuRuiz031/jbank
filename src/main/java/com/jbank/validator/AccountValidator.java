package com.jbank.validator;

/**
 * Validator for common account fields shared by all account types.
 * Contains static methods for balance, accountID, and clientID validation.
 */
public class AccountValidator {
    
    private AccountValidator() {
        // Prevent instantiation
    }
    
    // Validates account balance (non-negative, valid dollar amount)
    public static boolean isValidBalance(double balance) {
        return ValidationUtils.isNonNegative(balance) && ValidationUtils.isValidDollarAmount(balance);
    }
    
    // Validates account ID (non-negative integer)
    public static boolean isValidAccountId(Integer accountId) {
        return accountId != null && accountId >= 0;
    }
    
    // Validates client ID (positive integer)
    public static boolean isValidClientId(Integer clientId) {
        return ValidationUtils.isValidId(clientId);
    }
}
