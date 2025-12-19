package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BusinessClient 
 * */

public class BusinessClientTest {
    
    private BusinessClient testClient;

    @BeforeEach
    public void setUp() {
        testClient = new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
            "123456789", "LLC", "John Smith", "CEO", "john@techcorp.com", 
            500000, 1000000, 150000);
    }

    // EIN validation
    @Test
    public void testInvalidEINNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                null, "LLC", "John Smith", "CEO", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("EIN must contain exactly 9 digits.", exception.getMessage());
    }

    @Test
    public void testInvalidEINTooShort() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "12345", "LLC", "John Smith", "CEO", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("EIN must contain exactly 9 digits.", exception.getMessage());
    }

    // Business type validation
    @Test
    public void testInvalidBusinessTypeNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", null, "John Smith", "CEO", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid business type.", exception.getMessage());
    }

    @Test
    public void testInvalidBusinessTypeInvalid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "InvalidType", "John Smith", "CEO", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid business type.", exception.getMessage());
    }

    // Contact name validation
    @Test
    public void testInvalidContactNameTooShort() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "Jo", "CEO", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Contact name must be between 3 and 50 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidContactNameTooLong() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "a".repeat(51), "CEO", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Contact name must be between 3 and 50 characters.", exception.getMessage());
    }

    // Contact title validation
    @Test
    public void testInvalidContactTitleNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", null, "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid contact title.", exception.getMessage());
    }

    @Test
    public void testInvalidContactTitleInvalid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "InvalidTitle", "john@techcorp.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid contact title.", exception.getMessage());
    }

    // Contact email validation
    @Test
    public void testInvalidContactEmailNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "CEO", null, 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid contact email.", exception.getMessage());
    }

    @Test
    public void testInvalidContactEmailNoAtSign() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "CEO", "invalidemail.com", 
                500000, 1000000, 150000)
        );
        assertEquals("Invalid contact email.", exception.getMessage());
    }

    // Financial values validation
    @Test
    public void testInvalidFinancialNegativeTotalAsset() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "CEO", "john@techcorp.com", 
                -500000, 1000000, 150000)
        );
        assertEquals("Financial values cannot be negative.", exception.getMessage());
    }

    @Test
    public void testInvalidFinancialNegativeRevenue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "CEO", "john@techcorp.com", 
                500000, -1000000, 150000)
        );
        assertEquals("Financial values cannot be negative.", exception.getMessage());
    }

    @Test
    public void testInvalidFinancialNegativeProfit() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
                "123456789", "LLC", "John Smith", "CEO", "john@techcorp.com", 
                500000, 1000000, -150000)
        );
        assertEquals("Financial values cannot be negative.", exception.getMessage());
    }

    // Business logic - Annual profit margin
    @Test
    public void testAnnualProfitMargin() {
        assertEquals(15.0, testClient.getAnnualProfitMargin(), 0.01);
    }

    @Test
    public void testAnnualProfitMarginWithZeroRevenue() {
        BusinessClient client = new BusinessClient(1, "Tech Corp", "123 Business Ave", "5551234567", 
            "123456789", "LLC", "John Smith", "CEO", "john@techcorp.com", 
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
            "123456789", "LLC", "John Smith", "CEO", "john@techcorp.com", 
            0, 1000000, 150000);
        assertEquals(0.0, client.getReturnOnAssets(), 0.01);
    }
}
