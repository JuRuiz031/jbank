package com.jbank.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jbank.model.PersonalClient;
import com.jbank.repository.entities.PersonalClientEntity;

/**
 * Unit tests for PersonalClientService
 * Conversion tests, business logic tests
 * 
 * @author juanf
 */
public class PersonalClientServiceTest {

    private PersonalClientService service;
    
    private PersonalClient validClient;
    private PersonalClientEntity validEntity;

    @BeforeEach
    public void setUp() {
        service = new PersonalClientService();
        
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

    // ===== Model to Entity Conversion Tests =====

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

    @Test
    public void testConvertModelToEntity_NegativeResult_InvalidTaxID() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            new PersonalClient(0, "John Doe", "123 Main St", "5551234567", "12345", 750, 75000.00, 5000.00)
        );
        assertTrue(ex.getMessage() != null && !ex.getMessage().isEmpty());
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
