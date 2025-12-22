package com.jbank.controller;

import java.util.Optional;

import com.jbank.model.PersonalClient;
import com.jbank.service.PersonalClientService;
import com.jbank.util.ClientInputHelper;
import com.jbank.util.InputHandler;
import com.jbank.validator.ClientValidator;
import com.jbank.validator.PersonalClientValidator;
import com.jbank.validator.ValidationUtils;

/**
 *
 * @author juanf
 */
public class PersonalClientController {

    private final PersonalClientService personalClientService = new PersonalClientService();
    private final AccountController accountController = new AccountController();

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
        boolean running = true;
        while (running) { 
            printLoginMenu();

            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            while (choice < 1 || choice > 3) {
                System.out.println("Invalid choice. Please try again.");
                printLoginMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }

            switch (choice) {
                case 1 -> loginViaClientID();
                case 2 -> loginViaTaxID();
                case 3 -> running = false;
            }
        }
    }

    private void loginViaClientID() {
        Optional<Integer> idOpt = InputHandler.getIntInput("Enter your Client ID (type 0 to cancel): ");
        
        if (idOpt.isEmpty() || idOpt.get() == 0) {
            return;
        }
        
        Optional<PersonalClient> clientOpt = personalClientService.getById(idOpt.get());
        if (clientOpt.isPresent()) {
            System.out.println("\nWelcome, " + clientOpt.get().getName() + "!");
            handleLoggedInClient(clientOpt.get());
        } else {
            System.out.println("Client not found. Please check your Client ID and try again.");
        }
    }

    private void loginViaTaxID() {
        Optional<String> taxIDOpt = readTaxID("Enter your SSN or ITIN (type 'quit' to cancel): ");
        
        if (taxIDOpt.isEmpty()) {
            return;
        }
        
        Optional<PersonalClient> clientOpt = personalClientService.getByTaxID(taxIDOpt.get());
        if (clientOpt.isPresent()) {
            System.out.println("\nWelcome, " + clientOpt.get().getName() + "!");
            handleLoggedInClient(clientOpt.get());
        } else {
            System.out.println("Client not found. Please check your SSN/ITIN and try again.");
        }
    }

    private void handleLoggedInClient(PersonalClient client) {
        boolean inSession = true;
        while (inSession) {
            printClientSessionMenu();

            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            while (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please try again.");
                printClientSessionMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }

            switch (choice) {
                case 1 -> displayClientDetails(client);
                case 2 -> accountController.handleAccountManagement(client);
                case 3 -> updateClientInformation(client);
                case 4 -> deleteAccount(client);
                case 5 -> inSession = false;
            }
        }
    }

    private void saveUpdatedClient(PersonalClient client) {
        PersonalClient updated = personalClientService.update(client.getCustomerID(), client);
        if (updated != null) {
            System.out.println("Account information updated successfully!");
        } else {
            System.out.println("Failed to update account information. Please try again.");
        }
    }

    private void updateClientInformation(PersonalClient client) {
        try {
            System.out.println("\n== Update Client Information ==");
            System.out.println("What would you like to update?");
            System.out.println("1. Address");
            System.out.println("2. Phone Number");
            System.out.println("3. Yearly Income");
            System.out.println("4. Total Debt");
            System.out.println("5. Cancel");
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            while (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please try again.");
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }
            
            switch (choice) {
                case 1 -> {
                    String newAddress = unwrap(ClientInputHelper.readAddress("Enter new address (type 'quit' to cancel): "));
                    client.setAddress(newAddress);
                    saveUpdatedClient(client);
                }
                case 2 -> {
                    String newPhone = unwrap(ClientInputHelper.readPhoneNumber("Enter new phone number (type 'quit' to cancel): "));
                    client.setPhoneNumber(newPhone);
                    saveUpdatedClient(client);
                }
                case 3 -> {
                    double newIncome = unwrap(readYearlyIncome("Enter new yearly income: $ (type 'quit' to cancel): "));
                    client.setYearlyIncome(newIncome);
                    saveUpdatedClient(client);
                }
                case 4 -> {
                    double newDebt = unwrap(readTotalDebt("Enter new total debt: $ (type 'quit' to cancel): "));
                    client.setTotalDebt(newDebt);
                    saveUpdatedClient(client);
                }
                case 5 -> System.out.println("Update cancelled.");
            }
        } catch (CancellationException e) {
            System.out.println("Update cancelled. Returning to menu...");
        }
    }

    private void deleteAccount(PersonalClient client) {
        System.out.println("\n== Delete Account ==");
        System.out.println("WARNING: This action cannot be undone!");
        System.out.println("Are you sure you want to delete your account?");
        
        String confirmation = InputHandler.getStringInput("Type 'yes' to confirm or anything else to cancel: ");
        
        if (confirmation.equalsIgnoreCase("yes")) {
            try {
                boolean deleted = personalClientService.delete(client.getCustomerID());
                if (deleted) {
                    System.out.println("Account deleted successfully. Returning to main menu...");
                    // Exit the session loop by returning from handleLoggedInClient
                } else {
                    System.out.println("Failed to delete account. Please try again or contact support.");
                }
            } catch (com.jbank.util.AccountDeletionException e) {
                System.out.println("\nCannot delete account due to outstanding balances:");
                System.out.println(e.getAccountDetails());
                System.out.println("\nPlease resolve the following before deleting your account:");
                System.out.println("- Withdraw all funds from Checking and Savings accounts");
                System.out.println("- Pay off your Credit Line balance");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void displayClientDetails(PersonalClient client) {
        System.out.println("\n== Your Account Details ==");
        System.out.println("Client ID: " + client.getCustomerID());
        System.out.println("Name: " + client.getName());
        System.out.println("Address: " + client.getAddress());
        System.out.println("Phone: " + client.getPhoneNumber());
        System.out.println("SSN/ITIN: " + client.getTaxID());
        System.out.println("Credit Score: " + client.getCreditScore());
        System.out.println("Yearly Income: " + ValidationUtils.formatCurrency(client.getYearlyIncome()));
        System.out.println("Total Debt: " + ValidationUtils.formatCurrency(client.getTotalDebt()));
        System.out.println();
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
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            try {
                int score = Integer.parseInt(input);
                if (PersonalClientValidator.isValidCreditScore(score)) {
                    return Optional.of(score);
                }
            } catch (NumberFormatException e) {
                // Fall through to error message
            }
            
            System.out.println("Invalid credit score. Please enter a score between 300 and 850.");
        }
    }

    private Optional<Double> readYearlyIncome(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> incomeOpt = ValidationUtils.parseCurrencyString(input);
            
            if (incomeOpt.isEmpty()) {
                System.out.println("Invalid yearly income. Please enter a positive amount.");
                continue;
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
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> debtOpt = ValidationUtils.parseCurrencyString(input);
            
            if (debtOpt.isEmpty()) {
                System.out.println("Invalid total debt. Please enter a non-negative amount.");
                continue;
            }
            
            double debt = debtOpt.get();
            if (PersonalClientValidator.isValidTotalDebt(debt)) {
                return Optional.of(debt);
            }
            
            System.out.println("Invalid total debt. Please enter a non-negative amount.");
        }
    }

    private static void printLoginMenu() {
        System.out.println("\n== Existing Personal Client ==");
        System.out.println("1. Log in via Client ID");
        System.out.println("2. Log in via SSN/ITIN");
        System.out.println("3. Back to Main Menu");
    }

    private static void printClientSessionMenu() {
        System.out.println("\n== Account Menu ==");
        System.out.println("1. View Account Details");
        System.out.println("2. Manage Accounts");
        System.out.println("3. Update Client Information");
        System.out.println("4. Delete Account");
        System.out.println("5. Logout");
    }

}