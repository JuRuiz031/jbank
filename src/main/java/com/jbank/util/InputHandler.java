package com.jbank.util;

import java.util.Optional;
import java.util.Scanner;

import com.jbank.validator.ValidationUtils;

/**
 *
 * @author juanf
 */
public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    // Get integer input
    public static Optional<Integer> getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Optional.of(Integer.valueOf(scanner.nextLine()));
        } catch(NumberFormatException e) {
            return Optional.empty();
        }
    }

    // Get double input (accepts currency format with commas)
    public static Optional<Double> getDoubleInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return ValidationUtils.parseCurrencyString(input);
    }

    // Get string input
    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}