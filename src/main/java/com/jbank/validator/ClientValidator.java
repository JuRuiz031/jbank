package com.jbank.validator;

/**
 * @author juanf
 */
public class ClientValidator {
    
    private ClientValidator() {
        // Prevent instantiation
    }

    // Sanitizes string by removing non-digit characters
    private static String stripNonDigits(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    // Checks if string contains only digits and allowed separators
    private static boolean containsOnlyDigitsAndSeparators(String value, String allowedChars) {
        String pattern = "[0-9" + allowedChars + "]*";
        return value.matches(pattern);
    }
    
    // Validates client name (3-50 characters, letters/spaces/hyphens/apostrophes)
    public static boolean isValidName(String name) {
        return ValidationUtils.isValidString(name) &&
               name.matches("^[a-zA-Z .'-]+$") &&
               name.length() >= 3 &&
               name.length() <= 50;
    }
    
    // Validates client address (max 200 characters, alphanumeric and basic symbols)
    public static boolean isValidAddress(String address) {
        return ValidationUtils.isValidString(address) &&
               address.matches("^[a-zA-Z0-9 ,.'\\-#]+$") &&
               address.length() <= 200;
    }
    
    // Validates client phone number (must be exactly 10 digits)
    public static boolean isValidPhone(String phone) {
        if (!ValidationUtils.isValidString(phone) || !containsOnlyDigitsAndSeparators(phone, "\\.\\-\\() ")) {
            return false;
        }
        String digits = stripNonDigits(phone);
        return digits.length() == 10;
    }
    
    // Validates tax ID - 9 digits with optional hyphens/spaces (SSN, ITIN, or EIN)
    public static boolean isValidTaxID(String taxID) {
        if (!ValidationUtils.isValidString(taxID) || !containsOnlyDigitsAndSeparators(taxID, "\\- ")) {
            return false;
        }
        String digits = stripNonDigits(taxID);
        return digits.length() == 9;
    }
}
