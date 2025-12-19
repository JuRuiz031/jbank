package com.jbank.controller;

import java.util.Optional;

import com.jbank.model.PersonalClient;
import com.jbank.service.PersonalClientService;
import com.jbank.util.ClientInputHelper;
import com.jbank.util.InputHandler;
import com.jbank.validator.ClientValidator;
import com.jbank.validator.PersonalClientValidator;

/**
 *
 * @author juanf
 */
public class PersonalClientController {

    private final PersonalClientService personalClientService = new PersonalClientService();

    public void createNewClient() {
        try {
            System.out.println("\n Thank you for choosing JBank! Please provide the following information to create your account.\n");
            String name = unwrap(ClientInputHelper.readName("Enter your name (type 'quit' to cancel): "));
            String address = unwrap(ClientInputHelper.readAddress("Enter your home address (type 'quit' to cancel): "));
            String phone = unwrap(ClientInputHelper.readPhoneNumber("Enter your phone number (type 'quit' to cancel): "));
            String taxID = unwrap(readTaxID("Enter your 9-digit SSN or ITIN (type 'quit' to cancel): "));
            int creditScore = unwrap(readCreditScore("Enter your credit score 300-850 (type 'quit' to cancel): "));
            double yearlyIncome = unwrap(readYearlyIncome("Enter your yearly income: $ (type 'quit' to cancel): "));
            double totalDebt = unwrap(readTotalDebt("Enter your total debt: $ (type 'quit' to cancel): "));

            // Create PersonalClient model with validated inputs
            PersonalClient newClient = new PersonalClient(
                0, 
                name, 
                address, 
                phone, 
                taxID, 
                creditScore, 
                yearlyIncome, 
                totalDebt
            );
            
            // Save to database and get generated ID
            Integer newID = personalClientService.create(newClient);
            
            if (newID != null) {
                System.out.println("Client created successfully with ID: " + newID + "! You can now log in with this ID from the main menu.");
            } else {
                System.out.println("Failed to create client. Please try again if desired.");
            }
        } catch (CancellationException e) {
            System.out.println("Operation cancelled. Returning to menu...");
        }
    }

    public void handleExistingClient() {
        // Implement logic for handling existing customer
    }
    
    // Helper to unwrap Optional or throw CancellationException if empty
    private <T> T unwrap(Optional<T> optional) {
        if (optional.isEmpty()) {
            throw new CancellationException();
        }
        return optional.get();
    }
    
    // Custom exception for cancellation
    private static class CancellationException extends RuntimeException {}

    // PersonalClient-specific input helper methods
    private Optional<String> readTaxID(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if (ClientValidator.isValidTaxID(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid tax ID. Please enter a valid 9-digit SSN or ITIN.");
        }
    }

    private Optional<Integer> readCreditScore(String prompt) {
        while (true) {
            Optional<Integer> scoreOpt = InputHandler.getIntInput(prompt);
            
            if (scoreOpt.isEmpty()) {
                return Optional.empty();
            }
            
            int score = scoreOpt.get();
            if (PersonalClientValidator.isValidCreditScore(score)) {
                return Optional.of(score);
            }
            
            System.out.println("Invalid credit score. Please enter a score between 300 and 850.");
        }
    }

    private Optional<Double> readYearlyIncome(String prompt) {
        while (true) {
            Optional<Double> incomeOpt = InputHandler.getDoubleInput(prompt);
            
            if (incomeOpt.isEmpty()) {
                return Optional.empty();
            }
            
            double income = incomeOpt.get();
            if (PersonalClientValidator.isValidYearlyIncome(income)) {
                return Optional.of(income);
            }
            
            System.out.println("Invalid yearly income. Please enter a positive amount.");
        }
    }

    private Optional<Double> readTotalDebt(String prompt) {
        while (true) {
            Optional<Double> debtOpt = InputHandler.getDoubleInput(prompt);
            
            if (debtOpt.isEmpty()) {
                return Optional.empty();
            }
            
            double debt = debtOpt.get();
            if (PersonalClientValidator.isValidTotalDebt(debt)) {
                return Optional.of(debt);
            }
            
            System.out.println("Invalid total debt. Please enter a non-negative amount.");
        }
    }
}