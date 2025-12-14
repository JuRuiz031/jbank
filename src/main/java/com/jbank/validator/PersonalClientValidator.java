package com.jbank.validator;

import com.jbank.repository.entities.PersonalClientEntity;

/**
 * Validator for PersonalClient-specific fields.
 * Uses ClientValidator for common fields + validates taxID, creditScore, yearlyIncome, totalDebt.
 */
public class PersonalClientValidator {
    
    private PersonalClientValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates a PersonalClientEntity.
     * Returns true if all fields are valid, false otherwise.
     */
    public static boolean validate(PersonalClientEntity entity) {
        // TODO: Implement
        // Use ClientValidator.isValidName(entity.getName())
        // Use ClientValidator.isValidAddress(entity.getAddress())
        // Use ClientValidator.isValidPhone(entity.getPhone())
        // Validate taxID, creditScore, yearlyIncome, totalDebt
        return false;
    }
    
    /**
     * Validates personal client tax ID.
     */
    public static boolean isValidTaxId(String taxId) {
        // TODO: Implement (SSN format validation)
        return false;
    }
    
    /**
     * Validates credit score range.
     */
    public static boolean isValidCreditScore(int creditScore) {
        // TODO: Implement (300-850 range)
        return false;
    }
    
    /**
     * Validates yearly income.
     */
    public static boolean isValidYearlyIncome(double yearlyIncome) {
        // TODO: Implement (use ValidationUtils.isPositive)
        return false;
    }
    
    /**
     * Validates total debt.
     */
    public static boolean isValidTotalDebt(double totalDebt) {
        // TODO: Implement (non-negative)
        return false;
    }
}
