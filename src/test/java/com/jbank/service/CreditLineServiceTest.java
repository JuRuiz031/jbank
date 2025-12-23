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

import com.jbank.model.CreditLine;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.DAO.CreditLineDAO;
import com.jbank.repository.entities.CreditLineEntity;

/**
 * Unit tests for CreditLineService - CRUD operations, validation, business logic, rollback behavior
 * 
 * @author juanf
 */
@ExtendWith(MockitoExtension.class)
public class CreditLineServiceTest {

    @Mock
    private CreditLineDAO creditLineDAO;
    
    @Mock
    private ClientAccountDAO clientAccountDAO;
    
    @InjectMocks
    private CreditLineService service;
    
    private CreditLine validCreditLine;
    private CreditLineEntity validEntity;
    private final int testClientId = 50;
    private final int testAccountId = 100;

    @BeforeEach
    public void setUp() {
        // Valid test credit line - use accountID 0 as placeholder before DB assignment
        // Constructor receives 2.0 as percentage (2%), stored internally as 0.02 decimal
        validCreditLine = new CreditLine(
            testClientId,
            0,
            0.0,
            "My Credit Line",
            5000.00,
            15.0,
            2.0
        );

        // Entity stores percentage value (2.0) to match database schema which expects percentage (0-100)
        validEntity = new CreditLineEntity(
            testAccountId,
            testClientId,
            0.0,
            5000.00,
            15.0,
            2.0,
            "My Credit Line"
        );
    }

    // Create Tests
    @Test
    public void testCreate_HappyPath_CreatesAccountAndAssignsToClient() throws Exception {
        when(creditLineDAO.create(any())).thenReturn(testAccountId);
        when(clientAccountDAO.assignAccountToClient(anyInt(), anyInt(), anyString())).thenReturn(true);

        Integer result = service.create(validCreditLine, testClientId);

        assertEquals(testAccountId, result);
        verify(creditLineDAO).create(any(CreditLineEntity.class));
        verify(clientAccountDAO).assignAccountToClient(testClientId, testAccountId, "PRIMARY");
    }

    @Test
    public void testCreate_DAOReturnsNull_ReturnsNullAndDoesntAssign() throws Exception {
        when(creditLineDAO.create(any())).thenReturn(null);

        Integer result = service.create(validCreditLine, testClientId);

        assertNull(result);
        verify(creditLineDAO).create(any(CreditLineEntity.class));
        verify(clientAccountDAO, never()).assignAccountToClient(anyInt(), anyInt(), anyString());
    }

    @Test
    public void testCreate_JunctionAssignmentFails_RollsBackAccountCreation() throws Exception {
        when(creditLineDAO.create(any())).thenReturn(testAccountId);
        when(clientAccountDAO.assignAccountToClient(anyInt(), anyInt(), anyString())).thenReturn(false);

        Integer result = service.create(validCreditLine, testClientId);

        assertNull(result);
        verify(creditLineDAO).create(any(CreditLineEntity.class));
        verify(clientAccountDAO).assignAccountToClient(testClientId, testAccountId, "PRIMARY");
        // Verify rollback - credit line should be deleted
        verify(creditLineDAO).deleteByID(testAccountId);
    }

    @Test
    public void testCreate_InvalidCreditLine_ReturnsNullAndDoesntCreate() throws Exception {
        CreditLine invalidCreditLine = new CreditLine(
            -1, // Invalid client ID
            0,
            0.0,
            "My Credit Line",
            5000.00,
            15.0,
            2.0
        );

        Integer result = service.create(invalidCreditLine, testClientId);

        assertNull(result);
        verify(creditLineDAO, never()).create(any());
        verify(clientAccountDAO, never()).assignAccountToClient(anyInt(), anyInt(), anyString());
    }

    // ===== Read Tests =====

