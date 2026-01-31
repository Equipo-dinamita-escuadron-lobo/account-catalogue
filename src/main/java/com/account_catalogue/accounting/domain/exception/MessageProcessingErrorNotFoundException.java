package com.account_catalogue.accounting.domain.exception;

public class MessageProcessingErrorNotFoundException extends RuntimeException {
    public MessageProcessingErrorNotFoundException(String message) {
        super(message);
    }
    
}
