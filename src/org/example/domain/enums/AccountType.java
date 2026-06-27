package org.example.domain.enums;

public enum AccountType {
    DEBIT("Debit Account"),
    CREDIT("Credit Account");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}