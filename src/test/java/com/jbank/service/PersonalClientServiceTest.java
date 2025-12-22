package com.jbank.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jbank.model.PersonalClient;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.DAO.PersonalClientDAO;
import com.jbank.repository.entities.PersonalClientEntity;

/**
 * Unit tests for PersonalClientService - conversion tests, business logic tests
 * 
 * @author juanf
 */
@ExtendWith(MockitoExtension.class)
public class PersonalClientServiceTest {

    @Spy
    @InjectMocks
    private PersonalClientService service;
    @Mock
    private PersonalClientDAO personalClientDAO;
    @Mock
    private ClientAccountDAO clientAccountDAO;
    
    private PersonalClient validClient;
    private PersonalClientEntity validEntity;

    @BeforeEach
    public void setUp() {
        // Valid test data
        validClient = new PersonalClient(
            0,
            "John Doe",
            "123 Main St",
            "5551234567",
            "123456789",
            750,
            75000.00,
            5000.00
        );

        validEntity = new PersonalClientEntity(
            1,
            "5551234567",
            "123 Main St",
            "John Doe",
            "123456789",
            750,
            75000.00,
            5000.00
        );
    }

    // Model to Entity Conversion Tests
    @Test
    public void testConvertModelToEntity_HappyPath() {
        Optional<PersonalClientEntity> result = service.convertModelToEntity(validClient);
        
        assertTrue(result.isPresent());
        PersonalClientEntity entity = result.get();
        assertEquals("John Doe", entity.getName());
        assertEquals("(555) 123-4567", entity.getPhoneNumber());
        assertEquals("123-45-6789", entity.getTaxID());
        assertEquals(750, entity.getCreditScore());
    }

    @Test
    public void testConvertModelToEntity_EdgeCase_MinimumCreditScore() {
        PersonalClient client = new PersonalClient(
            0,
            "Jane Smith",
            "456 Oak Ave",
            "5559876543",
            "987654321",
            300,
            50000.00,
            0.00
        );

        Optional<PersonalClientEntity> result = service.convertModelToEntity(client);
        
        assertTrue(result.isPresent());
        assertEquals(300, result.get().getCreditScore());
    }

    @Test
    public void testConvertModelToEntity_EdgeCase_MaximumCreditScore() {
        PersonalClient client = new PersonalClient(
            0,
            "Jane Smith",
            "456 Oak Ave",
            "5559876543",
            "987654321",
            850,
            100000.00,
            25000.00
        );

        Optional<PersonalClientEntity> result = service.convertModelToEntity(client);
        
        assertTrue(result.isPresent());
        assertEquals(850, result.get().getCreditScore());
    }

    @Test
    public void testConvertModelToEntity_FormatsPhoneAndTaxID() {
        PersonalClient client = new PersonalClient(
            0,
            "Alice Johnson",
            "789 Elm St",
            "2025551234",
            "111223333",
            700,
            80000.00,
            10000.00
        );

        Optional<PersonalClientEntity> result = service.convertModelToEntity(client);
        
        assertTrue(result.isPresent());
        assertEquals("(202) 555-1234", result.get().getPhoneNumber());
        assertEquals("111-22-3333", result.get().getTaxID());
    }

    // ===== DAO interaction tests =====

    @Test
    public void testCreate_DelegatesToDAOWithFormattedValues() throws Exception {
        when(personalClientDAO.create(any())).thenReturn(42);
        ArgumentCaptor<PersonalClientEntity> captor = ArgumentCaptor.forClass(PersonalClientEntity.class);

        Integer id = service.create(validClient);

        assertEquals(42, id);
        verify(personalClientDAO).create(captor.capture());
        PersonalClientEntity entity = captor.getValue();
        assertEquals("(555) 123-4567", entity.getPhoneNumber());
        assertEquals("123-45-6789", entity.getTaxID());
    }

    @Test
    public void testCreate_WhenConversionFails_ReturnsNullAndSkipsDAO() {
        doReturn(Optional.empty()).when(service).convertModelToEntity(validClient);

        Integer id = service.create(validClient);

        assertNull(id);
        verifyNoInteractions(personalClientDAO);
    }

    @Test
    public void testGetById_HappyPath() throws Exception {
        when(personalClientDAO.getByID(1)).thenReturn(Optional.of(validEntity));

        Optional<PersonalClient> result = service.getById(1);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("123456789", result.get().getTaxID());
        verify(personalClientDAO).getByID(1);
    }

    @Test
    public void testGetById_NotFound() throws Exception {
        when(personalClientDAO.getByID(99)).thenReturn(Optional.empty());

        Optional<PersonalClient> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(personalClientDAO).getByID(99);
    }

    @Test
    public void testGetByTaxID_HappyPath() throws Exception {
        when(personalClientDAO.getByTaxID("123456789")).thenReturn(Optional.of(validEntity));

        Optional<PersonalClient> result = service.getByTaxID("123456789");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("123456789", result.get().getTaxID());
        verify(personalClientDAO).getByTaxID("123456789");
    }

