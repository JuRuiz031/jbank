package com.jbank;

import java.util.Optional;

import com.jbank.controller.PersonalClientController;
import com.jbank.util.InputHandler;

public class App {
    private static final PersonalClientController personalClientController = new PersonalClientController();
    //private static final BusinessClientController businessClientController = new BusinessClientController();
    private static boolean appRunning = true;
    
    public static void main(String[] args) {
        System.out.println("\n\n==Welcome to JBank==");

        while(appRunning) {
            printMainMenu();
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            while(choice < 1 || choice > 3) {
                System.out.println("Invalid choice. Please try again.");
                printMainMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }
            
            switch(choice) {
                case 1 -> createNewCustomer();
                case 2 -> handleExistingCustomer();
                case 3 -> {
                    System.out.println("Thank you for using JBank. Goodbye!");
                    appRunning = false;
                }
                }
            }
        }

    private static void createNewCustomer() {
        boolean inMenu = true;
        
        while (inMenu) {
            printNewClientMenu();
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            while(choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Please try again.");
                printNewClientMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }

            switch(choice) {
                case 1 -> personalClientController.createNewClient();
                case 2 -> System.out.println("Create new business customer functionality is not yet implemented.");
                case 3 -> inMenu = false;
                case 4 -> {
                    System.out.println("Thank you for using JBank. Goodbye!");
                    appRunning = false;
                    inMenu = false;
                }
            }
        }
    }
    
    private static void handleExistingCustomer() {
        boolean inMenu = true;

        while (inMenu) {
            printExistingClientMenu();
            
            Optional<Integer> choiceOpt = InputHandler.getIntInput("Enter your choice: ");
            int choice = choiceOpt.orElse(0);
            
            while(choice < 1 || choice > 4) {
                System.out.println("Invalid choice. Please try again.");
                printExistingClientMenu();
                choiceOpt = InputHandler.getIntInput("Enter your choice: ");
                choice = choiceOpt.orElse(0);
            }

            switch(choice) {
                case 1 -> personalClientController.handleExistingClient();
                case 2 -> System.out.println("Handle existing business customer functionality is not yet implemented.");
                case 3 -> inMenu = false;
                case 4 -> {
                    System.out.println("Thank you for using JBank. Goodbye!");
                    appRunning = false;
                    inMenu = false;
                }
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n**** Main Menu ****");
        System.out.println("\n Please select from one of the following options: ");
        System.out.println("1. New Customer");
        System.out.println("2. Existing Customer");
        System.out.println("3. Exit JBank");
    }

    private static void printNewClientMenu() {
        System.out.println("\n== Create New Customer ==");
        System.out.println("\n Please select from one of the following options: ");
        System.out.println("1. Create New Personal Customer");
        System.out.println("2. Create New Business Customer");
        System.out.println("3. Back to Main Menu");
        System.out.println("4. Exit JBank");
    }

    private static void printExistingClientMenu() {
        System.out.println("\n== Existing Customer Menu ==");
        System.out.println("\n Please select from one of the following options: ");
        System.out.println("1. Existing Personal Customer");
        System.out.println("2. Existing Business Customer");
        System.out.println("3. Back to Main Menu");
        System.out.println("4. Exit JBank");
    }
}
