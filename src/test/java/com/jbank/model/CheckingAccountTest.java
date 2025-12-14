package com.jbank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
    public void testUpdateOverdraftFeeValid() {
        testAccount.setOverdraftFee(35);
        assertEquals(35, testAccount.getOverdraftFee(), 0.01);
    }

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

    @Test
    public void testDepositInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.deposit(-100)
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
        testAccount.withdraw(200);
        assertEquals(800, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawalInvalidNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            testAccount.withdraw(-50)
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
            testAccount.withdraw(2000)
        );
        assertEquals("Withdrawal would exceed overdraft limit.", exception.getMessage());
    }
}
