package com.jbank.validator;

/**
 *
 * @author juanf
 */
// Validator for client-specific fields.
public class ClientValidator {
    
    private ClientValidator() {
        // Prevent instantiation
    }

    // Sanitizes string for number validation
    private static String stripNonDigits(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    // Helper method: checks if string contains only digits and allowed separators
    private static boolean containsOnlyDigitsAndSeparators(String value, String allowedChars) {
        String pattern = "[0-9" + allowedChars + "]*";
        return value.matches(pattern);
    }
    
    // Validates client name
    public static boolean isValidName(String name) {
        return ValidationUtils.isValidString(name) &&
               name.matches("^[a-zA-Z .'-]+$") &&
               name.length() >= 3 &&
               name.length() <= 50;
    }
    
    // Validates client address
    public static boolean isValidAddress(String address) {
        return ValidationUtils.isValidString(address) &&
               address.matches("^[a-zA-Z0-9 ,.'\\-#]+$") &&
               address.length() <= 200;
    }
    
    // Validates client phone number
    public static boolean isValidPhone(String phone) {
        if (!ValidationUtils.isValidString(phone) || !containsOnlyDigitsAndSeparators(phone, "\\.\\-\\() ")) {
            return false;
        }
        String digits = stripNonDigits(phone);
        return digits.length() == 10;
    }
    
    // Validates tax ID (SSN, ITIN, or EIN - all 9 digits with optional hyphens/spaces).
    // Accepts formats like: 123456789, 123-45-6789, 123 45 6789, 12-3456789, etc.
    public static boolean isValidTaxID(String taxID) {
        if (!ValidationUtils.isValidString(taxID)) {
            return false;
        }
        // Allow only digits, hyphens, and literal spaces
        if (!containsOnlyDigitsAndSeparators(taxID, "\\- ")) {
            return false;
        }
        // Strip to just digits and verify length == 9
        String digitsOnly = stripNonDigits(taxID);
        return digitsOnly.length() == 9;
    }
}
