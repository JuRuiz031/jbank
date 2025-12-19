package com.jbank.validator;

import com.jbank.model.PersonalClient;

// Validator for PersonalClient-specific fields.
public class PersonalClientValidator {
    
    private PersonalClientValidator() {
        // Prevent instantiation
    }
    
    // Validates a PersonalClient Model
    public static boolean validate(PersonalClient model) {
        return ClientValidator.isValidName(model.getName()) &&
               ClientValidator.isValidAddress(model.getAddress()) &&
               ClientValidator.isValidPhone(model.getPhoneNumber()) &&
               ClientValidator.isValidTaxID(model.getTaxID()) &&
               isValidCreditScore(model.getCreditScore()) &&
               isValidYearlyIncome(model.getYearlyIncome()) &&
               isValidTotalDebt(model.getTotalDebt());
    }

    // Validates credit score (300-850)
    public static boolean isValidCreditScore(int creditScore) {
        return creditScore >= 300 && creditScore <= 850;
    }

    // Validates yearly income (must be positive)
    public static boolean isValidYearlyIncome(double yearlyIncome) {
        return ValidationUtils.isPositive(yearlyIncome) && ValidationUtils.isValidDollarAmount(yearlyIncome);
    }

    // Validates total debt (non-negative)
    public static boolean isValidTotalDebt(double totalDebt) {
        return ValidationUtils.isNonNegative(totalDebt) && ValidationUtils.isValidDollarAmount(totalDebt);
    }
}