    @Test
    public void testGetByTaxID_NotFound() throws Exception {
        when(personalClientDAO.getByTaxID("999999999")).thenReturn(Optional.empty());

        Optional<PersonalClient> result = service.getByTaxID("999999999");

        assertTrue(result.isEmpty());
        verify(personalClientDAO).getByTaxID("999999999");
    }

    @Test
    public void testGetAll_ConvertsEntities() throws Exception {
        when(personalClientDAO.getAll()).thenReturn(List.of(validEntity));

        List<PersonalClient> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(personalClientDAO).getAll();
    }

    @Test
    public void testUpdate_UsesDAOAndReturnsConvertedModel() throws Exception {
        PersonalClientEntity updatedEntity = new PersonalClientEntity(
            1,
            "(555) 999-9999",
            "New Address",
            "John Doe",
            "123-45-6789",
            800,
            80000.0,
            1000.0
        );
        when(personalClientDAO.updateByID(any())).thenReturn(updatedEntity);

        PersonalClient result = service.update(1, validClient);

        assertEquals(800, result.getCreditScore());
        assertEquals("New Address", result.getAddress());

        ArgumentCaptor<PersonalClientEntity> captor = ArgumentCaptor.forClass(PersonalClientEntity.class);
        verify(personalClientDAO).updateByID(captor.capture());
        PersonalClientEntity sent = captor.getValue();
        assertEquals("(555) 123-4567", sent.getPhoneNumber());
        assertEquals("123-45-6789", sent.getTaxID());
    }

    @Test
    public void testDelete_HappyPath_NoAccounts() throws Exception {
        // Client has no accounts
        java.util.Map<Integer, String> emptyAccounts = new java.util.HashMap<>();
        when(clientAccountDAO.getAccountsByClient(1)).thenReturn(emptyAccounts);
        when(personalClientDAO.deleteByID(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(personalClientDAO).deleteByID(1);
    }

    @Test
    public void testDelete_HappyPath_SoleOwnerOfAccount() throws Exception {
        // Client is sole owner of one account
        java.util.Map<Integer, String> accounts = new java.util.HashMap<>();
        accounts.put(101, "PRIMARY");
        when(clientAccountDAO.getAccountsByClient(1)).thenReturn(accounts);
        when(clientAccountDAO.isJointAccount(101)).thenReturn(false);
        when(personalClientDAO.deleteByID(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(personalClientDAO).deleteByID(1);
    }

    @Test
    public void testDelete_HappyPath_JointAccount() throws Exception {
        // Client owns a joint account (should NOT delete the account)
        java.util.Map<Integer, String> accounts = new java.util.HashMap<>();
        accounts.put(102, "JOINT");
        when(clientAccountDAO.getAccountsByClient(1)).thenReturn(accounts);
        when(clientAccountDAO.isJointAccount(102)).thenReturn(true);
        when(personalClientDAO.deleteByID(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(personalClientDAO).deleteByID(1);
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        java.util.Map<Integer, String> emptyAccounts = new java.util.HashMap<>();
        when(clientAccountDAO.getAccountsByClient(99)).thenReturn(emptyAccounts);
        when(personalClientDAO.deleteByID(99)).thenReturn(false);

        boolean result = service.delete(99);

        assertTrue(!result);
        verify(personalClientDAO).deleteByID(99);
    }

    // ===== Entity to Model Conversion Tests =====

    @Test
    public void testConvertEntityToModel_HappyPath() {
        Optional<PersonalClient> result = service.convertEntityToModel(validEntity);
        
        assertTrue(result.isPresent());
        PersonalClient model = result.get();
        assertEquals("John Doe", model.getName());
        assertEquals("123456789", model.getTaxID());
        assertEquals(750, model.getCreditScore());
    }

    @Test
    public void testConvertEntityToModel_EdgeCase_ZeroDebt() {
        PersonalClientEntity entity = new PersonalClientEntity(
            1,
            "5551234567",
            "123 Main St",
            "John Doe",
            "123456789",
            700,
            60000.00,
            0.00
        );

        Optional<PersonalClient> result = service.convertEntityToModel(entity);
        
        assertTrue(result.isPresent());
        assertEquals(0.00, result.get().getTotalDebt());
    }

    @Test
    public void testConvertEntityToModel_NegativeResult_InvalidCreditScore() {
        PersonalClientEntity invalidEntity = new PersonalClientEntity(
            1,
            "5551234567",
            "123 Main St",
            "John Doe",
            "123456789",
            200,
            75000.00,
            5000.00
        );

        Optional<PersonalClient> result = service.convertEntityToModel(invalidEntity);
        
        assertTrue(result.isEmpty());
    }

    @Test
    public void testConvertRoundTrip_ModelToEntityToModel() {
        Optional<PersonalClientEntity> entity = service.convertModelToEntity(validClient);
        assertTrue(entity.isPresent());
        
        Optional<PersonalClient> roundTripModel = service.convertEntityToModel(entity.get());
        
        assertTrue(roundTripModel.isPresent());
        PersonalClient result = roundTripModel.get();
        assertEquals(validClient.getName(), result.getName());
        assertEquals("123-45-6789", result.getTaxID());
        assertEquals(validClient.getCreditScore(), result.getCreditScore());
    }
}
