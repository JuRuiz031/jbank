package com.jbank.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jbank.model.AbstractClient;
import com.jbank.model.CheckingAccount;
import com.jbank.model.CreditLine;
import com.jbank.model.SavingsAccount;
import com.jbank.repository.DAO.ClientAccountDAO;
import com.jbank.service.CheckingAccountService;
import com.jbank.service.CreditLineService;
import com.jbank.service.SavingsAccountService;
import com.jbank.util.InputHandler;
import com.jbank.validator.CheckingAccountValidator;
import com.jbank.validator.CreditLineValidator;
import com.jbank.validator.SavingsAccountValidator;
import com.jbank.validator.ValidationUtils;

/**
 * Controller for Account operations.
 * Handles user interaction for account management, delegates to service layer.
 * Supports Checking, Savings, and Credit Line accounts.
 * 
 * @author juanf
 */
public class AccountController {

    private final ClientAccountDAO clientAccountDAO = new ClientAccountDAO();
    private final CheckingAccountService checkingService = new CheckingAccountService();
    private final SavingsAccountService savingsService = new SavingsAccountService();
    private final CreditLineService creditLineService = new CreditLineService();
    private final com.jbank.service.PersonalClientService personalClientService = new com.jbank.service.PersonalClientService();
    private final com.jbank.service.BusinessClientService businessClientService = new com.jbank.service.BusinessClientService();

    // ===== Main Entry Point =====

    public void handleAccountManagement(AbstractClient client) {
        boolean managing = true;
        while (managing) {
            printAccountMenu();

            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            while (choice < 1 || choice > 6) {
                System.out.println("Invalid choice. Please try again.");
                printAccountMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }

            switch (choice) {
                case 1 -> viewAllAccounts(client);
                case 2 -> manageCheckingAccounts(client);
                case 3 -> manageSavingsAccounts(client);
                case 4 -> manageCreditLines(client);
                case 5 -> openNewAccount(client);
                case 6 -> managing = false;
            }
        }
    }

    // ===== View All Accounts =====

    private void viewAllAccounts(AbstractClient client) {
        System.out.println("\n== All Your Accounts ==");
        
        try {
            Map<Integer, String> accountMap = clientAccountDAO.getAccountsByClient(client.getCustomerID());
            
            if (accountMap.isEmpty()) {
                System.out.println("You don't have any accounts yet.");
                System.out.println("Would you like to open one? Select 'Open New Account' from the menu.");
                return;
            }
            
            List<CheckingAccount> checkingAccounts = new ArrayList<>();
            List<SavingsAccount> savingsAccounts = new ArrayList<>();
            List<CreditLine> creditLines = new ArrayList<>();
            
            // Categorize accounts by type
            for (Map.Entry<Integer, String> entry : accountMap.entrySet()) {
                int accountId = entry.getKey();
                
                Optional<CheckingAccount> checking = checkingService.getById(accountId);
                if (checking.isPresent()) {
                    checkingAccounts.add(checking.get());
                    continue;
                }
                
                Optional<SavingsAccount> savings = savingsService.getById(accountId);
                if (savings.isPresent()) {
                    savingsAccounts.add(savings.get());
                    continue;
                }
                
                Optional<CreditLine> credit = creditLineService.getById(accountId);
                if (credit.isPresent()) {
                    creditLines.add(credit.get());
                }
            }
            
            // Display checking accounts
            if (!checkingAccounts.isEmpty()) {
                System.out.println("\n-- Checking Accounts --");
                for (CheckingAccount acc : checkingAccounts) {
                    String ownership = accountMap.get(acc.getAccountID());
                    System.out.printf("  Account #%d [%s]: Balance: %s%n", 
                        acc.getAccountID(), ownership, ValidationUtils.formatCurrency(acc.getBalance()));
                }
            }
            
            // Display savings accounts
            if (!savingsAccounts.isEmpty()) {
                System.out.println("\n-- Savings Accounts --");
                for (SavingsAccount acc : savingsAccounts) {
                    String ownership = accountMap.get(acc.getAccountID());
                    System.out.printf("  Account #%d [%s]: Balance: %s (%.2f%% APY)%n", 
                        acc.getAccountID(), ownership, ValidationUtils.formatCurrency(acc.getBalance()), acc.getInterestRate());
                }
            }
            
            // Display credit lines
            if (!creditLines.isEmpty()) {
                System.out.println("\n-- Credit Lines --");
                for (CreditLine acc : creditLines) {
                    String ownership = accountMap.get(acc.getAccountID());
                    double available = acc.getCreditLimit() - acc.getBalance();
                    System.out.printf("  Account #%d [%s]: Balance: %s | Available: %s | Limit: %s%n", 
                        acc.getAccountID(), ownership, 
                        ValidationUtils.formatCurrency(acc.getBalance()),
                        ValidationUtils.formatCurrency(available),
                        ValidationUtils.formatCurrency(acc.getCreditLimit()));
                }
            }
            
            System.out.println();
            
        } catch (SQLException e) {
            System.out.println("Error retrieving accounts. Please try again.");
        }
    }

