package com.jbank.model;

/**
 *
 * @author juanf
 */
public class PersonalClient extends AbstractClient {
    private final String taxID;
    private int creditScore;
    private double yearlyIncome;
    private double totalDebt;

    public PersonalClient(int customerID, String name, String address, String phoneNumber, String taxID, int creditScore, double yearlyIncome, double totalDebt) {
        super(customerID, phoneNumber, address, name);

        // Input validation
        if(taxID == null || taxID.isEmpty() || taxID.length() != 9) {
            throw new IllegalArgumentException("Tax ID must be exactly 9 characters.");
        }

        if (creditScore < 300 || creditScore > 850) {
            throw new IllegalArgumentException("Credit score must be between 300 and 850.");
        }

        if (yearlyIncome < 0) {
            throw new IllegalArgumentException("Yearly income cannot be negative.");
        }

        if (totalDebt < 0) {
            throw new IllegalArgumentException("Total debt cannot be negative.");
        }

        this.taxID = taxID;
        this.creditScore = creditScore;
        this.yearlyIncome = yearlyIncome;
        this.totalDebt = totalDebt;
    }

    // Mutators
    public void updateCreditScore(int newScore) {
        if (newScore < 300 || newScore > 850) {
            throw new IllegalArgumentException("Credit score must be between 300 and 850.");
        }
        this.creditScore = newScore;
    }

    public void updateYearlyIncome(double newIncome) {
        if (newIncome < 0) {
            throw new IllegalArgumentException("Yearly income cannot be negative.");
        }
        this.yearlyIncome = newIncome;
    }

    public void updateTotalDebt(double newDebt) {
        if (newDebt < 0) {
            throw new IllegalArgumentException("Total debt cannot be negative.");
        }
        this.totalDebt = newDebt;
    }

    // Getters
    public String getTaxID() {
        return taxID;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public double getYearlyIncome() {
        return yearlyIncome;
    }

    public double getTotalDebt() {
        return totalDebt;
    }

    public double getDebtToIncomeRatio() {
        if (yearlyIncome == 0) {
            return 0;
        }
        return (totalDebt / yearlyIncome) * 100;
    }
}