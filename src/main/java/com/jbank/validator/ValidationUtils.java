package com.jbank.validator;
/**
 *
 * @author juanf
 */

// Utility class for common validation methods.
public class ValidationUtils {
    
    private ValidationUtils() {
        // Prevent instantiation
    }
    
    // Validates that a string is non-null and non-empty.
    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    // Validates that a number is positive (greater than zero).
    public static boolean isPositive(double value) {
        return value > 0;
    }

    // Validates that a number is non-negative (zero or greater).
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }
    
    // Validates that a percentage is between 0 and 100.
    public static boolean isValidPercentage(double value) {
        return value >= 0 && value <= 100;
    }
    
    // Validates that an ID is a positive integer.
    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }
}
