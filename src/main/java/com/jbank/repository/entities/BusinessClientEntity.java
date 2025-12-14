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
    private String name;
    private String businessName;
    private String ein; // Employer Identification Number

    public BusinessClientEntity() {
    }

    public BusinessClientEntity(int customerID, String phoneNumber, String address, String name, String businessName, String ein) {
        this.customerID = customerID;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.name = name;
        this.businessName = businessName;
        this.ein = ein;
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

    public String getBusinessName() {
        return businessName;
    }

    public String getEIN() {
        return ein;
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

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setEIN(String ein) {
        this.ein = ein;
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
                ", businessName='" + businessName + '\'' +
                ", ein='" + ein + '\'' +
                '}';
    }
}
