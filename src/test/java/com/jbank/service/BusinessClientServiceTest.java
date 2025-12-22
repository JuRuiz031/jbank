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

import com.jbank.model.BusinessClient;
import com.jbank.repository.DAO.BusinessClientDAO;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.repository.entities.BusinessClientEntity;

/**
 * Unit tests for BusinessClientService - conversion tests, business logic tests
 * 
 * @author juanf
 */
@ExtendWith(MockitoExtension.class)
public class BusinessClientServiceTest {

    @Spy
    @InjectMocks
    private BusinessClientService service;
    @Mock
    private BusinessClientDAO businessClientDAO;
    @Mock
    private ClientAccountDAO clientAccountDAO;
    
    private BusinessClient validClient;
    private BusinessClientEntity validEntity;

    @BeforeEach
    public void setUp() {
        // Valid test data
        validClient = new BusinessClient(
            0,
            "Acme Corp",
            "456 Business Blvd",
            "5559876543",
            "987654321",
            "LLC",
            "Jane Smith",
            "CEO",
            500000.00,
            1000000.00,
            150000.00
        );

        validEntity = new BusinessClientEntity(
            1,
            "5559876543",
            "456 Business Blvd",
            "Acme Corp",
            "987654321",
            "LLC",
            "Jane Smith",
            "CEO",
            500000.00,
            1000000.00,
            150000.00
        );
    }

    // Model to Entity Conversion Tests
    @Test
    public void testConvertModelToEntity_HappyPath() {
        Optional<BusinessClientEntity> result = service.convertModelToEntity(validClient);
        
        assertTrue(result.isPresent());
        BusinessClientEntity entity = result.get();
        assertEquals("Acme Corp", entity.getName());
        assertEquals("(555) 987-6543", entity.getPhoneNumber());
        assertEquals("98-7654321", entity.getEIN());
        assertEquals("LLC", entity.getBusinessType());
        assertEquals("Jane Smith", entity.getContactPersonName());
        assertEquals("CEO", entity.getContactPersonTitle());
    }

    @Test
    public void testConvertModelToEntity_EdgeCase_ZeroProfit() {
        BusinessClient client = new BusinessClient(
            0,
            "TechStart Inc",
            "789 Startup Ave",
            "2025551234",
            "111223333",
            "Corporation",
            "Bob Johnson",
            "CFO",
            100000.00,
            200000.00,
            0.00
        );

        Optional<BusinessClientEntity> result = service.convertModelToEntity(client);
        
        assertTrue(result.isPresent());
        assertEquals(0.00, result.get().getAnnualProfit());
    }

    @Test
    public void testConvertModelToEntity_FormatsPhoneAndEIN() {
        BusinessClient client = new BusinessClient(
            0,
            "Global Solutions",
            "321 Commerce St",
            "4445556666",
            "123456789",
            "Partnership",
            "Alice Brown",
            "Partner",
            750000.00,
            2000000.00,
            300000.00
        );

        Optional<BusinessClientEntity> result = service.convertModelToEntity(client);
        
        assertTrue(result.isPresent());
        assertEquals("(444) 555-6666", result.get().getPhoneNumber());
        assertEquals("12-3456789", result.get().getEIN());
    }

    // DAO interaction tests
    @Test
    public void testCreate_DelegatesToDAOWithFormattedValues() throws Exception {
        when(businessClientDAO.create(any())).thenReturn(42);
        ArgumentCaptor<BusinessClientEntity> captor = ArgumentCaptor.forClass(BusinessClientEntity.class);

        Integer id = service.create(validClient);

        assertEquals(42, id);
        verify(businessClientDAO).create(captor.capture());
        BusinessClientEntity entity = captor.getValue();
        assertEquals("(555) 987-6543", entity.getPhoneNumber());
        assertEquals("98-7654321", entity.getEIN());
    }

    @Test
    public void testCreate_WhenConversionFails_ReturnsNullAndSkipsDAO() {
        doReturn(Optional.empty()).when(service).convertModelToEntity(validClient);

        Integer id = service.create(validClient);

        assertNull(id);
        verifyNoInteractions(businessClientDAO);
    }

    @Test
    public void testGetById_HappyPath() throws Exception {
        when(businessClientDAO.getByID(1)).thenReturn(Optional.of(validEntity));

        Optional<BusinessClient> result = service.getById(1);

        assertTrue(result.isPresent());
        assertEquals("Acme Corp", result.get().getName());
        assertEquals("987654321", result.get().getEin());
        verify(businessClientDAO).getByID(1);
    }

    @Test
    public void testGetById_NotFound() throws Exception {
        when(businessClientDAO.getByID(99)).thenReturn(Optional.empty());

        Optional<BusinessClient> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(businessClientDAO).getByID(99);
    }

    @Test
    public void testGetByEIN_HappyPath() throws Exception {
        when(businessClientDAO.getByEIN("987654321")).thenReturn(Optional.of(validEntity));

        Optional<BusinessClient> result = service.getByEIN("987654321");

        assertTrue(result.isPresent());
        assertEquals("Acme Corp", result.get().getName());
        assertEquals("987654321", result.get().getEin());
        verify(businessClientDAO).getByEIN("987654321");
    }

    @Test
    public void testGetByEIN_NotFound() throws Exception {
        when(businessClientDAO.getByEIN("000000000")).thenReturn(Optional.empty());

        Optional<BusinessClient> result = service.getByEIN("000000000");

        assertTrue(result.isEmpty());
        verify(businessClientDAO).getByEIN("000000000");
    }

