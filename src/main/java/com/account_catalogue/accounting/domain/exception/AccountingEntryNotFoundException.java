package com.account_catalogue.accounting.domain.exception;

public class AccountingEntryNotFoundException extends RuntimeException {
    public AccountingEntryNotFoundException(String message) {
        super(message);
    }
}
