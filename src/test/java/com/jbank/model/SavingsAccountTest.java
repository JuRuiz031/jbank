package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


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
    public void testUpdateInterestRateInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.setInterestRate(-1)
        );
        assertEquals("Interest rate cannot be negative.", exception.getMessage());
    }

    // Deposit business logic
    @Test
    public void testDepositValid() {
        testAccount.deposit(1000);
        assertEquals(6000, testAccount.getBalance(), 0.01);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-500, -0.01, -1000})
    public void testDepositInvalidNegative(double amount) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.deposit(amount)
        );
        assertEquals("Deposit amount must be positive.", exception.getMessage());
    }

    // Withdrawal business logic
    @Test
    public void testWithdrawalValid() {
        testAccount.withdraw(1000);
        assertEquals(4000, testAccount.getBalance(), 0.01);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-200, -0.01, -5000})
    public void testWithdrawalInvalidNegative(double amount) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(amount)
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
        testAccount.setInterestRate(1.0);
        testAccount.applyInterest();
        assertEquals(5050, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testApplyInterestWithZeroRate() {
        testAccount.setInterestRate(0);
        testAccount.applyInterest();
        assertEquals(5000, testAccount.getBalance(), 0.01);
    }
}
