package com.jbank.controller;

import java.util.Optional;

import com.jbank.model.AbstractClient;
import com.jbank.util.InputHandler;

/**
 *
 * @author juanf
 */
public class AccountController {

    public void handleAccountManagement(AbstractClient client) {
        boolean managing = true;
        while (managing) {
            printAccountMenu();

            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            while (choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Please try again.");
                printAccountMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }

            switch (choice) {
                case 1 -> viewCheckingAccount(client);
                case 2 -> viewSavingsAccount(client);
                case 3 -> viewCreditLine(client);
                case 4 -> managing = false;
            }
        }
    }

    private void viewCheckingAccount(AbstractClient client) {
        System.out.println("\n== Checking Account ==");
        System.out.println("Checking Account functionality is not yet implemented.");
    }

    private void viewSavingsAccount(AbstractClient client) {
        System.out.println("\n== Savings Account ==");
        System.out.println("Savings Account functionality is not yet implemented.");
    }

    private void viewCreditLine(AbstractClient client) {
        System.out.println("\n== Credit Line ==");
        System.out.println("Credit Line functionality is not yet implemented.");
    }

    private static void printAccountMenu() {
        System.out.println("\n== Account Management ==");
        System.out.println("1. View Checking Account");
        System.out.println("2. View Savings Account");
        System.out.println("3. View Credit Line");
        System.out.println("4. Back");
    }
}