    // ===== Checking Account Management =====

    private void manageCheckingAccounts(AbstractClient client) {
        try {
            List<CheckingAccount> accounts = getClientCheckingAccounts(client.getCustomerID());
            
            if (accounts.isEmpty()) {
                System.out.println("\nYou don't have any checking accounts.");
                System.out.println("Would you like to open one?");
                String response = InputHandler.getStringInput("Enter 'yes' to open a new checking account, or anything else to go back: ");
                if (response.equalsIgnoreCase("yes")) {
                    createCheckingAccount(client);
                }
                return;
            }
            
            // Select an account
            System.out.println("\n== Your Checking Accounts ==");
            for (int i = 0; i < accounts.size(); i++) {
                CheckingAccount acc = accounts.get(i);
                System.out.printf("%d. Account #%d - Balance: %s%n", 
                    i + 1, acc.getAccountID(), ValidationUtils.formatCurrency(acc.getBalance()));
            }
            System.out.println((accounts.size() + 1) + ". Back");
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Select an account: ");
            int choice = choiceOpt.orElse(0);
            
            if (choice < 1 || choice > accounts.size() + 1) {
                System.out.println("Invalid choice.");
                return;
            }
            
            if (choice == accounts.size() + 1) {
                return; // Back
            }
            
            CheckingAccount selectedAccount = accounts.get(choice - 1);
            handleCheckingAccountOperations(selectedAccount);
            
        } catch (SQLException e) {
            System.out.println("Error retrieving checking accounts. Please try again.");
        }
    }

