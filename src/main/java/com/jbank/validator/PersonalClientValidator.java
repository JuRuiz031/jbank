package com.jbank.validator;

import com.jbank.model.PersonalClient;

public class PersonalClientValidator {
    
    private PersonalClientValidator() {
        // Prevent instantiation
    }
    
    // Validates a PersonalClient model with all required fields
    public static boolean validate(PersonalClient model) {
        return ClientValidator.isValidName(model.getName()) &&
               ClientValidator.isValidAddress(model.getAddress()) &&
               ClientValidator.isValidPhone(model.getPhoneNumber()) &&
               ClientValidator.isValidTaxID(model.getTaxID()) &&
               isValidCreditScore(model.getCreditScore()) &&
               isValidYearlyIncome(model.getYearlyIncome()) &&
               isValidTotalDebt(model.getTotalDebt());
    }

    // Validates credit score (between 300 and 850)
    public static boolean isValidCreditScore(int creditScore) {
        return creditScore >= 300 && creditScore <= 850;
    }

    // Validates yearly income (must be positive dollar amount)
    public static boolean isValidYearlyIncome(double yearlyIncome) {
        return ValidationUtils.isPositive(yearlyIncome) && ValidationUtils.isValidDollarAmount(yearlyIncome);
    }

    // Validates total debt (non-negative dollar amount)
    public static boolean isValidTotalDebt(double totalDebt) {
        return ValidationUtils.isNonNegative(totalDebt) && ValidationUtils.isValidDollarAmount(totalDebt);
    }
}
