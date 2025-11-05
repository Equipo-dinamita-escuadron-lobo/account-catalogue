package com.account_catalogue.accounting.domain.exception;

public class InvoiceNotFoundException extends RuntimeException {
     public InvoiceNotFoundException(String message) {
        super(message);
    }
}