    private void handleCheckingAccountOperations(CheckingAccount account) {
        boolean managing = true;
        while (managing) {
            printCheckingOperationsMenu(account);
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            switch (choice) {
                case 1 -> displayCheckingAccountDetails(account);
                case 2 -> depositToChecking(account);
                case 3 -> withdrawFromChecking(account);
                case 4 -> manageCoOwners(account.getAccountID());
                case 5 -> {
                    if (closeCheckingAccount(account)) {
                        managing = false; // Exit loop after account is closed
                    }
                }
                case 6 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayCheckingAccountDetails(CheckingAccount account) {
        System.out.println("\n== Checking Account Details ==");
        System.out.println("Account ID: " + account.getAccountID());
        System.out.println("Account Name: " + account.getAccountName());
        System.out.println("Current Balance: " + ValidationUtils.formatCurrency(account.getBalance()));
        System.out.println("Overdraft Fee: " + ValidationUtils.formatCurrency(account.getOverdraftFee()));
        System.out.println();
    }

    private void depositToChecking(CheckingAccount account) {
        try {
            Optional<Double> amountOpt = readDepositAmount("Enter deposit amount: $");
            if (amountOpt.isEmpty()) {
                System.out.println("Deposit cancelled.");
                return;
            }
            
            double amount = amountOpt.get();
            boolean success = checkingService.deposit(account, amount);
            
            if (success) {
                System.out.println("Successfully deposited " + ValidationUtils.formatCurrency(amount));
                System.out.println("New balance: " + ValidationUtils.formatCurrency(account.getBalance()));
            } else {
                System.out.println("Deposit failed. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Deposit cancelled.");
        }
    }

    private void withdrawFromChecking(CheckingAccount account) {
        try {
            System.out.println("Current balance: " + ValidationUtils.formatCurrency(account.getBalance()));
            Optional<Double> amountOpt = readWithdrawAmount("Enter withdrawal amount: $");
            if (amountOpt.isEmpty()) {
                System.out.println("Withdrawal cancelled.");
                return;
            }
            
            double amount = amountOpt.get();
            boolean success = checkingService.withdraw(account, amount);
            
            if (success) {
                System.out.println("Successfully withdrew " + ValidationUtils.formatCurrency(amount));
                System.out.println("New balance: " + ValidationUtils.formatCurrency(account.getBalance()));
                if (account.getBalance() < 0) {
                    System.out.println("Note: Overdraft fee of " + ValidationUtils.formatCurrency(account.getOverdraftFee()) + " was applied.");
                }
            } else {
                System.out.println("Withdrawal failed. You may have exceeded your overdraft limit.");
            }
        } catch (CancellationException e) {
            System.out.println("Withdrawal cancelled.");
        }
    }

    private boolean closeCheckingAccount(CheckingAccount account) {
        System.out.println("\n== Close Checking Account ==");
        if (!ValidationUtils.isEffectivelyZero(account.getBalance())) {
            System.out.println("WARNING: Your account has a balance of " + ValidationUtils.formatCurrency(account.getBalance()));
            System.out.println("Please withdraw or transfer all funds before closing.");
            return false;
        }
        
        System.out.println("WARNING: This action cannot be undone!");
        String confirmation = InputHandler.getStringInput("Type 'yes' to confirm account closure: ");
        
        if (confirmation.equalsIgnoreCase("yes")) {
            boolean deleted = checkingService.delete(account.getAccountID());
            if (deleted) {
                System.out.println("Checking account closed successfully.");
                return true;
            } else {
                System.out.println("Failed to close account. Please contact support.");
                return false;
            }
        } else {
            System.out.println("Account closure cancelled.");
            return false;
        }
    }

    // ===== Savings Account Management =====

    private void manageSavingsAccounts(AbstractClient client) {
        try {
            List<SavingsAccount> accounts = getClientSavingsAccounts(client.getCustomerID());
            
            if (accounts.isEmpty()) {
                System.out.println("\nYou don't have any savings accounts.");
                System.out.println("Would you like to open one?");
                String response = InputHandler.getStringInput("Enter 'yes' to open a new savings account, or anything else to go back: ");
                if (response.equalsIgnoreCase("yes")) {
                    createSavingsAccount(client);
                }
                return;
            }
            
            // Select an account
            System.out.println("\n== Your Savings Accounts ==");
            for (int i = 0; i < accounts.size(); i++) {
                SavingsAccount acc = accounts.get(i);
                System.out.printf("%d. Account #%d - Balance: %s (%.2f%% APY)%n", 
                    i + 1, acc.getAccountID(), ValidationUtils.formatCurrency(acc.getBalance()), acc.getInterestRate());
            }
            System.out.println((accounts.size() + 1) + ". Back");
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Select an account: ");
            int choice = choiceOpt.orElse(0);
            
            if (choice < 1 || choice > accounts.size() + 1) {
                System.out.println("Invalid choice.");
                return;
            }
            
            if (choice == accounts.size() + 1) {
                return; // Back
            }
            
            SavingsAccount selectedAccount = accounts.get(choice - 1);
            handleSavingsAccountOperations(selectedAccount);
            
        } catch (SQLException e) {
            System.out.println("Error retrieving savings accounts. Please try again.");
        }
    }

    private void handleSavingsAccountOperations(SavingsAccount account) {
        boolean managing = true;
        while (managing) {
            printSavingsOperationsMenu(account);
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            switch (choice) {
                case 1 -> displaySavingsAccountDetails(account);
                case 2 -> depositToSavings(account);
                case 3 -> withdrawFromSavings(account);
                case 4 -> manageCoOwners(account.getAccountID());
                case 5 -> {
                    if (closeSavingsAccount(account)) {
                        managing = false; // Exit loop after account is closed
                    }
                }
                case 6 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displaySavingsAccountDetails(SavingsAccount account) {
        System.out.println("\n== Savings Account Details ==");
        System.out.println("Account ID: " + account.getAccountID());
        System.out.println("Account Name: " + account.getAccountName());
        System.out.println("Current Balance: " + ValidationUtils.formatCurrency(account.getBalance()));
        System.out.println("Interest Rate (APY): " + String.format("%.2f%%", account.getInterestRate()));
        System.out.println("Monthly Withdrawal Limit: " + account.getWithdrawalLimit());
        System.out.println("Withdrawals This Month: " + account.getWithdrawalCounter());
        System.out.println("Remaining Withdrawals: " + (account.getWithdrawalLimit() - account.getWithdrawalCounter()));
        System.out.println();
    }

    private void depositToSavings(SavingsAccount account) {
        try {
            Optional<Double> amountOpt = readDepositAmount("Enter deposit amount: $");
            if (amountOpt.isEmpty()) {
                System.out.println("Deposit cancelled.");
                return;
            }
            
            double amount = amountOpt.get();
            boolean success = savingsService.deposit(account, amount);
            
            if (success) {
                System.out.println("Successfully deposited " + ValidationUtils.formatCurrency(amount));
                System.out.println("New balance: " + ValidationUtils.formatCurrency(account.getBalance()));
            } else {
                System.out.println("Deposit failed. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Deposit cancelled.");
        }
    }

    private void withdrawFromSavings(SavingsAccount account) {
        try {
            int remaining = account.getWithdrawalLimit() - account.getWithdrawalCounter();
            if (remaining <= 0) {
                System.out.println("You have reached your monthly withdrawal limit.");
                System.out.println("Please wait until next month or contact customer service.");
                return;
            }
            
            System.out.println("Current balance: " + ValidationUtils.formatCurrency(account.getBalance()));
            System.out.println("Remaining withdrawals this month: " + remaining);
            
            Optional<Double> amountOpt = readWithdrawAmount("Enter withdrawal amount: $");
            if (amountOpt.isEmpty()) {
                System.out.println("Withdrawal cancelled.");
                return;
            }
            
            double amount = amountOpt.get();
            
            if (amount > account.getBalance()) {
                System.out.println("Insufficient funds. Your balance is " + ValidationUtils.formatCurrency(account.getBalance()));
                return;
            }
            
            boolean success = savingsService.withdraw(account, amount);
            
            if (success) {
                System.out.println("Successfully withdrew " + ValidationUtils.formatCurrency(amount));
                System.out.println("New balance: " + ValidationUtils.formatCurrency(account.getBalance()));
                System.out.println("Remaining withdrawals this month: " + (account.getWithdrawalLimit() - account.getWithdrawalCounter()));
            } else {
                System.out.println("Withdrawal failed. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Withdrawal cancelled.");
        }
    }

    private boolean closeSavingsAccount(SavingsAccount account) {
        System.out.println("\n== Close Savings Account ==");
        if (!ValidationUtils.isEffectivelyZero(account.getBalance())) {
            System.out.println("WARNING: Your account has a balance of " + ValidationUtils.formatCurrency(account.getBalance()));
            System.out.println("Please withdraw or transfer all funds before closing.");
            return false;
        }
        
        System.out.println("WARNING: This action cannot be undone!");
        String confirmation = InputHandler.getStringInput("Type 'yes' to confirm account closure: ");
        
        if (confirmation.equalsIgnoreCase("yes")) {
            boolean deleted = savingsService.delete(account.getAccountID());
            if (deleted) {
                System.out.println("Savings account closed successfully.");
                return true;
            } else {
                System.out.println("Failed to close account. Please contact support.");
                return false;
            }
        } else {
            System.out.println("Account closure cancelled.");
            return false;
        }
    }

    // ===== Credit Line Management =====

    private void manageCreditLines(AbstractClient client) {
        try {
            List<CreditLine> accounts = getClientCreditLines(client.getCustomerID());
            
            if (accounts.isEmpty()) {
                System.out.println("\nYou don't have any credit lines.");
                System.out.println("Would you like to apply for one?");
                String response = InputHandler.getStringInput("Enter 'yes' to apply for a credit line, or anything else to go back: ");
                if (response.equalsIgnoreCase("yes")) {
                    createCreditLine(client);
                }
                return;
            }
            
            // Select an account
            System.out.println("\n== Your Credit Lines ==");
            for (int i = 0; i < accounts.size(); i++) {
                CreditLine acc = accounts.get(i);
                double available = acc.getCreditLimit() - acc.getBalance();
                System.out.printf("%d. Account #%d - Balance: %s | Available: %s%n", 
                    i + 1, acc.getAccountID(), 
                    ValidationUtils.formatCurrency(acc.getBalance()),
                    ValidationUtils.formatCurrency(available));
            }
            System.out.println((accounts.size() + 1) + ". Back");
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Select an account: ");
            int choice = choiceOpt.orElse(0);
            
            if (choice < 1 || choice > accounts.size() + 1) {
                System.out.println("Invalid choice.");
                return;
            }
            
            if (choice == accounts.size() + 1) {
                return; // Back
            }
            
            CreditLine selectedAccount = accounts.get(choice - 1);
            handleCreditLineOperations(selectedAccount);
            
        } catch (SQLException e) {
            System.out.println("Error retrieving credit lines. Please try again.");
        }
    }

    private void handleCreditLineOperations(CreditLine account) {
        boolean managing = true;
        while (managing) {
            printCreditLineOperationsMenu(account);
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            switch (choice) {
                case 1 -> displayCreditLineDetails(account);
                case 2 -> makeCharge(account);
                case 3 -> makePayment(account);
                case 4 -> manageCoOwners(account.getAccountID());
                case 5 -> {
                    if (closeCreditLine(account)) {
                        managing = false; // Exit loop after account is closed
                    }
                }
                case 6 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayCreditLineDetails(CreditLine account) {
        double available = account.getCreditLimit() - account.getBalance();
        double minPayment = creditLineService.calculateMinimumPayment(account);
        
        System.out.println("\n== Credit Line Details ==");
        System.out.println("Account ID: " + account.getAccountID());
        System.out.println("Account Name: " + account.getAccountName());
        System.out.println("Current Balance: " + ValidationUtils.formatCurrency(account.getBalance()));
        System.out.println("Credit Limit: " + ValidationUtils.formatCurrency(account.getCreditLimit()));
        System.out.println("Available Credit: " + ValidationUtils.formatCurrency(available));
        System.out.println("Interest Rate (APR): " + String.format("%.2f%%", account.getInterestRate()));
        System.out.println("Minimum Payment Due: " + ValidationUtils.formatCurrency(minPayment));
        System.out.println();
    }

    private void makeCharge(CreditLine account) {
        try {
            double available = account.getCreditLimit() - account.getBalance();
            System.out.println("Available credit: " + ValidationUtils.formatCurrency(available));
            
            if (available <= 0) {
                System.out.println("No available credit. Please make a payment first.");
                return;
            }
            
            Optional<Double> amountOpt = readChargeAmount("Enter charge amount: $");
            if (amountOpt.isEmpty()) {
                System.out.println("Charge cancelled.");
                return;
            }
            
            double amount = amountOpt.get();
            
            if (amount > available) {
                System.out.println("Charge exceeds available credit of " + ValidationUtils.formatCurrency(available));
                return;
            }
            
            boolean success = creditLineService.chargeCredit(account, amount);
            
            if (success) {
                // Refresh account data
                Optional<CreditLine> refreshed = creditLineService.getById(account.getAccountID());
                if (refreshed.isPresent()) {
                    System.out.println("Successfully charged " + ValidationUtils.formatCurrency(amount));
                    System.out.println("New balance: " + ValidationUtils.formatCurrency(refreshed.get().getBalance()));
                    System.out.println("Available credit: " + ValidationUtils.formatCurrency(refreshed.get().getCreditLimit() - refreshed.get().getBalance()));
                }
            } else {
                System.out.println("Charge failed. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Charge cancelled.");
        }
    }

    private void makePayment(CreditLine account) {
        try {
            if (account.getBalance() <= 0) {
                System.out.println("You have no balance to pay.");
                return;
            }
            
            double minPayment = creditLineService.calculateMinimumPayment(account);
            System.out.println("Current balance: " + ValidationUtils.formatCurrency(account.getBalance()));
            System.out.println("Minimum payment due: " + ValidationUtils.formatCurrency(minPayment));
            
            Optional<Double> amountOpt = readPaymentAmount("Enter payment amount: $");
            if (amountOpt.isEmpty()) {
                System.out.println("Payment cancelled.");
                return;
            }
            
            double amount = amountOpt.get();
            boolean success = creditLineService.makePayment(account, amount);
            
            if (success) {
                // Refresh account data
                Optional<CreditLine> refreshed = creditLineService.getById(account.getAccountID());
                if (refreshed.isPresent()) {
                    System.out.println("Successfully paid " + ValidationUtils.formatCurrency(amount));
                    System.out.println("New balance: " + ValidationUtils.formatCurrency(refreshed.get().getBalance()));
                    System.out.println("Note: Interest may be applied to remaining balance.");
                }
            } else {
                System.out.println("Payment failed. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Payment cancelled.");
        }
    }

    private boolean closeCreditLine(CreditLine account) {
        System.out.println("\n== Close Credit Line ==");
        if (account.getBalance() > 0) {
            System.out.println("WARNING: Your credit line has a balance of " + ValidationUtils.formatCurrency(account.getBalance()));
            System.out.println("Please pay off your balance before closing.");
            return false;
        }
        
        System.out.println("WARNING: This action cannot be undone!");
        String confirmation = InputHandler.getStringInput("Type 'yes' to confirm account closure: ");
        
        if (confirmation.equalsIgnoreCase("yes")) {
            boolean deleted = creditLineService.delete(account.getAccountID());
            if (deleted) {
                System.out.println("Credit line closed successfully.");
                return true;
            } else {
                System.out.println("Failed to close account. Please contact support.");
                return false;
            }
        } else {
            System.out.println("Account closure cancelled.");
            return false;
        }
    }

    // ===== Open New Account =====

    private void openNewAccount(AbstractClient client) {
        try {
            System.out.println("\n== Open New Account ==");
            System.out.println("What type of account would you like to open?");
            System.out.println("1. Checking Account");
            System.out.println("2. Savings Account");
            System.out.println("3. Credit Line");
            System.out.println("4. Cancel");
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            switch (choice) {
                case 1 -> createCheckingAccount(client);
                case 2 -> createSavingsAccount(client);
                case 3 -> createCreditLine(client);
                case 4 -> System.out.println("Account creation cancelled.");
                default -> System.out.println("Invalid choice.");
            }
        } catch (CancellationException e) {
            System.out.println("Account creation cancelled.");
        }
    }

    private void createCheckingAccount(AbstractClient client) {
        try {
            System.out.println("\n== Open Checking Account ==");
            
            String accountName = unwrap(readAccountName("Enter account name (e.g., 'Primary Checking'): "));
            double initialDeposit = unwrap(readInitialDeposit("Enter initial deposit amount: $"));
            double overdraftFee = unwrap(readOverdraftFee("Enter overdraft fee (suggested: $25-35): $"));
            double overdraftLimit = unwrap(readOverdraftLimit("Enter overdraft limit (suggested: $500): $"));
            
            CheckingAccount newAccount = new CheckingAccount(
                client.getCustomerID(),
                0, // Placeholder - DB will assign
                initialDeposit,
                accountName,
                overdraftFee,
                overdraftLimit
            );
            
            Integer accountId = checkingService.create(newAccount, client.getCustomerID());
            
            if (accountId != null) {
                System.out.println("Checking account created successfully!");
                System.out.println("Account ID: " + accountId);
                System.out.println("Initial Balance: " + ValidationUtils.formatCurrency(initialDeposit));
            } else {
                System.out.println("Failed to create checking account. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Account creation cancelled.");
        }
    }

    private void createSavingsAccount(AbstractClient client) {
        try {
            System.out.println("\n== Open Savings Account ==");
            
            String accountName = unwrap(readAccountName("Enter account name (e.g., 'Emergency Fund'): "));
            double initialDeposit = unwrap(readInitialDeposit("Enter initial deposit amount: $"));
            double interestRate = unwrap(readInterestRate("Enter interest rate (APY, e.g., 3.5): "));
            int withdrawalLimit = unwrap(readWithdrawalLimit("Enter monthly withdrawal limit (suggested: 6): "));
            
            SavingsAccount newAccount = new SavingsAccount(
                client.getCustomerID(),
                0, // Placeholder - DB will assign
                initialDeposit,
                accountName,
                interestRate,
                withdrawalLimit
            );
            
            Integer accountId = savingsService.create(newAccount, client.getCustomerID());
            
            if (accountId != null) {
                System.out.println("Savings account created successfully!");
                System.out.println("Account ID: " + accountId);
                System.out.println("Initial Balance: " + ValidationUtils.formatCurrency(initialDeposit));
                System.out.println("Interest Rate: " + String.format("%.2f%%", interestRate) + " APY");
            } else {
                System.out.println("Failed to create savings account. Please try again.");
            }
        } catch (CancellationException e) {
            System.out.println("Account creation cancelled.");
        }
    }

    private void createCreditLine(AbstractClient client) {
        try {
            System.out.println("\n== Apply for Credit Line ==");
            
            String accountName = unwrap(readAccountName("Enter account name (e.g., 'Business Credit'): "));
            double creditLimit = unwrap(readCreditLimit("Enter requested credit limit: $"));
            double interestRate = unwrap(readCreditInterestRate("Enter interest rate (APR, e.g., 15.0): "));
            double minPaymentPercentage = unwrap(readMinPaymentPercentage("Enter minimum payment percentage (e.g., 2.0): "));
            
            CreditLine newAccount = new CreditLine(
                client.getCustomerID(),
                0, // Placeholder - DB will assign
                0.0, // Initial balance is 0
                accountName,
                creditLimit,
                interestRate,
                minPaymentPercentage
            );
            
            Integer accountId = creditLineService.create(newAccount, client.getCustomerID());
            
            if (accountId != null) {
                System.out.println("Credit line approved!");
                System.out.println("Account ID: " + accountId);
                System.out.println("Credit Limit: " + ValidationUtils.formatCurrency(creditLimit));
                System.out.println("Interest Rate: " + String.format("%.2f%%", interestRate) + " APR");
            } else {
                System.out.println("Credit line application denied. Please try again or contact support.");
            }
        } catch (CancellationException e) {
            System.out.println("Application cancelled.");
        }
    }

    // ===== Helper Methods - Get Accounts =====

    private List<CheckingAccount> getClientCheckingAccounts(int clientId) throws SQLException {
        List<CheckingAccount> accounts = new ArrayList<>();
        Map<Integer, String> accountMap = clientAccountDAO.getAccountsByClient(clientId);
        
        for (Integer accountId : accountMap.keySet()) {
            Optional<CheckingAccount> account = checkingService.getById(accountId);
            account.ifPresent(accounts::add);
        }
        return accounts;
    }

    private List<SavingsAccount> getClientSavingsAccounts(int clientId) throws SQLException {
        List<SavingsAccount> accounts = new ArrayList<>();
        Map<Integer, String> accountMap = clientAccountDAO.getAccountsByClient(clientId);
        
        for (Integer accountId : accountMap.keySet()) {
            Optional<SavingsAccount> account = savingsService.getById(accountId);
            account.ifPresent(accounts::add);
        }
        return accounts;
    }

    private List<CreditLine> getClientCreditLines(int clientId) throws SQLException {
        List<CreditLine> accounts = new ArrayList<>();
        Map<Integer, String> accountMap = clientAccountDAO.getAccountsByClient(clientId);
        
        for (Integer accountId : accountMap.keySet()) {
            Optional<CreditLine> account = creditLineService.getById(accountId);
            account.ifPresent(accounts::add);
        }
        return accounts;
    }

    // ===== Helper Methods - Input Reading =====

    private <T> T unwrap(Optional<T> optional) {
        if (optional.isEmpty()) {
            throw new CancellationException();
        }
        return optional.get();
    }

    private static class CancellationException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private Optional<String> readAccountName(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if (input.length() >= 3 && input.length() <= 30) {
                return Optional.of(input);
            }
            
            System.out.println("Account name must be between 3 and 30 characters.");
        }
    }

    private Optional<Double> readInitialDeposit(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> amountOpt = ValidationUtils.parseCurrencyString(input);
            if (amountOpt.isPresent() && amountOpt.get() >= 0) {
                return amountOpt;
            }
            
            System.out.println("Please enter a valid non-negative amount.");
        }
    }

    private Optional<Double> readDepositAmount(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> amountOpt = ValidationUtils.parseCurrencyString(input);
            if (amountOpt.isPresent() && amountOpt.get() > 0) {
                return amountOpt;
            }
            
            System.out.println("Please enter a positive amount.");
        }
    }

    private Optional<Double> readWithdrawAmount(String prompt) {
        return readDepositAmount(prompt); // Same validation
    }

    private Optional<Double> readChargeAmount(String prompt) {
        return readDepositAmount(prompt); // Same validation
    }

    private Optional<Double> readPaymentAmount(String prompt) {
        return readDepositAmount(prompt); // Same validation
    }

    private Optional<Double> readOverdraftFee(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> amountOpt = ValidationUtils.parseCurrencyString(input);
            if (amountOpt.isPresent() && CheckingAccountValidator.isValidOverdraftFee(amountOpt.get())) {
                return amountOpt;
            }
            
            System.out.println("Please enter a valid non-negative fee amount.");
        }
    }

    private Optional<Double> readOverdraftLimit(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> amountOpt = ValidationUtils.parseCurrencyString(input);
            if (amountOpt.isPresent() && amountOpt.get() >= 0) {
                return amountOpt;
            }
            
            System.out.println("Please enter a valid non-negative limit.");
        }
    }

    private Optional<Double> readInterestRate(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            try {
                double rate = Double.parseDouble(input);
                if (SavingsAccountValidator.isValidInterestRate(rate)) {
                    return Optional.of(rate);
                }
            } catch (NumberFormatException e) {
                // Fall through to error
            }
            
            System.out.println("Please enter a valid interest rate (0-100).");
        }
    }

    private Optional<Integer> readWithdrawalLimit(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            try {
                int limit = Integer.parseInt(input);
                if (SavingsAccountValidator.isValidWithdrawalLimit(limit)) {
                    return Optional.of(limit);
                }
            } catch (NumberFormatException e) {
                // Fall through to error
            }
            
            System.out.println("Please enter a positive whole number.");
        }
    }

    private Optional<Double> readCreditLimit(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            Optional<Double> amountOpt = ValidationUtils.parseCurrencyString(input);
            if (amountOpt.isPresent() && CreditLineValidator.isValidCreditLimit(amountOpt.get())) {
                return amountOpt;
            }
            
            System.out.println("Please enter a valid positive credit limit.");
        }
    }

    private Optional<Double> readCreditInterestRate(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            try {
                double rate = Double.parseDouble(input);
                if (CreditLineValidator.isValidInterestRate(rate)) {
                    return Optional.of(rate);
                }
            } catch (NumberFormatException e) {
                // Fall through to error
            }
            
            System.out.println("Please enter a valid interest rate (0-100).");
        }
    }

    private Optional<Double> readMinPaymentPercentage(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if (input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            try {
                double percentage = Double.parseDouble(input);
                if (CreditLineValidator.isValidMinPaymentPercentage(percentage)) {
                    return Optional.of(percentage);
                }
            } catch (NumberFormatException e) {
                // Fall through to error
            }
            
            System.out.println("Please enter a valid percentage (0-100).");
        }
    }

    // ===== Joint Account Management (Co-Owners) =====

    private void manageCoOwners(int accountId) {
        boolean managing = true;
        while (managing) {
            printCoOwnerMenu();
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            switch (choice) {
                case 1 -> viewCoOwners(accountId);
                case 2 -> addCoOwner(accountId);
                case 3 -> removeCoOwner(accountId);
                case 4 -> managing = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewCoOwners(int accountId) {
        System.out.println("\n== Account Co-Owners ==");
        
        try {
            Map<Integer, String> owners = clientAccountDAO.getClientsByAccount(accountId);
            
            if (owners.isEmpty()) {
                System.out.println("No owners found for this account.");
                return;
            }
            
            System.out.println("This account has " + owners.size() + " owner(s):");
            for (Map.Entry<Integer, String> entry : owners.entrySet()) {
                int clientId = entry.getKey();
                String ownershipType = entry.getValue();
                
                // Try to find client in both personal and business services
                String clientName = getClientName(clientId);
                
                System.out.printf("  Client #%d - %s [%s]%n", clientId, clientName, ownershipType);
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.out.println("Error retrieving co-owners. Please try again.");
        }
    }

    private void addCoOwner(int accountId) {
        try {
            System.out.println("\n== Add Co-Owner ==");
            System.out.println("Enter the Client ID of the person or business you want to add as a co-owner.");
            
            Optional<Integer> clientIdOpt = InputHandler.getIntInput("Enter Client ID (type 0 to cancel): ");
            
            if (clientIdOpt.isEmpty() || clientIdOpt.get() == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            int newClientId = clientIdOpt.get();
            
            // Verify client exists
            String clientName = getClientName(newClientId);
            if (clientName.equals("Unknown Client")) {
                System.out.println("Client ID not found. Please check the ID and try again.");
                return;
            }
            
            // Check if already an owner
            if (clientAccountDAO.clientOwnsAccount(newClientId, accountId)) {
                System.out.println("This client is already an owner of this account.");
                return;
            }
            
            // Confirm addition
            System.out.println("\nYou are about to add " + clientName + " (Client #" + newClientId + ") as a JOINT owner.");
            String confirmation = InputHandler.getStringInput("Type 'yes' to confirm: ");
            
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            // Add as JOINT owner
            clientAccountDAO.assignAccountToClient(newClientId, accountId, "JOINT");
            System.out.println("Successfully added " + clientName + " as a co-owner!");
            
        } catch (SQLException e) {
            System.out.println("Error adding co-owner. Please try again.");
        } catch (CancellationException e) {
            System.out.println("Operation cancelled.");
        }
    }

    private void removeCoOwner(int accountId) {
        try {
            System.out.println("\n== Remove Co-Owner ==");
            
            Map<Integer, String> owners = clientAccountDAO.getClientsByAccount(accountId);
            
            if (owners.size() <= 1) {
                System.out.println("Cannot remove owner. Account must have at least one owner.");
                return;
            }
            
            // Display current owners
            System.out.println("Current owners:");
            for (Map.Entry<Integer, String> entry : owners.entrySet()) {
                int clientId = entry.getKey();
                String ownershipType = entry.getValue();
                String clientName = getClientName(clientId);
                System.out.printf("  Client #%d - %s [%s]%n", clientId, clientName, ownershipType);
            }
            
            Optional<Integer> clientIdOpt = InputHandler.getIntInput("\nEnter Client ID to remove (type 0 to cancel): ");
            
            if (clientIdOpt.isEmpty() || clientIdOpt.get() == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            int removeClientId = clientIdOpt.get();
            
            // Verify they are an owner
            if (!owners.containsKey(removeClientId)) {
                System.out.println("This client is not an owner of this account.");
                return;
            }
            
            String clientName = getClientName(removeClientId);
            
            // Confirm removal
            System.out.println("\nYou are about to remove " + clientName + " (Client #" + removeClientId + ") from this account.");
            System.out.println("WARNING: They will no longer have access to this account!");
            String confirmation = InputHandler.getStringInput("Type 'yes' to confirm: ");
            
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Operation cancelled.");
                return;
            }
            
            // Remove the owner
            clientAccountDAO.removeAccountFromClient(removeClientId, accountId);
            System.out.println("Successfully removed " + clientName + " as a co-owner.");
            
        } catch (SQLException e) {
            System.out.println("Error removing co-owner. Please try again.");
        } catch (CancellationException e) {
            System.out.println("Operation cancelled.");
        }
    }

    private String getClientName(int clientId) {
        // Try personal client first
        Optional<com.jbank.model.PersonalClient> personalClient = personalClientService.getById(clientId);
        if (personalClient.isPresent()) {
            return personalClient.get().getName();
        }
        
        // Try business client
        Optional<com.jbank.model.BusinessClient> businessClient = businessClientService.getById(clientId);
        if (businessClient.isPresent()) {
            return businessClient.get().getName();
        }
        
        return "Unknown Client";
    }

    // ===== Menu Printers =====

    private static void printAccountMenu() {
        System.out.println("\n== Account Management ==");
        System.out.println("1. View All Accounts");
        System.out.println("2. Manage Checking Accounts");
        System.out.println("3. Manage Savings Accounts");
        System.out.println("4. Manage Credit Lines");
        System.out.println("5. Open New Account");
        System.out.println("6. Back");
    }

    private static void printCheckingOperationsMenu(CheckingAccount account) {
        System.out.println("\n== Checking Account #" + account.getAccountID() + " ==");
        System.out.println("Balance: " + ValidationUtils.formatCurrency(account.getBalance()));
        System.out.println("1. View Account Details");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Manage Co-Owners");
        System.out.println("5. Close Account");
        System.out.println("6. Back");
    }

    private static void printSavingsOperationsMenu(SavingsAccount account) {
        int remaining = account.getWithdrawalLimit() - account.getWithdrawalCounter();
        System.out.println("\n== Savings Account #" + account.getAccountID() + " ==");
        System.out.println("Balance: " + ValidationUtils.formatCurrency(account.getBalance()) + " | Withdrawals remaining: " + remaining);
        System.out.println("1. View Account Details");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Manage Co-Owners");
        System.out.println("5. Close Account");
        System.out.println("6. Back");
    }

    private static void printCreditLineOperationsMenu(CreditLine account) {
        double available = account.getCreditLimit() - account.getBalance();
        System.out.println("\n== Credit Line #" + account.getAccountID() + " ==");
        System.out.println("Balance: " + ValidationUtils.formatCurrency(account.getBalance()) + " | Available: " + ValidationUtils.formatCurrency(available));
        System.out.println("1. View Account Details");
        System.out.println("2. Make a Charge");
        System.out.println("3. Make a Payment");
        System.out.println("4. Manage Co-Owners");
        System.out.println("5. Close Credit Line");
        System.out.println("6. Back");
    }

    private static void printCoOwnerMenu() {
        System.out.println("\n== Manage Co-Owners ==");
        System.out.println("1. View All Co-Owners");
        System.out.println("2. Add Co-Owner");
        System.out.println("3. Remove Co-Owner");
        System.out.println("4. Back");
    }
}
