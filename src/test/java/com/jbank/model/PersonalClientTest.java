package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // Constructor validation tests
    @Test
    public void testValidPersonalClient() {
        PersonalClient client = new PersonalClient(
            "John Doe", "123 Main St", "1234567890", 
            "123456789", 700, 50000, 15000
        );
        assertNotNull(client);
        assertEquals("John Doe", client.getName());
        assertEquals("123456789", client.getTaxID());
        assertEquals(700, client.getCreditScore());
        assertEquals(50000, client.getYearlyIncome(), 0.01);
        assertEquals(15000, client.getTotalDebt(), 0.01);
    }

    @Test
    public void testInvalidTaxIDNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                null, 700, 50000, 15000);
        });
        assertEquals("Tax ID must be exactly 9 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidTaxIDTooShort() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                "12345", 700, 50000, 15000);
        });
        assertEquals("Tax ID must be exactly 9 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidTaxIDTooLong() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                "1234567890", 700, 50000, 15000);
        });
        assertEquals("Tax ID must be exactly 9 characters.", exception.getMessage());
    }

    @Test
    public void testInvalidCreditScoreTooLow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                "123456789", 299, 50000, 15000);
        });
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
    }

    @Test
    public void testInvalidCreditScoreTooHigh() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                "123456789", 851, 50000, 15000);
        });
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
    }

    @Test
    public void testValidCreditScoreBoundaryLow() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 300, 50000, 15000);
        assertNotNull(client);
        assertEquals(300, client.getCreditScore());
    }

    @Test
    public void testValidCreditScoreBoundaryHigh() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 850, 50000, 15000);
        assertNotNull(client);
        assertEquals(850, client.getCreditScore());
    }

    @Test
    public void testInvalidYearlyIncomeNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                "123456789", 700, -1000, 15000);
        });
        assertEquals("Yearly income cannot be negative.", exception.getMessage());
    }

    @Test
    public void testValidYearlyIncomeZero() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 0, 15000);
        assertNotNull(client);
        assertEquals(0, client.getYearlyIncome(), 0.01);
    }

    @Test
    public void testInvalidTotalDebtNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            new PersonalClient("John", "123 Main", "1234567890", 
                "123456789", 700, 50000, -5000);
        });
        assertEquals("Total debt cannot be negative.", exception.getMessage());
    }

    // Mutator tests
    @Test
    public void testUpdateCreditScoreValid() {
        testClient.updateCreditScore(750);
        assertEquals(750, testClient.getCreditScore());
    }

    @Test
    public void testUpdateCreditScoreInvalidTooLow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testClient.updateCreditScore(250);
        });
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
        assertEquals(700, testClient.getCreditScore()); // Verify unchanged
    }

    @Test
    public void testUpdateCreditScoreInvalidTooHigh() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testClient.updateCreditScore(900);
        });
        assertEquals("Credit score must be between 300 and 850.", exception.getMessage());
        assertEquals(700, testClient.getCreditScore()); // Verify unchanged
    }

    @Test
    public void testUpdateYearlyIncomeValid() {
        testClient.updateYearlyIncome(60000);
        assertEquals(60000, testClient.getYearlyIncome(), 0.01);
    }

    @Test
    public void testUpdateYearlyIncomeInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testClient.updateYearlyIncome(-10000);
        });
        assertEquals("Yearly income cannot be negative.", exception.getMessage());
        assertEquals(50000, testClient.getYearlyIncome(), 0.01); // Verify unchanged
    }

    @Test
    public void testUpdateTotalDebtValid() {
        testClient.updateTotalDebt(20000);
        assertEquals(20000, testClient.getTotalDebt(), 0.01);
    }

    @Test
    public void testUpdateTotalDebtInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testClient.updateTotalDebt(-5000);
        });
        assertEquals("Total debt cannot be negative.", exception.getMessage());
        assertEquals(15000, testClient.getTotalDebt(), 0.01); // Verify unchanged
    }

    @Test
    public void testValidTotalDebtZero() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 50000, 0);
        assertNotNull(client);
        assertEquals(0, client.getTotalDebt(), 0.01);
    }

    // Calculated method tests
    @Test
    public void testDebtToIncomeRatio() {
        assertEquals(30.0, testClient.getDebtToIncomeRatio(), 0.01);
    }

    @Test
    public void testDebtToIncomeRatioWithZeroIncome() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 0, 15000);
        assertNotNull(client);
        assertEquals(0.0, client.getDebtToIncomeRatio(), 0.01);
    }

    @Test
    public void testDebtToIncomeRatioWithZeroDebt() {
        PersonalClient client = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 50000, 0);
        assertNotNull(client);
        assertEquals(0.0, client.getDebtToIncomeRatio(), 0.01);
    }

    // Inherited method tests (from AbstractClient)
    @Test
    public void testUpdatePhoneNumber() {
        testClient.updatePhoneNumber("9876543210");
        assertEquals("9876543210", testClient.getNumber());
    }

    @Test
    public void testUpdateAddress() {
        testClient.updateAddress("456 Oak Ave");
        assertEquals("456 Oak Ave", testClient.getAddress());
    }

    @Test
    public void testUpdateName() {
        testClient.updateName("Jane Doe");
        assertEquals("Jane Doe", testClient.getName());
    }

    @Test
    public void testCustomerIDAutoGeneration() {
        PersonalClient client1 = new PersonalClient("John", "123 Main", "1234567890", 
            "123456789", 700, 50000, 15000);
        PersonalClient client2 = new PersonalClient("Jane", "456 Oak", "9876543210", 
            "987654321", 750, 60000, 10000);
        assertNotNull(client1);
        assertNotNull(client2);
        assertTrue(client2.getCustomerID() > client1.getCustomerID());
    }
}
