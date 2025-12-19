package com.jbank.util;

import java.util.Optional;

import com.jbank.validator.ClientValidator;

/**
 *
 * @author juanf
 */
public class ClientInputHelper {
    
    // Helper methods for reading and validating client input
    public static Optional<String> readName(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if(input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if(ClientValidator.isValidName(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid name. Please enter a valid name (1-50 characters).");
        }   
    }

    public static Optional<String> readAddress(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if(input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if(ClientValidator.isValidAddress(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid address. Please enter a valid address (1-200 characters).");
        }
    }

    public static Optional<String> readPhoneNumber(String prompt) {
        while (true) {
            String input = InputHandler.getStringInput(prompt);
            
            if(input.equalsIgnoreCase("quit")) {
                return Optional.empty();
            }
            
            if(ClientValidator.isValidPhone(input)) {
                return Optional.of(input);
            }
            
            System.out.println("Invalid phone number. Please enter a valid 10-digit phone number.");
        }
    }

}
