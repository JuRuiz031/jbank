package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for BusinessClient
 */
public class BusinessClientTest {
    
    private BusinessClient testClient;

    @BeforeEach
    public void setUp() {
        testClient = new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
            "123456789", "LLC", "John Smith", "CEO", 
            500000, 1000000, 150000);
    }

    // EIN validation
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "12345", "1234567890"})
    public void testInvalidEIN(String ein) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                ein, "LLC", "John Smith", "CEO",
                500000, 1000000, 150000)
        );
        assertEquals("EIN must contain exactly 9 digits.", exception.getMessage());
    }

    // Business type validation
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"InvalidType", "Inc", "Nonprofit"})
    public void testInvalidBusinessType(String businessType) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", businessType, "John Smith", "CEO", 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid business type.", exception.getMessage());
    }

    // Contact name validation
    @ParameterizedTest
    @ValueSource(strings = {"Jo", "AB", "a"})
    public void testInvalidContactNameTooShort(String contactName) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", contactName, "CEO", 
                500000, 1000000, 150000)
        );
        assertEquals("Contact name must be between 3 and 50 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidContactNameTooLong() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "a".repeat(51), "CEO", 
                500000, 1000000, 150000)
        );
        assertEquals("Contact name must be between 3 and 50 characters.", exception.getMessage());
    }

    // Contact title validation
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"InvalidTitle", "President", "Founder"})
    public void testInvalidContactTitle(String contactTitle) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", contactTitle, 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid contact title.", exception.getMessage());
    }

    // Financial values validation
    @ParameterizedTest
    @CsvSource({
        "-500000, 1000000",  // Negative asset
        "500000, -1000000"   // Negative revenue
    })
    public void testInvalidFinancialValues(double assets, double revenue) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "CEO",
                assets, revenue, 0)
        );
        assertEquals("Financial values cannot be negative.", exception.getMessage());
    }

    // Negative profit is allowed (business can have losses)
    @Test
    public void testNegativeProfitIsValid() {
        BusinessClient client = new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
            "123456789", "LLC", "John Smith", "CEO",
            500000, 1000000, -150000);
        assertEquals(-150000, client.getAnnualProfit(), 0.01);
    }

    // Business logic - Annual profit margin
    @Test
    public void testAnnualProfitMargin() {
        assertEquals(15.0, testClient.getAnnualProfitMargin(), 0.01);
    }

    @Test
    public void testAnnualProfitMarginWithZeroRevenue() {
        BusinessClient client = new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
            "123456789", "LLC", "John Smith", "CEO", 
            500000, 0, 0);
        assertEquals(0.0, client.getAnnualProfitMargin(), 0.01);
    }

    // Business logic - Return on assets
    @Test
    public void testReturnOnAssets() {
        assertEquals(30.0, testClient.getReturnOnAssets(), 0.01);
    }

    @Test
    public void testReturnOnAssetsWithZeroAssets() {
        BusinessClient client = new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
            "123456789", "LLC", "John Smith", "CEO", 
            0, 1000000, 150000);
        assertEquals(0.0, client.getReturnOnAssets(), 0.01);
    }
}
