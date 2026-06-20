package com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.MessageProcessingErrorEntity;

/**
 * Repositorio para gestionar los errores de procesamiento de mensajes.
 */
public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
    
    /**
     * Finds the most recent MessageProcessingErrorEntity based on the highest ID.
     *
     * @return An Optional containing the latest MessageProcessingErrorEntity if found, otherwise empty.
     */
    Optional<MessageProcessingErrorEntity> findFirstByOrderByIdDesc();
}
