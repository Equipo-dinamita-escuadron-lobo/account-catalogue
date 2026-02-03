package com.account_catalogue.accounting.infraestructure.output.jpaAdapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.account_catalogue.accounting.application.output.IMessageProcessingErrorPersistencePort;
import com.account_catalogue.accounting.domain.models.MessageProcessingError;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper.IMessageProcessingErrorPersistenceMapper;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IMessageProcessingErrorRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageProcessingErrorPersistenceAdapter implements IMessageProcessingErrorPersistencePort {

    private final IMessageProcessingErrorRepository repository;
    private final IMessageProcessingErrorPersistenceMapper mapper; 

    @Override
    public Optional<MessageProcessingError> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<MessageProcessingError> findLastRecord() {
        return repository.findFirstByOrderByIdDesc().map(mapper::toDomain);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
