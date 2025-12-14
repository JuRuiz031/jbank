package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for CreditLine
 */
public class CreditLineTest {
    
    private CreditLine testAccount;

    @BeforeEach
    public void setUp() {
        testAccount = new CreditLine(1, 1, 0, "Credit Line", 5000, 18.0, 2.0);
    }

    // Credit limit validation
    @Test
    public void testUpdateCreditLimitValid() {
        testAccount.setCreditLimit(10000);
        assertEquals(10000, testAccount.getCreditLimit(), 0.01);
    }

    @Test
    public void testUpdateCreditLimitInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.setCreditLimit(-1000)
        );
        assertEquals("Credit limit cannot be negative.", exception.getMessage());
    }

    // Interest rate validation
    @Test
    public void testUpdateInterestRateValid() {
        testAccount.setInterestRate(19.5);
        assertEquals(19.5, testAccount.getInterestRate(), 0.01);
    }

    @Test
    public void testUpdateInterestRateInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.setInterestRate(-5)
        );
        assertEquals("Interest rate cannot be negative.", exception.getMessage());
    }

    // Minimum payment percentage validation
    @Test
    public void testUpdateMinPaymentPercentageValid() {
        testAccount.setMinPaymentPercentage(5.0);
        assertEquals(5.0, testAccount.getMinPaymentPercentage(), 0.01);
    }

    @Test
    public void testUpdateMinPaymentPercentageInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.setMinPaymentPercentage(-1)
        );
        assertEquals("Minimum payment percentage must be between 0 and 100.", exception.getMessage());
    }

    @Test
    public void testUpdateMinPaymentPercentageInvalidTooHigh() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.setMinPaymentPercentage(101)
        );
        assertEquals("Minimum payment percentage must be between 0 and 100.", exception.getMessage());
    }

    // Payment business logic
    @Test
    public void testMakePaymentValid() {
        testAccount.makePayment(500);
        assertEquals(-500, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testMakePaymentInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.makePayment(-100)
        );
        assertEquals("Payment amount cannot be negative.", exception.getMessage());
    }

    @Test
    public void testMakePaymentExceedsCreditLimit() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.makePayment(10000)
        );
        assertEquals("Payment would exceed credit limit. Would you like to pay off the full balance?", exception.getMessage());
    }
}
