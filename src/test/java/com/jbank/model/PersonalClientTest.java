package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PersonalClient
 */

public class PersonalClientTest {
    
    private PersonalClient testClient;

    @BeforeEach
    public void setUp() {
        testClient = new PersonalClient(1, "John", "123 Main", "1234567890", 
            "123456789", 700, 50000, 15000);
    }

    // Tax ID validation
    @Test
    public void testInvalidTaxIDNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", null, 700, 50000, 15000)
        );
        assertEquals("Tax ID must be exactly 9 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidTaxIDTooShort() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "12345", 700, 50000, 15000)
        );
        assertEquals("Tax ID must be exactly 9 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidTaxIDTooLong() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "1234567890", 700, 50000, 15000)
        );
        assertEquals("Tax ID must be exactly 9 characters.", exception.getMessage());
    }

    // Credit score validation
    @Test
    public void testInvalidCreditScoreTooLow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "123456789", 299, 50000, 15000)
        );
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
    }

    @Test
    public void testInvalidCreditScoreTooHigh() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "123456789", 851, 50000, 15000)
        );
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
    }

    // Yearly income validation
    @Test
    public void testInvalidYearlyIncomeNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "123456789", 700, -1000, 15000)
        );
        assertEquals("Yearly income cannot be negative.", exception.getMessage());
    }

    // Total debt validation
    @Test
    public void testInvalidTotalDebtNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "123456789", 700, 50000, -5000)
        );
        assertEquals("Total debt cannot be negative.", exception.getMessage());
    }

    // Credit score mutator validation
    @Test
    public void testUpdateCreditScoreInvalidTooLow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testClient.setCreditScore(250)
        );
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
    }

    @Test
    public void testUpdateCreditScoreInvalidTooHigh() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testClient.setCreditScore(900)
        );
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
    }

    // Yearly income mutator validation
    @Test
    public void testUpdateYearlyIncomeInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testClient.setYearlyIncome(-10000)
        );
        assertEquals("Yearly income cannot be negative.", exception.getMessage());
    }

    // Total debt mutator validation
    @Test
    public void testUpdateTotalDebtInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testClient.setTotalDebt(-5000)
        );
        assertEquals("Total debt cannot be negative.", exception.getMessage());
    }

    // Business logic - Debt to income ratio
    @Test
    public void testDebtToIncomeRatio() {
        assertEquals(30.0, testClient.getDebtToIncomeRatio(), 0.01);
    }

    @Test
    public void testDebtToIncomeRatioWithZeroIncome() {
        PersonalClient client = new PersonalClient(1, "John", "123 Main", "1234567890", 
            "123456789", 700, 0, 15000);
        assertEquals(0.0, client.getDebtToIncomeRatio(), 0.01);
    }
}
