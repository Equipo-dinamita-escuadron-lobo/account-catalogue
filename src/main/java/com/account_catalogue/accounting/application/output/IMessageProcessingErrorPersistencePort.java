package com.account_catalogue.accounting.application.output;

import java.util.Optional;

import com.account_catalogue.accounting.domain.models.MessageProcessingError;

public interface IMessageProcessingErrorPersistencePort {
    Optional<MessageProcessingError> findById(Long id);
    Optional<MessageProcessingError> findLastRecord();
    void deleteAll();
}