    @Test
    public void testGetById_AccountExists_ReturnsAccount() throws Exception {
        when(creditLineDAO.getByID(testAccountId)).thenReturn(Optional.of(validEntity));

        Optional<CreditLine> result = service.getById(testAccountId);

        assertTrue(result.isPresent());
        CreditLine creditLine = result.get();
        assertEquals(0.0, creditLine.getBalance());
        assertEquals(5000.00, creditLine.getCreditLimit());
        assertEquals(15.0, creditLine.getInterestRate());
    }

    @Test
    public void testGetById_AccountNotFound_ReturnsEmpty() throws Exception {
        when(creditLineDAO.getByID(testAccountId)).thenReturn(Optional.empty());

        Optional<CreditLine> result = service.getById(testAccountId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetAll_ReturnsAllAccounts() throws Exception {
        CreditLineEntity entity2 = new CreditLineEntity(101, 51, 1500.0, 10000.00, 18.0, 0.025, "Other Credit Line");
        when(creditLineDAO.getAll()).thenReturn(java.util.List.of(validEntity, entity2));

        java.util.List<CreditLine> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals(0.0, result.get(0).getBalance());
        assertEquals(1500.0, result.get(1).getBalance());
    }

    // ===== Update Tests =====

    @Test
    public void testUpdate_ValidCreditLine_UpdatesSuccessfully() throws Exception {
        CreditLine updatedCreditLine = new CreditLine(
            testClientId,
            testAccountId,
            2000.0,
            "My Credit Line",
            5000.00,
            15.0,
            2.0
        );
        CreditLineEntity updatedEntity = new CreditLineEntity(
            testAccountId,
            testClientId,
            2000.0,
            5000.00,
            15.0,
            0.02,
            "My Credit Line"
        );
        when(creditLineDAO.updateByID(any())).thenReturn(updatedEntity);

        CreditLine result = service.update(testAccountId, updatedCreditLine);

        assertEquals(2000.0, result.getBalance());
        assertEquals(5000.00, result.getCreditLimit());
        verify(creditLineDAO).updateByID(any(CreditLineEntity.class));
    }

    @Test
    public void testUpdate_InvalidCreditLine_ReturnsNullAndDoesntUpdate() throws Exception {
        CreditLine invalidCreditLine = new CreditLine(
            -1,
            testAccountId,
            2000.0,
            "My Credit Line",
            5000.00,
            15.0,
            2.0
        );

        CreditLine result = service.update(testAccountId, invalidCreditLine);

        assertNull(result);
        verify(creditLineDAO, never()).updateByID(any());
    }

    // ===== Delete Tests =====

    @Test
    public void testDelete_RemovesJunctionThenAccount() throws Exception {
        when(clientAccountDAO.removeAllClientsFromAccount(testAccountId)).thenReturn(true);
        when(creditLineDAO.deleteByID(testAccountId)).thenReturn(true);

        boolean result = service.delete(testAccountId);

        assertTrue(result);
        verify(clientAccountDAO).removeAllClientsFromAccount(testAccountId);
        verify(creditLineDAO).deleteByID(testAccountId);
    }

    @Test
    public void testDelete_DAOFails_ReturnsFalse() throws Exception {
        when(clientAccountDAO.removeAllClientsFromAccount(testAccountId)).thenReturn(true);
        when(creditLineDAO.deleteByID(testAccountId)).thenReturn(false);

        boolean result = service.delete(testAccountId);

        assertFalse(result);
    }

    // ===== Charge/Payment Tests =====

    @Test
    public void testChargeCredit_ValidAmount_UpdatesBalance() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 0.0, "My Credit Line", 5000.00, 15.0, 2.0);
        when(creditLineDAO.updateByID(any())).thenReturn(new CreditLineEntity(testAccountId, testClientId, 1000.0, 5000.00, 15.0, 0.02, "My Credit Line"));

        boolean result = service.chargeCredit(creditLine, 1000.0);

        assertTrue(result);
        verify(creditLineDAO).updateByID(any(CreditLineEntity.class));
    }

