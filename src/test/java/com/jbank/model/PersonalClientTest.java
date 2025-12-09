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
        testClient = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 50000, 15000);
    }

    // Tax ID validation
    @Test
    public void testInvalidTaxIDNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", null, 700, 50000, 15000)
        );
    }

    @Test
    public void testInvalidTaxIDTooShort() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", "12345", 700, 50000, 15000)
        );
    }

    @Test
    public void testInvalidTaxIDTooLong() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", "1234567890", 700, 50000, 15000)
        );
    }

    // Credit score validation
    @Test
    public void testInvalidCreditScoreTooLow() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", "123456789", 299, 50000, 15000)
        );
    }

    @Test
    public void testInvalidCreditScoreTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", "123456789", 851, 50000, 15000)
        );
    }

    // Yearly income validation
    @Test
    public void testInvalidYearlyIncomeNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", "123456789", 700, -1000, 15000)
        );
    }

    // Total debt validation
    @Test
    public void testInvalidTotalDebtNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient("John", "123 Main", "1234567890", "123456789", 700, 50000, -5000)
        );
    }

    // Credit score mutator validation
    @Test
    public void testUpdateCreditScoreInvalidTooLow() {
        assertThrows(IllegalArgumentException.class, () -> 
            testClient.updateCreditScore(250)
        );
    }

    @Test
    public void testUpdateCreditScoreInvalidTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> 
            testClient.updateCreditScore(900)
        );
    }

    // Yearly income mutator validation
    @Test
    public void testUpdateYearlyIncomeInvalidNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            testClient.updateYearlyIncome(-10000)
        );
    }

    // Total debt mutator validation
    @Test
    public void testUpdateTotalDebtInvalidNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            testClient.updateTotalDebt(-5000)
        );
    }

    // Business logic - Debt to income ratio
    @Test
    public void testDebtToIncomeRatio() {
        assertEquals(30.0, testClient.getDebtToIncomeRatio(), 0.01);
    }

    @Test
    public void testDebtToIncomeRatioWithZeroIncome() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 0, 15000);
        assertEquals(0.0, client.getDebtToIncomeRatio(), 0.01);
    }
}
