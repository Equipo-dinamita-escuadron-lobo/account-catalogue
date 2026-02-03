package com.account_catalogue.accounting.application.service;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IMessageProcessingErrorCommandPort;
import com.account_catalogue.accounting.application.input.IMessageProcessingErrorQueryPort;
import com.account_catalogue.accounting.application.output.IMessageProcessingErrorPersistencePort;
import com.account_catalogue.accounting.domain.exception.MessageProcessingErrorNotFoundException;
import com.account_catalogue.accounting.domain.models.MessageProcessingError;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageProcessingErrorService implements IMessageProcessingErrorCommandPort, IMessageProcessingErrorQueryPort{
     private final IMessageProcessingErrorPersistencePort persistencePort;
    
    @Override
    public MessageProcessingError findById(Long id) {
        return persistencePort.findById(id).orElseThrow(() -> 
            new  MessageProcessingErrorNotFoundException("MessageProcessingError with ID " + id + " not found"));
    }

    @Override
    public MessageProcessingError findLastRecord() {
        return persistencePort.findLastRecord().orElseThrow(() -> 
            new MessageProcessingErrorNotFoundException("No message processing errors found"));
    }

    @Override
    public void deleteAll() {
        persistencePort.deleteAll();
    }
}
