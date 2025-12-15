
package com.jbank.model;

import java.util.Set;

/**
 *
 * @author juanfruiz
 */
public class BusinessClient extends AbstractClient {
    // Valid business types
    private static final Set<String> VALID_BUSINESS_TYPES = Set.of(
        "LLC", "Corporation", "Partnership", "Sole Proprietorship", "Non-Profit"
    );
    // Valid titles for contact person
    public static final Set<String> VALID_TITLES = Set.of(
        "CEO", "CFO", "Manager", "Director", "Owner", "Partner"
    );

    // Business details
    private final String ein; // Employer Identification Number
    private String businessType;

    // Contact person details
    private String contactName;
    private String contactTitle;
    private String contactEmail;

    // Financial details
    private double totalAssetValue;
    private double annualRevenue;
    private double annualProfit;

    // Constructor
    public BusinessClient(int customerID, String name, String address, String phoneNumber, String ein, String businessType,
                          String contactName, String contactTitle, String contactEmail,
                          double totalAssetValue, double annualRevenue, double annualProfit) {
        super(customerID, phoneNumber, address, name);

        // Input validation
        if(ein == null || ein.isEmpty() || ein.length() != 9) {
            throw new IllegalArgumentException("EIN must be exactly 9 characters.");
        }
        if(businessType == null || !VALID_BUSINESS_TYPES.contains(businessType)) {
            throw new IllegalArgumentException("Invalid business type.");
        }
        if(contactName == null || contactName.isEmpty() || contactName.length() < 3 || contactName.length() > 50) {
            throw new IllegalArgumentException("Contact name must be between 3 and 50 characters.");
        }
        if(contactTitle == null || !VALID_TITLES.contains(contactTitle)) {
            throw new IllegalArgumentException("Invalid contact title.");
        }
        if(contactEmail == null || !contactEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid contact email.");
        }
        if(totalAssetValue < 0 || annualRevenue < 0 || annualProfit < 0) {
            throw new IllegalArgumentException("Financial values cannot be negative.");
        }

        this.ein = ein;
        this.businessType = businessType;
        this.contactName = contactName;
        this.contactTitle = contactTitle;
        this.contactEmail = contactEmail;
        this.totalAssetValue = totalAssetValue;
        this.annualRevenue = annualRevenue;
        this.annualProfit = annualProfit;
    }

    // Setters
    public void setBusinessType(String newType) {
        if(newType == null || !VALID_BUSINESS_TYPES.contains(newType)) {
            throw new IllegalArgumentException("Invalid business type.");
        }
        this.businessType = newType;
    }

    public void setContactName(String newContactName) {
        if(newContactName == null || newContactName.isEmpty() || newContactName.length() < 3 || newContactName.length() > 50) {
            throw new IllegalArgumentException("Contact name must be between 3 and 50 characters.");
        }
        this.contactName = newContactName;
    }

    public void setContactTitle(String newContactTitle) {
        if(newContactTitle == null || !VALID_TITLES.contains(newContactTitle)) {
            throw new IllegalArgumentException("Invalid contact title.");
        }
        this.contactTitle = newContactTitle;
    }

    public void setContactEmail(String newContactEmail) {
        if(newContactEmail == null || !newContactEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid contact email.");
        }
        this.contactEmail = newContactEmail;
    }

    public void setTotalAssetValue(double newTotalAssetValue) {
        if(newTotalAssetValue < 0) {
            throw new IllegalArgumentException("Total asset value cannot be negative.");
        }
        this.totalAssetValue = newTotalAssetValue;
    }

    public void setAnnualRevenue(double newAnnualRevenue) {
        if(newAnnualRevenue < 0) {
            throw new IllegalArgumentException("Annual revenue cannot be negative.");
        }
        this.annualRevenue = newAnnualRevenue;
    }

    public void setAnnualProfit(double newAnnualProfit) {
        if(newAnnualProfit < 0) {
            throw new IllegalArgumentException("Annual profit cannot be negative.");
        }
        this.annualProfit = newAnnualProfit;
    }

    // Getters
    public String getEin() {
        return ein;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public String getContactEmail() {
        return contactEmail;
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
    public double getAnnualProfitMargin() {
        if (annualRevenue == 0) {
            return 0;
        }
        return (annualProfit / annualRevenue) * 100;
    }
    public double getReturnOnAssets() {
        if (totalAssetValue == 0) {
            return 0;
        }
        return (annualProfit / totalAssetValue) * 100;
    }

    @Override
    public String toString() {
        String maskedEin = (ein == null || ein.length() < 4)
                ? "****"
                : "**-" + ein.substring(ein.length() - 4);
        return "BusinessClient{" +
                "customerID=" + getCustomerID() +
                ", name='" + getName() + '\'' +
                ", businessType='" + businessType + '\'' +
                ", ein='" + maskedEin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessClient that = (BusinessClient) o;
        long thisId = getCustomerID();
        long thatId = that.getCustomerID();
        if (thisId > 0 && thatId > 0) {
            return thisId == thatId;
        }
        return java.util.Objects.equals(this.ein, that.ein);
    }

    @Override
    public int hashCode() {
        long id = getCustomerID();
        return (id > 0)
                ? java.util.Objects.hash(id)
                : java.util.Objects.hash(ein);
    }
}