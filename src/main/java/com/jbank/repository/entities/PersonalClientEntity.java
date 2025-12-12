package com.jbank.repository.entities;

import java.util.Objects;

/**
 *
 * @author juanfruiz
 */
public class PersonalClientEntity {
    private int customerID;
    private String phoneNumber;
    private String address;
    private String name;
    private String taxID;
    private int creditScore;
    private double yearlyIncome;
    private double totalDebt;

    public PersonalClientEntity() {
    }

    public PersonalClientEntity(int customerID, String phoneNumber, String address, String name, String taxID, int creditScore, double yearlyIncome, double totalDebt) {
        this.customerID = customerID;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.name = name;
        this.taxID = taxID;
        this.creditScore = creditScore;
        this.yearlyIncome = yearlyIncome;
        this.totalDebt = totalDebt;
    }

    // Getters
    public int getCustomerID() {
        return customerID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

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


    // Setters

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public void setYearlyIncome(double yearlyIncome) {
        this.yearlyIncome = yearlyIncome;
    }

    public void setTotalDebt(double totalDebt) {
        this.totalDebt = totalDebt;
    }
    
    @Override
    public String toString() {
        String maskedTax = (taxID == null || taxID.length() < 4)
            ? "****"
            : "***-**-" + taxID.substring(taxID.length() - 4);
        return "PersonalClientEntity{" +
            "customerID=" + customerID +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", address='" + address + '\'' +
            ", name='" + name + '\'' +
            ", taxID='" + maskedTax + '\'' +
            ", creditScore=" + creditScore +
            ", yearlyIncome=" + yearlyIncome +
            ", totalDebt=" + totalDebt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalClientEntity that = (PersonalClientEntity) o;
        // Prefer database identity when available
        if (this.customerID > 0 && that.customerID > 0) {
            return this.customerID == that.customerID;
        }
        // Fallback to taxID business key
        return Objects.equals(this.taxID, that.taxID);
    }

    @Override
    public int hashCode() {
        // Stable hash based on identity: DB id if assigned, else business key
        return (customerID > 0)
            ? Objects.hash(customerID)
            : Objects.hash(taxID);
    }
}
