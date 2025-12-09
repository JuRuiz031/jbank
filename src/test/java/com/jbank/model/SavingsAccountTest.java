package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for SavingsAccount
 */
public class SavingsAccountTest {
    
    private SavingsAccount testAccount;

    @BeforeEach
    public void setUp() {
        testAccount = new SavingsAccount(1, 1, 5000.0, "Savings", 1.5, 10);
    }

    // Interest rate validation
    @Test
    public void testUpdateInterestRateValid() {
        testAccount.updateInterestRate(2.5);
        assertEquals(2.5, testAccount.getInterestRate(), 0.01);
    }

    @Test
    public void testUpdateInterestRateInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.updateInterestRate(-1)
        );
        assertEquals("Interest rate cannot be negative.", exception.getMessage());
    }

    // Deposit business logic
    @Test
    public void testDepositValid() {
        testAccount.deposit(1000);
        assertEquals(6000, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testDepositInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.deposit(-500)
        );
        assertEquals("Deposit amount must be positive.", exception.getMessage());
    }

    @Test
    public void testDepositInvalidZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.deposit(0)
        );
        assertEquals("Deposit amount must be positive.", exception.getMessage());
    }

    // Withdrawal business logic
    @Test
    public void testWithdrawalValid() {
        testAccount.withdraw(1000);
        assertEquals(4000, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawalInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(-200)
        );
        assertEquals("Withdrawal amount must be positive.", exception.getMessage());
    }

    @Test
    public void testWithdrawalInvalidZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(0)
        );
        assertEquals("Withdrawal amount must be positive.", exception.getMessage());
    }

    @Test
    public void testWithdrawalExceedsBalance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(10000)
        );
        assertEquals("Insufficient funds for withdrawal.", exception.getMessage());
    }

    // Apply interest business logic
    @Test
    public void testApplyInterestValid() {
        testAccount.updateInterestRate(1.0);
        testAccount.applyInterest();
        assertEquals(5050, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testApplyInterestWithZeroRate() {
        testAccount.updateInterestRate(0);
        testAccount.applyInterest();
        assertEquals(5000, testAccount.getBalance(), 0.01);
    }
}
