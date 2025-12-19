package com.jbank.repository.entities;

import java.util.Objects;

/**
 * Entity class representing a business client in the database.
 * Maps to clients and business_clients tables.
 * 
 * @author juanf
 */
public class BusinessClientEntity {
    private int customerID;
    private String phoneNumber;
    private String address;
    private String name; // Business name
    private String ein; // Employer Identification Number
    private String businessType;
    private String contactPersonName;
    private String contactPersonTitle;
    private double totalAssetValue;
    private double annualRevenue;
    private double annualProfit;

    public BusinessClientEntity() {
    }

    public BusinessClientEntity(int customerID, String phoneNumber, String address, String name, String ein,
                                String businessType, String contactPersonName, String contactPersonTitle,
                                double totalAssetValue, double annualRevenue, double annualProfit) {
        this.customerID = customerID;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.name = name;
        this.ein = ein;
        this.businessType = businessType;
        this.contactPersonName = contactPersonName;
        this.contactPersonTitle = contactPersonTitle;
        this.totalAssetValue = totalAssetValue;
        this.annualRevenue = annualRevenue;
        this.annualProfit = annualProfit;
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

    public String getEIN() {
        return ein;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public String getContactPersonTitle() {
        return contactPersonTitle;
    }

    public double getTotalAssetValue() {
        return totalAssetValue;
    }

    public double getAnnualRevenue() {
        return annualRevenue;
    }

    public double getAnnualProfit() {
        return annualProfit;
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

    public void setEIN(String ein) {
        this.ein = ein;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public void setContactPersonTitle(String contactPersonTitle) {
        this.contactPersonTitle = contactPersonTitle;
    }

    public void setTotalAssetValue(double totalAssetValue) {
        this.totalAssetValue = totalAssetValue;
    }

    public void setAnnualRevenue(double annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public void setAnnualProfit(double annualProfit) {
        this.annualProfit = annualProfit;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessClientEntity that = (BusinessClientEntity) o;
        return customerID == that.customerID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerID);
    }

    @Override
    public String toString() {
        return "BusinessClientEntity{" +
                "customerID=" + customerID +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", ein='" + ein + '\'' +
                ", businessType='" + businessType + '\'' +
                ", contactPersonName='" + contactPersonName + '\'' +
                ", contactPersonTitle='" + contactPersonTitle + '\'' +
                ", totalAssetValue=" + totalAssetValue +
                ", annualRevenue=" + annualRevenue +
                ", annualProfit=" + annualProfit +
                '}';
    }
}
