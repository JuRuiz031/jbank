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

import com.jbank.model.CheckingAccount;
import com.jbank.repository.DAO.CheckingAccountDAO;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.entities.CheckingAccountEntity;

/**
 * Unit tests for CheckingAccountService - CRUD operations, validation, business logic, rollback behavior
 * 
 * @author juanf
 */
@ExtendWith(MockitoExtension.class)
public class CheckingAccountServiceTest {

    @Mock
    private CheckingAccountDAO checkingAccountDAO;
    
    @Mock
    private ClientAccountDAO clientAccountDAO;
    
    @InjectMocks
    private CheckingAccountService service;
    
    private CheckingAccount validAccount;
    private CheckingAccountEntity validEntity;
    private final int testClientId = 50;
    private final int testAccountId = 100;

    @BeforeEach
    public void setUp() {
        // Valid test account - use accountID 0 as placeholder before DB assignment
        validAccount = new CheckingAccount(
            testClientId,
            0,
            500.00,
            "My Checking",
            25.00,
            500.00
        );

        validEntity = new CheckingAccountEntity(
            testAccountId,
            testClientId,
            500.00,
            25.00
        );
    }

    // Create Tests
    @Test
    public void testCreate_HappyPath_CreatesAccountAndAssignsToClient() throws Exception {
        when(checkingAccountDAO.create(any())).thenReturn(testAccountId);
        when(clientAccountDAO.assignAccountToClient(anyInt(), anyInt(), anyString())).thenReturn(true);

        Integer result = service.create(validAccount, testClientId);

        assertEquals(testAccountId, result);
        verify(checkingAccountDAO).create(any(CheckingAccountEntity.class));
        verify(clientAccountDAO).assignAccountToClient(testClientId, testAccountId, "PRIMARY");
    }

    @Test
    public void testCreate_DAOReturnsNull_ReturnsNullAndDoesntAssign() throws Exception {
        when(checkingAccountDAO.create(any())).thenReturn(null);

        Integer result = service.create(validAccount, testClientId);

        assertNull(result);
        verify(checkingAccountDAO).create(any(CheckingAccountEntity.class));
        verify(clientAccountDAO, never()).assignAccountToClient(anyInt(), anyInt(), anyString());
    }

    @Test
    public void testCreate_JunctionAssignmentFails_RollsBackAccountCreation() throws Exception {
        when(checkingAccountDAO.create(any())).thenReturn(testAccountId);
        when(clientAccountDAO.assignAccountToClient(anyInt(), anyInt(), anyString())).thenReturn(false);

        Integer result = service.create(validAccount, testClientId);

        assertNull(result);
        verify(checkingAccountDAO).create(any(CheckingAccountEntity.class));
        verify(clientAccountDAO).assignAccountToClient(testClientId, testAccountId, "PRIMARY");
        // Verify rollback - account should be deleted
        verify(checkingAccountDAO).deleteByID(testAccountId);
    }

    @Test
    public void testCreate_InvalidAccount_ReturnsNullAndDoesntCreate() throws Exception {
        CheckingAccount invalidAccount = new CheckingAccount(
            -1, // Invalid client ID
            0,
            500.00,
            "My Checking",
            25.00,
            500.00
        );

        Integer result = service.create(invalidAccount, testClientId);

        assertNull(result);
        verify(checkingAccountDAO, never()).create(any());
        verify(clientAccountDAO, never()).assignAccountToClient(anyInt(), anyInt(), anyString());
    }

    // Read Tests
    @Test
    public void testGetById_AccountExists_ReturnsAccount() throws Exception {
        when(checkingAccountDAO.getByID(testAccountId)).thenReturn(Optional.of(validEntity));

        Optional<CheckingAccount> result = service.getById(testAccountId);

        assertTrue(result.isPresent());
        CheckingAccount account = result.get();
        assertEquals(500.00, account.getBalance());
        assertEquals(25.00, account.getOverdraftFee());
    }

