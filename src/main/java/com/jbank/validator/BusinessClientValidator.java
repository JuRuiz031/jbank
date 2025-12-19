package com.jbank.validator;

import java.util.Set;

/**
 * Validator for BusinessClient-specific fields.
 * Uses ClientValidator for common fields + validates business-specific data.
 */
public class BusinessClientValidator {
    
    // Valid business types
    private static final Set<String> VALID_BUSINESS_TYPES = Set.of(
        "LLC", "Corporation", "Partnership", "Sole Proprietorship", "Non-Profit"
    );
    
    // Valid titles for contact person
    private static final Set<String> VALID_TITLES = Set.of(
        "CEO", "CFO", "Manager", "Director", "Owner", "Partner"
    );
    
    private BusinessClientValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates EIN (Employer Identification Number).
     * Must be exactly 9 digits (like Tax ID).
     */
    public static boolean isValidEIN(String ein) {
        return ClientValidator.isValidTaxID(ein);
    }
    
    /**
     * Validates business type.
     */
    public static boolean isValidBusinessType(String businessType) {
        return businessType != null && VALID_BUSINESS_TYPES.contains(businessType);
    }
    
    /**
     * Validates contact person name.
     */
    public static boolean isValidContactName(String contactName) {
        return contactName != null && !contactName.trim().isEmpty() 
               && contactName.length() >= 3 && contactName.length() <= 50;
    }
    
    /**
     * Validates contact person title.
     */
    public static boolean isValidContactTitle(String contactTitle) {
        return contactTitle != null && VALID_TITLES.contains(contactTitle);
    }
    
    /**
     * Validates total asset value.
     */
    public static boolean isValidTotalAssetValue(double totalAssetValue) {
        return totalAssetValue >= 0;
    }
    
    /**
     * Validates annual revenue.
     */
    public static boolean isValidAnnualRevenue(double annualRevenue) {
        return annualRevenue >= 0;
    }
    
    /**
     * Validates annual profit (can be negative).
     */
    public static boolean isValidAnnualProfit(double annualProfit) {
        return true; // No restrictions, profit can be negative
    }
    
    /**
     * Get valid business types for display.
     */
    public static Set<String> getValidBusinessTypes() {
        return VALID_BUSINESS_TYPES;
    }
    
    /**
     * Get valid contact titles for display.
     */
    public static Set<String> getValidContactTitles() {
        return VALID_TITLES;
    }
}
