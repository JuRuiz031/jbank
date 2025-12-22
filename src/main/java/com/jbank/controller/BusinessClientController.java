package com.jbank.controller;

import java.util.Optional;

import com.jbank.model.BusinessClient;
import com.jbank.service.BusinessClientService;
import com.jbank.util.ClientInputHelper;
import com.jbank.util.InputHandler;
import com.jbank.validator.BusinessClientValidator;
import com.jbank.validator.ValidationUtils;

/**
 * Controller for BusinessClient operations.
 * Handles user interaction, input validation, and delegates to service layer.
 * 
 * @author juanf
 */
public class BusinessClientController {

    private final BusinessClientService businessClientService = new BusinessClientService();
    private final AccountController accountController = new AccountController();

    public void createNewClient() {
        try {
            System.out.println("\n Thank you for choosing JBank! Please provide the following information to create your business account.\n");
            String businessName = unwrap(ClientInputHelper.readName("Enter business name (type 'quit' to cancel): "));
            String address = unwrap(ClientInputHelper.readAddress("Enter business address (type 'quit' to cancel): "));
            String phone = unwrap(ClientInputHelper.readPhoneNumber("Enter business phone number (type 'quit' to cancel): "));
            String ein = unwrap(readEIN("Enter your 9-digit EIN (type 'quit' to cancel): "));
            String businessType = unwrap(readBusinessType("Enter business type (type 'quit' to cancel): "));
            String contactName = unwrap(readContactName("Enter contact person name (type 'quit' to cancel): "));
            String contactTitle = unwrap(readContactTitle("Enter contact person title (type 'quit' to cancel): "));
            double totalAssetValue = unwrap(readTotalAssetValue("Enter total asset value: $ (type 'quit' to cancel): "));
            double annualRevenue = unwrap(readAnnualRevenue("Enter annual revenue: $ (type 'quit' to cancel): "));
            double annualProfit = unwrap(readAnnualProfit("Enter annual profit: $ (type 'quit' to cancel): "));

            // Create BusinessClient model with validated inputs
            BusinessClient newClient = new BusinessClient(
                0, 
                businessName, 
                address, 
                phone, 
                ein,
                businessType,
                contactName,
                contactTitle,
                totalAssetValue,
                annualRevenue,
                annualProfit
            );
            
            // Save to database and get generated ID
            Integer newID = businessClientService.create(newClient);
            
            if (newID != null) {
                System.out.println("Business client created successfully with ID: " + newID + "! You can now log in with this ID from the main menu.");
            } else {
                System.out.println("Failed to create business client. Please try again if desired.");
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
                case 2 -> loginViaEIN();
                case 3 -> running = false;
            }
        }
    }

    private void loginViaClientID() {
        Optional<Integer> idOpt = InputHandler.getIntInput("Enter your Client ID (type 0 to cancel): ");
        
        if (idOpt.isEmpty() || idOpt.get() == 0) {
            return;
        }
        
        Optional<BusinessClient> clientOpt = businessClientService.getById(idOpt.get());
        if (clientOpt.isPresent()) {
            System.out.println("\nWelcome, " + clientOpt.get().getName() + "!");
            handleLoggedInClient(clientOpt.get());
        } else {
            System.out.println("Business client not found. Please check your Client ID and try again.");
        }
    }

    private void loginViaEIN() {
        Optional<String> einOpt = readEIN("Enter your EIN (type 'quit' to cancel): ");
        
        if (einOpt.isEmpty()) {
            return;
        }
        
        Optional<BusinessClient> clientOpt = businessClientService.getByEIN(einOpt.get());
        if (clientOpt.isPresent()) {
            System.out.println("\nWelcome, " + clientOpt.get().getName() + "!");
            handleLoggedInClient(clientOpt.get());
        } else {
            System.out.println("Business client not found. Please check your EIN and try again.");
        }
    }

    private void handleLoggedInClient(BusinessClient client) {
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

    private void saveUpdatedClient(BusinessClient client) {
        BusinessClient updated = businessClientService.update(client.getCustomerID(), client);
        if (updated != null) {
            System.out.println("Business account information updated successfully!");
        } else {
            System.out.println("Failed to update business account information. Please try again.");
        }
    }

    private void updateClientInformation(BusinessClient client) {
        try {
            System.out.println("\n== Update Business Client Information ==");
            System.out.println("What would you like to update?");
            System.out.println("1. Business Address");
            System.out.println("2. Phone Number");
            System.out.println("3. Business Type");
            System.out.println("4. Contact Person Name");
            System.out.println("5. Contact Person Title");
            System.out.println("6. Total Asset Value");
            System.out.println("7. Annual Revenue");
            System.out.println("8. Annual Profit");
            System.out.println("9. Cancel");
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            while (choice < 1 || choice > 9) {
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
                    String newBusinessType = unwrap(readBusinessType("Enter new business type (type 'quit' to cancel): "));
                    client.setBusinessType(newBusinessType);
                    saveUpdatedClient(client);
                }
                case 4 -> {
                    String newContactName = unwrap(readContactName("Enter new contact person name (type 'quit' to cancel): "));
                    client.setContactName(newContactName);
                    saveUpdatedClient(client);
                }
                case 5 -> {
                    String newContactTitle = unwrap(readContactTitle("Enter new contact person title (type 'quit' to cancel): "));
                    client.setContactTitle(newContactTitle);
                    saveUpdatedClient(client);
                }
                case 6 -> {
                    double newTotalAssetValue = unwrap(readTotalAssetValue("Enter new total asset value: $ (type 'quit' to cancel): "));
                    client.setTotalAssetValue(newTotalAssetValue);
                    saveUpdatedClient(client);
                }
                case 7 -> {
                    double newAnnualRevenue = unwrap(readAnnualRevenue("Enter new annual revenue: $ (type 'quit' to cancel): "));
                    client.setAnnualRevenue(newAnnualRevenue);
                    saveUpdatedClient(client);
                }
                case 8 -> {
                    double newAnnualProfit = unwrap(readAnnualProfit("Enter new annual profit: $ (type 'quit' to cancel): "));
                    client.setAnnualProfit(newAnnualProfit);
                    saveUpdatedClient(client);
                }
                case 9 -> System.out.println("Update cancelled.");
            }
        } catch (CancellationException e) {
            System.out.println("Update cancelled. Returning to menu...");
        }
    }

