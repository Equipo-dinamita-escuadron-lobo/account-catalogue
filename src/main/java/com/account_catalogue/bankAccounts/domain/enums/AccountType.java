package com.account_catalogue.bankAccounts.domain.enums;

/**
 * Enum que define los tipos de cuenta bancaria soportados.
 * Estos tipos están predefinidos en el sistema.
 */
public enum AccountType {
    AHORROS("Cuenta de Ahorros"),
    CORRIENTE("Cuenta Corriente");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name() + " - " + this.description;
    }
}
