package com.jbank.validator;
import java.util.Optional;

/**
 * @author juanf
 */
public class ValidationUtils {
    
    private ValidationUtils() {
        // Prevent instantiation
    }
    
    // Validates that a string is non-null and non-empty
    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    // Validates that a number is positive (greater than zero)
    public static boolean isPositive(double value) {
        return value > 0;
    }

    // Validates that a number is non-negative (zero or greater)
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }
    
    // Validates that a percentage is between 0 and 100
    public static boolean isValidPercentage(double value) {
        return value >= 0 && value <= 100;
    }
    
    // Validates that an ID is a positive integer
    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }

    // Validates that a dollar amount is correct (non-negative and has at most two decimal places)
    public static boolean isValidDollarAmount(double amount) {
        return isNonNegative(amount) &&
        amount == Double.parseDouble(String.format("%.2f", amount));
        
    }

    // Parses a currency string with optional commas (e.g., "1,234.56") to a double
    public static Optional<Double> parseCurrencyString(String input) {
        if (!isValidString(input)) {
            return Optional.empty();
        }
        
        try {
            // Remove commas and parse
            String cleaned = input.replaceAll(",", "");
            double amount = Double.parseDouble(cleaned);
            
            // Validate it's a proper dollar amount
            if (isValidDollarAmount(amount)) {
                return Optional.of(amount);
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    // Formats a double as a currency string with commas and 2 decimal places
    public static String formatCurrency(double amount) {
        return String.format("%,.2f", amount);
    }

    // Rounds a double to 2 decimal places to prevent floating-point precision issues
    public static double roundToTwoDecimals(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    // Checks if balance is effectively zero (within floating-point tolerance)
    public static boolean isEffectivelyZero(double balance) {
        return Math.abs(balance) < 0.005; // Less than half a cent
    }
}