    @Test
    public void testChargeCredit_ExceedsCreditLimit_ReturnsFalse() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 0.0, "My Credit Line", 5000.00, 15.0, 2.0);

        boolean result = service.chargeCredit(creditLine, 6000.0);

        assertFalse(result);
        verify(creditLineDAO, never()).updateByID(any());
    }

    @Test
    public void testChargeCredit_InvalidAmount_ReturnsFalse() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 0.0, "My Credit Line", 5000.00, 15.0, 2.0);

        boolean result = service.chargeCredit(creditLine, -1000.0);

        assertFalse(result);
        verify(creditLineDAO, never()).updateByID(any());
    }

    @Test
    public void testMakePayment_ValidAmount_UpdatesBalance() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 2000.0, "My Credit Line", 5000.00, 15.0, 2.0);
        when(creditLineDAO.updateByID(any())).thenReturn(new CreditLineEntity(testAccountId, testClientId, 1000.0, 5000.00, 15.0, 2.0, "My Credit Line"));

        boolean result = service.makePayment(creditLine, 1000.0);

        assertTrue(result);
        verify(creditLineDAO).updateByID(any(CreditLineEntity.class));
    }

    @Test
    public void testMakePayment_InvalidAmount_ReturnsFalse() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 2000.0, "My Credit Line", 5000.00, 15.0, 2.0);

        boolean result = service.makePayment(creditLine, -1000.0);

        assertFalse(result);
        verify(creditLineDAO, never()).updateByID(any());
    }

    @Test
    public void testCalculateMinimumPayment_ReturnsCorrectAmount() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 1000.0, "My Credit Line", 5000.00, 15.0, 2.0);

        // 1000 * (2.0 / 100) = 20
        double minimumPayment = service.calculateMinimumPayment(creditLine);

        assertEquals(20.0, minimumPayment);
    }

    @Test
    public void testIncreaseCreditLimit_IncreasesByTenPercent() throws Exception {
        CreditLine creditLine = new CreditLine(testClientId, testAccountId, 1000.0, "My Credit Line", 5000.00, 15.0, 2.0);
        when(creditLineDAO.updateByID(any())).thenReturn(new CreditLineEntity(testAccountId, testClientId, 1000.0, 5500.00, 15.0, 0.02, "My Credit Line"));

        boolean result = service.increaseCreditLimit(creditLine);

        assertTrue(result);
        assertEquals(5500.00, creditLine.getCreditLimit());
        verify(creditLineDAO).updateByID(any(CreditLineEntity.class));
    }

    // ===== Model to Entity Conversion Tests =====

    @Test
    public void testConvertModelToEntity_HappyPath() {
        Optional<CreditLineEntity> result = service.convertModelToEntity(validCreditLine);

        assertTrue(result.isPresent());
        CreditLineEntity entity = result.get();
        assertEquals(testClientId, entity.getCustomerID());
        assertEquals(0.0, entity.getBalance());
        assertEquals(5000.00, entity.getCreditLimit());
        assertEquals(15.0, entity.getInterestRate());
        assertEquals(2.0, entity.getMinPaymentPercentage()); // Entity stores percentage (2.0)
    }

    @Test
    public void testConvertEntityToModel_HappyPath() {
        Optional<CreditLine> result = service.convertEntityToModel(validEntity);

        assertTrue(result.isPresent());
        CreditLine creditLine = result.get();
        assertEquals(testClientId, creditLine.getCustomerID());
        assertEquals(testAccountId, creditLine.getAccountID());
        assertEquals(0.0, creditLine.getBalance());
        assertEquals(5000.00, creditLine.getCreditLimit());
        assertEquals(15.0, creditLine.getInterestRate());
        assertEquals(2.0, creditLine.getMinPaymentPercentage()); // Model getter returns percentage (2.0)
    }
}
