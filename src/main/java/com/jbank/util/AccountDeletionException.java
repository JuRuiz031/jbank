package com.jbank.util;

/**
 * Exception thrown when a client deletion is prevented due to account validation failures.
 * Contains info about which accounts could not be deleted and why.
 */
public class AccountDeletionException extends Exception {
    private final String accountDetails;

    public AccountDeletionException(String message, String accountDetails) {
        super(message);
        this.accountDetails = accountDetails;
    }

    public AccountDeletionException(String message) {
        super(message);
        this.accountDetails = "";
    }

    public String getAccountDetails() {
        return accountDetails;
    }

    @Override
    public String toString() {
        return super.toString() + (accountDetails.isEmpty() ? "" : "\n" + accountDetails);
    }
}
