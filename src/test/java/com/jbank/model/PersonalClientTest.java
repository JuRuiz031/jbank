package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "12345", "1234567890"})
    public void testInvalidTaxID(String taxId) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", taxId, 700, 50000, 15000)
        );
        assertEquals("Tax ID must contain exactly 9 digits.", exception.getMessage());
    }

    // Credit score validation
    @ParameterizedTest
    @ValueSource(ints = {299, 200, 0, 851, 900, 1000})
    public void testInvalidCreditScore(int creditScore) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(1, "John", "123 Main", "1234567890", "123456789", creditScore, 50000, 15000)
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
