package com.jbank.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jbank.model.SavingsAccount;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.DAO.SavingsAccountDAO;
import com.jbank.repository.entities.SavingsAccountEntity;

/**
 * Unit tests for SavingsAccountService - CRUD operations, validation, business logic, rollback behavior
 * 
 * @author juanf
 */
@ExtendWith(MockitoExtension.class)
public class SavingsAccountServiceTest {

    @Mock
    private SavingsAccountDAO savingsAccountDAO;
    
    @Mock
    private ClientAccountDAO clientAccountDAO;
    
    @InjectMocks
    private SavingsAccountService service;
    
    private SavingsAccount validAccount;
    private SavingsAccountEntity validEntity;
    private final int testClientId = 50;
    private final int testAccountId = 100;

    @BeforeEach
    public void setUp() {
        // Valid test account - use accountID 0 as placeholder before DB assignment
        validAccount = new SavingsAccount(
            testClientId,
            0,
            1000.00,
            "My Savings",
            3.0,
            500
        );

        validEntity = new SavingsAccountEntity(
            testAccountId,
            testClientId,
            1000.00,
            3.0,
            500,
            0,
            "My Savings"
        );
    }

    // Create Tests
    @Test
    public void testCreate_HappyPath_CreatesAccountAndAssignsToClient() throws Exception {
        when(savingsAccountDAO.create(any())).thenReturn(testAccountId);
        when(clientAccountDAO.assignAccountToClient(anyInt(), anyInt(), anyString())).thenReturn(true);

        Integer result = service.create(validAccount, testClientId);

        assertEquals(testAccountId, result);
        verify(savingsAccountDAO).create(any(SavingsAccountEntity.class));
        verify(clientAccountDAO).assignAccountToClient(testClientId, testAccountId, "PRIMARY");
    }

    @Test
    public void testCreate_DAOReturnsNull_ReturnsNullAndDoesntAssign() throws Exception {
        when(savingsAccountDAO.create(any())).thenReturn(null);

        Integer result = service.create(validAccount, testClientId);

        assertNull(result);
        verify(savingsAccountDAO).create(any(SavingsAccountEntity.class));
        verify(clientAccountDAO, never()).assignAccountToClient(anyInt(), anyInt(), anyString());
    }

    @Test
    public void testCreate_JunctionAssignmentFails_RollsBackAccountCreation() throws Exception {
        when(savingsAccountDAO.create(any())).thenReturn(testAccountId);
        when(clientAccountDAO.assignAccountToClient(anyInt(), anyInt(), anyString())).thenReturn(false);

        Integer result = service.create(validAccount, testClientId);

        assertNull(result);
        verify(savingsAccountDAO).create(any(SavingsAccountEntity.class));
        verify(clientAccountDAO).assignAccountToClient(testClientId, testAccountId, "PRIMARY");
        // Verify rollback - account should be deleted
        verify(savingsAccountDAO).deleteByID(testAccountId);
    }

    @Test
    public void testCreate_InvalidAccount_ReturnsNullAndDoesntCreate() throws Exception {
        SavingsAccount invalidAccount = new SavingsAccount(
            -1, // Invalid client ID
            0,
            1000.00,
            "My Savings",
            3.0,
            500
        );

        Integer result = service.create(invalidAccount, testClientId);

        assertNull(result);
        verify(savingsAccountDAO, never()).create(any());
        verify(clientAccountDAO, never()).assignAccountToClient(anyInt(), anyInt(), anyString());
    }

    // ===== Read Tests =====

    @Test
    public void testGetById_AccountExists_ReturnsAccount() throws Exception {
        when(savingsAccountDAO.getByID(testAccountId)).thenReturn(Optional.of(validEntity));

        Optional<SavingsAccount> result = service.getById(testAccountId);

        assertTrue(result.isPresent());
        SavingsAccount account = result.get();
        assertEquals(1000.00, account.getBalance());
        assertEquals(3.0, account.getInterestRate());
    }

