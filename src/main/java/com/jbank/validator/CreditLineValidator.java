package com.jbank.validator;

import com.jbank.model.CreditLine;

/**
 * Validator for CreditLine-specific fields.
 * Uses AccountValidator for common fields + validates credit limit, interest rate, and minimum payment.
 */
public class CreditLineValidator {
    
    private CreditLineValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a CreditLine model.
     * Returns true if all fields are valid, false otherwise.
     */
    public static boolean validate(CreditLine account) {
        return account != null &&
               AccountValidator.isValidClientId(account.getCustomerID()) &&
               AccountValidator.isValidAccountId(account.getAccountID()) &&
               AccountValidator.isValidBalance(account.getBalance()) &&
               isValidCreditLimit(account.getCreditLimit()) &&
               isValidInterestRate(account.getInterestRate()) &&
               isValidMinPaymentPercentage(account.getMinPaymentPercentage());
    }
    
    /**
     * Validates credit limit (positive dollar amount).
     */
    public static boolean isValidCreditLimit(double creditLimit) {
        return ValidationUtils.isPositive(creditLimit) && ValidationUtils.isValidDollarAmount(creditLimit);
    }
    
    /**
     * Validates interest rate (percentage between 0 and 100).
     */
    public static boolean isValidInterestRate(double interestRate) {
        return ValidationUtils.isValidPercentage(interestRate);
    }
    
    /**
     * Validates minimum payment percentage (percentage between 0 and 100).
     */
    public static boolean isValidMinPaymentPercentage(double minPaymentPercentage) {
        return ValidationUtils.isValidPercentage(minPaymentPercentage);
    }
}
