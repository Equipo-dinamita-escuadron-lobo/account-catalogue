package com.account_catalogue.accounting.domain.exception;

public class ReceiptNotFoundException extends RuntimeException {
    public ReceiptNotFoundException(String message) {
        super(message);
    }
}
