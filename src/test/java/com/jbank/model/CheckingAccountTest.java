package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


/**
 * Unit tests for CheckingAccount
 */
public class CheckingAccountTest {
    
    private CheckingAccount testAccount;

    @BeforeEach
    public void setUp() {
        testAccount = new CheckingAccount(1, 1, 1000.0, "Checking", 35.0, 0.05);
    }

    // Overdraft fee validation
    @Test
    public void testUpdateOverdraftFeeInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.setOverdraftFee(-10)
        );
        assertEquals("Overdraft fee cannot be negative.", exception.getMessage());
    }

    // Deposit business logic
    @Test
    public void testDepositValid() {
        testAccount.deposit(500);
        assertEquals(1500, testAccount.getBalance(), 0.01);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100, -0.01, -500})
    public void testDepositInvalidNegative(double amount) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.deposit(amount)
        );
        assertEquals("Deposit amount must be positive.", exception.getMessage());
    }

    // Withdrawal business logic
    @Test
    public void testWithdrawalValid() {
        testAccount.withdraw(200);
        assertEquals(800, testAccount.getBalance(), 0.01);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-50, -0.01, -1000})
    public void testWithdrawalInvalidNegative(double amount) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(amount)
        );
        assertEquals("Withdrawal amount must be positive.", exception.getMessage());
    }

    @Test
    public void testWithdrawalExceedsBalance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(2000)
        );
        assertEquals("Withdrawal would exceed overdraft limit.", exception.getMessage());
    }
}
