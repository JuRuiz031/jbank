package com.jbank.validator;

import com.jbank.model.CheckingAccount;

/**
 * Validator for CheckingAccount-specific fields.
 * Uses AccountValidator for common fields + validates overdraft fee.
 */
public class CheckingAccountValidator {
    
    private CheckingAccountValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a CheckingAccount model.
     * Returns true if all fields are valid, false otherwise.
     */
    public static boolean validate(CheckingAccount account) {
        return account != null &&
               AccountValidator.isValidClientId(account.getCustomerID()) &&
               AccountValidator.isValidAccountId(account.getAccountID()) &&
               AccountValidator.isValidBalance(account.getBalance()) &&
               isValidOverdraftFee(account.getOverdraftFee());
    }
    
    /**
     * Validates overdraft fee (non-negative dollar amount).
     */
    public static boolean isValidOverdraftFee(double overdraftFee) {
        return ValidationUtils.isNonNegative(overdraftFee) && ValidationUtils.isValidDollarAmount(overdraftFee);
    }
}