    private void deleteAccount(BusinessClient client) {
        System.out.println("\n== Delete Business Account ==");
        System.out.println("WARNING: This action cannot be undone!");
        System.out.println("Are you sure you want to delete your business account?");
        
        String confirmation = InputHandler.getStringInput("Type 'yes' to confirm or anything else to cancel: ");
        
        if (confirmation.equalsIgnoreCase("yes")) {
            try {
                boolean deleted = businessClientService.delete(client.getCustomerID());
                if (deleted) {
                    System.out.println("Business account deleted successfully. Returning to main menu...");
                } else {
                    System.out.println("Failed to delete business account. Please try again or contact support.");
                }
            } catch (com.jbank.util.AccountDeletionException e) {
                System.out.println("\nCannot delete business account due to outstanding balances:");
                System.out.println(e.getAccountDetails());
                System.out.println("\nPlease resolve the following before deleting your business account:");
                System.out.println("- Withdraw all funds from Checking and Savings accounts");
                System.out.println("- Pay off your Credit Line balance");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void displayClientDetails(BusinessClient client) {
        System.out.println("\n== Your Business Account Details ==");
        System.out.println("Client ID: " + client.getCustomerID());
        System.out.println("Business Name: " + client.getName());
        System.out.println("Address: " + client.getAddress());
        System.out.println("Phone: " + client.getPhoneNumber());
        System.out.println("EIN: " + client.getEin());
        System.out.println("Business Type: " + client.getBusinessType());
        System.out.println("Contact Person: " + client.getContactName() + " (" + client.getContactTitle() + ")");
        System.out.println("Total Asset Value: " + ValidationUtils.formatCurrency(client.getTotalAssetValue()));
        System.out.println("Annual Revenue: " + ValidationUtils.formatCurrency(client.getAnnualRevenue()));
        System.out.println("Annual Profit: " + ValidationUtils.formatCurrency(client.getAnnualProfit()));
        System.out.println("Profit Margin: " + String.format("%.2f%%", client.getAnnualProfitMargin()));
        System.out.println("Return on Assets: " + String.format("%.2f%%", client.getReturnOnAssets()));
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

    // BusinessClient-specific input helper methods
    private Optional<String> readEIN(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if (BusinessClientValidator.isValidEIN(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid EIN. Please enter a valid 9-digit EIN.");
        }
    }

    private Optional<String> readBusinessType(String prompt) {
        while (true) {
            System.out.println("Valid business types: " + BusinessClientValidator.getValidBusinessTypes());
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if (BusinessClientValidator.isValidBusinessType(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid business type. Please choose from the list above.");
        }
    }

    private Optional<String> readContactName(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if (BusinessClientValidator.isValidContactName(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid contact name. Must be between 3 and 50 characters.");
        }
    }

    private Optional<String> readContactTitle(String prompt) {
        while (true) {
            System.out.println("Valid titles: " + BusinessClientValidator.getValidContactTitles());
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if (BusinessClientValidator.isValidContactTitle(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid contact title. Please choose from the list above.");
        }
    }

    private Optional<Double> readTotalAssetValue(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> valueOpt = ValidationUtils.parseCurrencyString(input);
            
            if (valueOpt.isEmpty()) {
                System.out.println("Invalid total asset value. Please enter a non-negative amount.");
                continue;
            }
            
            double value = valueOpt.get();
            if (BusinessClientValidator.isValidTotalAssetValue(value)) {
                return Optional.of(value);
            }
            
            System.out.println("Invalid total asset value. Please enter a non-negative amount.");
        }
    }

    private Optional<Double> readAnnualRevenue(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> revenueOpt = ValidationUtils.parseCurrencyString(input);
            
            if (revenueOpt.isEmpty()) {
                System.out.println("Invalid annual revenue. Please enter a non-negative amount.");
                continue;
            }
            
            double revenue = revenueOpt.get();
            if (BusinessClientValidator.isValidAnnualRevenue(revenue)) {
                return Optional.of(revenue);
            }
            
            System.out.println("Invalid annual revenue. Please enter a non-negative amount.");
        }
    }

    private Optional<Double> readAnnualProfit(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> profitOpt = ValidationUtils.parseCurrencyString(input);
            
            if (profitOpt.isEmpty()) {
                System.out.println("Invalid annual profit. Please enter a valid amount (can be negative for losses).");
                continue;
            }
            
            double profit = profitOpt.get();
            if (BusinessClientValidator.isValidAnnualProfit(profit)) {
                return Optional.of(profit);
            }
            
            System.out.println("Invalid annual profit. Please enter a valid amount (can be negative for losses).");
        }
    }

    private static void printLoginMenu() {
        System.out.println("\n== Existing Business Client ==");
        System.out.println("1. Log in via Client ID");
        System.out.println("2. Log in via EIN");
        System.out.println("3. Back to Main Menu");
    }

    private static void printClientSessionMenu() {
        System.out.println("\n== Business Account Menu ==");
        System.out.println("1. View Business Account Details");
        System.out.println("2. Manage Accounts");
        System.out.println("3. Update Business Information");
        System.out.println("4. Delete Business Account");
        System.out.println("5. Logout");
    }
}