    @Test
    public void testGetById_AccountNotFound_ReturnsEmpty() throws Exception {
        when(checkingAccountDAO.getByID(testAccountId)).thenReturn(Optional.empty());

        Optional<CheckingAccount> result = service.getById(testAccountId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetAll_ReturnsAllAccounts() throws Exception {
        CheckingAccountEntity entity2 = new CheckingAccountEntity(101, 51, 1000.00, 30.00);
        when(checkingAccountDAO.getAll()).thenReturn(java.util.List.of(validEntity, entity2));

        java.util.List<CheckingAccount> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals(500.00, result.get(0).getBalance());
        assertEquals(1000.00, result.get(1).getBalance());
    }

    // ===== Update Tests =====

    @Test
    public void testUpdate_ValidAccount_UpdatesSuccessfully() throws Exception {
        CheckingAccount updatedAccount = new CheckingAccount(
            testClientId,
            testAccountId,
            750.00,
            "My Checking",
            30.00,
            500.00
        );
        CheckingAccountEntity updatedEntity = new CheckingAccountEntity(
            testAccountId,
            testClientId,
            750.00,
            30.00
        );
        when(checkingAccountDAO.updateByID(any())).thenReturn(updatedEntity);

        CheckingAccount result = service.update(testAccountId, updatedAccount);

        assertEquals(750.00, result.getBalance());
        assertEquals(30.00, result.getOverdraftFee());
        verify(checkingAccountDAO).updateByID(any(CheckingAccountEntity.class));
    }

    @Test
    public void testUpdate_InvalidAccount_ReturnsNullAndDoesntUpdate() throws Exception {
        CheckingAccount invalidAccount = new CheckingAccount(
            -1,
            testAccountId,
            750.00,
            "My Checking",
            25.00,
            500.00
        );

        CheckingAccount result = service.update(testAccountId, invalidAccount);

        assertNull(result);
        verify(checkingAccountDAO, never()).updateByID(any());
    }

    // ===== Delete Tests =====

    @Test
    public void testDelete_RemovesJunctionThenAccount() throws Exception {
        when(clientAccountDAO.removeAllClientsFromAccount(testAccountId)).thenReturn(true);
        when(checkingAccountDAO.deleteByID(testAccountId)).thenReturn(true);

        boolean result = service.delete(testAccountId);

        assertTrue(result);
        verify(clientAccountDAO).removeAllClientsFromAccount(testAccountId);
        verify(checkingAccountDAO).deleteByID(testAccountId);
    }

    @Test
    public void testDelete_DAOFails_ReturnsFalse() throws Exception {
        when(clientAccountDAO.removeAllClientsFromAccount(testAccountId)).thenReturn(true);
        when(checkingAccountDAO.deleteByID(testAccountId)).thenReturn(false);

        boolean result = service.delete(testAccountId);

        assertFalse(result);
    }

    // ===== Deposit/Withdraw Tests =====

    @Test
    public void testDeposit_ValidAmount_UpdatesBalance() throws Exception {
        CheckingAccount account = new CheckingAccount(testClientId, testAccountId, 500.00, "My Checking", 25.00, 500.00);
        when(checkingAccountDAO.updateByID(any())).thenReturn(new CheckingAccountEntity(testAccountId, testClientId, 600.00, 25.00));

        boolean result = service.deposit(account, 100.00);

        assertTrue(result);
        assertEquals(600.00, account.getBalance());
        verify(checkingAccountDAO).updateByID(any(CheckingAccountEntity.class));
    }

    @Test
    public void testDeposit_InvalidAmount_ReturnsFalse() throws Exception {
        CheckingAccount account = new CheckingAccount(testClientId, testAccountId, 500.00, "My Checking", 25.00, 500.00);

        boolean result = service.deposit(account, -100.00);

        assertFalse(result);
        assertEquals(500.00, account.getBalance()); // Balance unchanged
        verify(checkingAccountDAO, never()).updateByID(any());
    }

    @Test
    public void testWithdraw_ValidAmount_UpdatesBalance() throws Exception {
        CheckingAccount account = new CheckingAccount(testClientId, testAccountId, 500.00, "My Checking", 25.00, 500.00);
        when(checkingAccountDAO.updateByID(any())).thenReturn(new CheckingAccountEntity(testAccountId, testClientId, 400.00, 25.00));

        boolean result = service.withdraw(account, 100.00);

        assertTrue(result);
        assertEquals(400.00, account.getBalance());
        verify(checkingAccountDAO).updateByID(any(CheckingAccountEntity.class));
    }

    @Test
    public void testWithdraw_WithOverdraft_AppliesFeeAndUpdates() throws Exception {
        CheckingAccount account = new CheckingAccount(testClientId, testAccountId, 100.00, "My Checking", 25.00, 500.00);
        // Withdraw 200: 100 - 200 = -100, then apply fee: -100 - 25 = -125
        when(checkingAccountDAO.updateByID(any())).thenReturn(new CheckingAccountEntity(testAccountId, testClientId, -125.00, 25.00));

        boolean result = service.withdraw(account, 200.00);

        assertTrue(result);
        assertEquals(-125.00, account.getBalance());
        verify(checkingAccountDAO).updateByID(any(CheckingAccountEntity.class));
    }

    // ===== Model to Entity Conversion Tests =====

    @Test
    public void testConvertModelToEntity_HappyPath() {
        Optional<CheckingAccountEntity> result = service.convertModelToEntity(validAccount);

        assertTrue(result.isPresent());
        CheckingAccountEntity entity = result.get();
        assertEquals(testClientId, entity.getCustomerID());
        assertEquals(500.00, entity.getBalance());
        assertEquals(25.00, entity.getOverdraftFee());
    }

    @Test
    public void testConvertEntityToModel_HappyPath() {
        Optional<CheckingAccount> result = service.convertEntityToModel(validEntity);

        assertTrue(result.isPresent());
        CheckingAccount account = result.get();
        assertEquals(testClientId, account.getCustomerID());
        assertEquals(testAccountId, account.getAccountID());
        assertEquals(500.00, account.getBalance());
        assertEquals(25.00, account.getOverdraftFee());
    }
}