    @Test
    public void testGetAll_ConvertsEntities() throws Exception {
        when(businessClientDAO.getAll()).thenReturn(List.of(validEntity));

        List<BusinessClient> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Acme Corp", result.get(0).getName());
        verify(businessClientDAO).getAll();
    }

    @Test
    public void testUpdate_UsesDAOAndReturnsConvertedModel() throws Exception {
        BusinessClientEntity updatedEntity = new BusinessClientEntity(
            1,
            "(555) 111-2222",
            "New Business Address",
            "Acme Corp",
            "98-7654321",
            "Corporation",
            "Jane Smith",
            "CEO",
            600000.0,
            1200000.0,
            180000.0
        );
        when(businessClientDAO.updateByID(any())).thenReturn(updatedEntity);

        BusinessClient result = service.update(1, validClient);

        assertEquals("Corporation", result.getBusinessType());
        assertEquals("New Business Address", result.getAddress());

        ArgumentCaptor<BusinessClientEntity> captor = ArgumentCaptor.forClass(BusinessClientEntity.class);
        verify(businessClientDAO).updateByID(captor.capture());
        BusinessClientEntity sent = captor.getValue();
        assertEquals("(555) 987-6543", sent.getPhoneNumber());
        assertEquals("98-7654321", sent.getEIN());
    }

    @Test
    public void testDelete_HappyPath_NoAccounts() throws Exception {
        // Client has no accounts
        java.util.Map<Integer, String> emptyAccounts = new java.util.HashMap<>();
        when(clientAccountDAO.getAccountsByClient(1)).thenReturn(emptyAccounts);
        when(businessClientDAO.deleteByID(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(businessClientDAO).deleteByID(1);
    }

    @Test
    public void testDelete_HappyPath_SoleOwnerOfAccount() throws Exception {
        // Client is sole owner of one account
        java.util.Map<Integer, String> accounts = new java.util.HashMap<>();
        accounts.put(101, "PRIMARY");
        when(clientAccountDAO.getAccountsByClient(1)).thenReturn(accounts);
        when(clientAccountDAO.isJointAccount(101)).thenReturn(false);
        when(businessClientDAO.deleteByID(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(businessClientDAO).deleteByID(1);
    }

    @Test
    public void testDelete_HappyPath_JointAccount() throws Exception {
        // Client owns a joint account (should NOT delete the account)
        java.util.Map<Integer, String> accounts = new java.util.HashMap<>();
        accounts.put(102, "JOINT");
        when(clientAccountDAO.getAccountsByClient(1)).thenReturn(accounts);
        when(clientAccountDAO.isJointAccount(102)).thenReturn(true);
        when(businessClientDAO.deleteByID(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(businessClientDAO).deleteByID(1);
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        java.util.Map<Integer, String> emptyAccounts = new java.util.HashMap<>();
        when(clientAccountDAO.getAccountsByClient(99)).thenReturn(emptyAccounts);
        when(businessClientDAO.deleteByID(99)).thenReturn(false);

        boolean result = service.delete(99);

        assertTrue(!result);
        verify(businessClientDAO).deleteByID(99);
    }

    // ===== Entity to Model Conversion Tests =====

    @Test
    public void testConvertEntityToModel_HappyPath() {
        Optional<BusinessClient> result = service.convertEntityToModel(validEntity);
        
        assertTrue(result.isPresent());
        BusinessClient model = result.get();
        assertEquals("Acme Corp", model.getName());
        assertEquals("987654321", model.getEin());
        assertEquals("LLC", model.getBusinessType());
        assertEquals("Jane Smith", model.getContactName());
        assertEquals("CEO", model.getContactTitle());
    }

    @Test
    public void testConvertEntityToModel_EdgeCase_NegativeProfit() {
        BusinessClientEntity entity = new BusinessClientEntity(
            1,
            "5551234567",
            "123 Loss St",
            "Struggling Inc",
            "111111111",
            "LLC",
            "John Loss",
            "Owner",
            100000.00,
            50000.00,
            -10000.00
        );

        Optional<BusinessClient> result = service.convertEntityToModel(entity);
        
        assertTrue(result.isPresent());
        assertEquals(-10000.00, result.get().getAnnualProfit());
    }

    @Test
    public void testConvertEntityToModel_NegativeResult_InvalidBusinessType() {
        BusinessClientEntity invalidEntity = new BusinessClientEntity(
            1,
            "5551234567",
            "123 Main St",
            "Invalid Corp",
            "123456789",
            "InvalidType",
            "John Doe",
            "CEO",
            100000.00,
            200000.00,
            50000.00
        );

        Optional<BusinessClient> result = service.convertEntityToModel(invalidEntity);
        
        assertTrue(result.isEmpty());
    }

    @Test
    public void testConvertRoundTrip_ModelToEntityToModel() {
        Optional<BusinessClientEntity> entity = service.convertModelToEntity(validClient);
        assertTrue(entity.isPresent());
        
        Optional<BusinessClient> roundTripModel = service.convertEntityToModel(entity.get());
        
        assertTrue(roundTripModel.isPresent());
        BusinessClient result = roundTripModel.get();
        assertEquals(validClient.getName(), result.getName());
        assertEquals("98-7654321", result.getEin());
        assertEquals(validClient.getBusinessType(), result.getBusinessType());
    }
}