    @Test
    public void testGetById_AccountNotFound_ReturnsEmpty() throws Exception {
        when(savingsAccountDAO.getByID(testAccountId)).thenReturn(Optional.empty());

        Optional<SavingsAccount> result = service.getById(testAccountId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetAll_ReturnsAllAccounts() throws Exception {
        SavingsAccountEntity entity2 = new SavingsAccountEntity(101, 51, 5000.00, 4.0, 500, 0, "Test Savings");
        when(savingsAccountDAO.getAll()).thenReturn(java.util.List.of(validEntity, entity2));

        java.util.List<SavingsAccount> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals(1000.00, result.get(0).getBalance());
        assertEquals(5000.00, result.get(1).getBalance());
    }

    // ===== Update Tests =====

    @Test
    public void testUpdate_ValidAccount_UpdatesSuccessfully() throws Exception {
        SavingsAccount updatedAccount = new SavingsAccount(
            testClientId,
            testAccountId,
            1500.00,
            "My Savings",
            4.0,
            500
        );
        SavingsAccountEntity updatedEntity = new SavingsAccountEntity(
            testAccountId,
            testClientId,
            1500.00,
            4.0,
            500,
            0,
            "My Savings"
        );
        when(savingsAccountDAO.updateByID(any())).thenReturn(updatedEntity);

        SavingsAccount result = service.update(testAccountId, updatedAccount);

        assertEquals(1500.00, result.getBalance());
        assertEquals(4.0, result.getInterestRate());
        verify(savingsAccountDAO).updateByID(any(SavingsAccountEntity.class));
    }

    @Test
    public void testUpdate_InvalidAccount_ReturnsNullAndDoesntUpdate() throws Exception {
        SavingsAccount invalidAccount = new SavingsAccount(
            -1,
            testAccountId,
            1500.00,
            "My Savings",
            3.0,
            500
        );

        SavingsAccount result = service.update(testAccountId, invalidAccount);

        assertNull(result);
        verify(savingsAccountDAO, never()).updateByID(any());
    }

    // ===== Delete Tests =====

    @Test
    public void testDelete_RemovesJunctionThenAccount() throws Exception {
        when(clientAccountDAO.removeAllClientsFromAccount(testAccountId)).thenReturn(true);
        when(savingsAccountDAO.deleteByID(testAccountId)).thenReturn(true);

        boolean result = service.delete(testAccountId);

        assertTrue(result);
        verify(clientAccountDAO).removeAllClientsFromAccount(testAccountId);
        verify(savingsAccountDAO).deleteByID(testAccountId);
    }

    @Test
    public void testDelete_DAOFails_ReturnsFalse() throws Exception {
        when(clientAccountDAO.removeAllClientsFromAccount(testAccountId)).thenReturn(true);
        when(savingsAccountDAO.deleteByID(testAccountId)).thenReturn(false);

        boolean result = service.delete(testAccountId);

        assertFalse(result);
    }

    // ===== Deposit/Withdraw Tests =====

    @Test
    public void testDeposit_ValidAmount_UpdatesBalance() throws Exception {
        SavingsAccount account = new SavingsAccount(testClientId, testAccountId, 1000.00, "My Savings", 3.0, 500);
        when(savingsAccountDAO.updateByID(any())).thenReturn(new SavingsAccountEntity(testAccountId, testClientId, 1500.00, 3.0, 500, 0, "My Savings"));

        boolean result = service.deposit(account, 500.00);

        assertTrue(result);
        assertEquals(1500.00, account.getBalance());
        verify(savingsAccountDAO).updateByID(any(SavingsAccountEntity.class));
    }

    @Test
    public void testDeposit_InvalidAmount_ReturnsFalse() throws Exception {
        SavingsAccount account = new SavingsAccount(testClientId, testAccountId, 1000.00, "My Savings", 3.0, 500);

        boolean result = service.deposit(account, -500.00);

        assertFalse(result);
        assertEquals(1000.00, account.getBalance()); // Balance unchanged
        verify(savingsAccountDAO, never()).updateByID(any());
    }

    @Test
    public void testWithdraw_ValidAmount_UpdatesBalance() throws Exception {
        SavingsAccount account = new SavingsAccount(testClientId, testAccountId, 1000.00, "My Savings", 3.0, 500);
        when(savingsAccountDAO.updateByID(any())).thenReturn(new SavingsAccountEntity(testAccountId, testClientId, 500.00, 3.0, 500, 0, "My Savings"));

        boolean result = service.withdraw(account, 500.00);

        assertTrue(result);
        assertEquals(500.00, account.getBalance());
        verify(savingsAccountDAO).updateByID(any(SavingsAccountEntity.class));
    }

    @Test
    public void testWithdraw_InvalidAmount_ReturnsFalse() throws Exception {
        SavingsAccount account = new SavingsAccount(testClientId, testAccountId, 1000.00, "My Savings", 3.0, 500);

        boolean result = service.withdraw(account, -500.00);

        assertFalse(result);
        assertEquals(1000.00, account.getBalance()); // Balance unchanged
        verify(savingsAccountDAO, never()).updateByID(any());
    }

    // ===== Interest Rate Tests =====

    @Test
    public void testApplyInterest_ValidRate_UpdatesBalance() throws Exception {
        SavingsAccount account = new SavingsAccount(testClientId, testAccountId, 1000.00, "My Savings", 3.0, 500);
        // 1000 * (3.0 / 100) = 30
        when(savingsAccountDAO.updateByID(any())).thenReturn(new SavingsAccountEntity(testAccountId, testClientId, 1030.00, 3.0, 500, 0, "My Savings"));

        boolean result = service.applyInterest(account);

        assertTrue(result);
        assertEquals(1030.00, account.getBalance());
        verify(savingsAccountDAO).updateByID(any(SavingsAccountEntity.class));
    }

    @Test
    public void testApplyInterest_ZeroRate_BalanceUnchanged() throws Exception {
        SavingsAccount account = new SavingsAccount(testClientId, testAccountId, 1000.00, "My Savings", 0.0, 500);

        boolean result = service.applyInterest(account);

        assertTrue(result);
        assertEquals(1000.00, account.getBalance());
    }

    // ===== Model to Entity Conversion Tests =====

    @Test
    public void testConvertModelToEntity_HappyPath() {
        Optional<SavingsAccountEntity> result = service.convertModelToEntity(validAccount);

        assertTrue(result.isPresent());
        SavingsAccountEntity entity = result.get();
        assertEquals(testClientId, entity.getCustomerID());
        assertEquals(1000.00, entity.getBalance());
        assertEquals(3.0, entity.getInterestRate());
    }

    @Test
    public void testConvertEntityToModel_HappyPath() {
        Optional<SavingsAccount> result = service.convertEntityToModel(validEntity);

        assertTrue(result.isPresent());
        SavingsAccount account = result.get();
        assertEquals(testClientId, account.getCustomerID());
        assertEquals(testAccountId, account.getAccountID());
        assertEquals(1000.00, account.getBalance());
        assertEquals(3.0, account.getInterestRate());
    }
}
