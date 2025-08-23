package com.account_catalogue.banks.domain.enums;

/**
 * Enum que define las monedas soportadas para los bancos.
 * Estas monedas están predefinidas en el sistema.
 */
public enum Currency {
    COP("Peso Colombiano"),
    USD("Dólar Estadounidense"),
    EUR("Euro"),
    GBP("Libra Esterlina"),
    CHF("Franco Suizo"),
    JPY("Yen Japonés");

    private final String description;

    Currency(String description) {
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
