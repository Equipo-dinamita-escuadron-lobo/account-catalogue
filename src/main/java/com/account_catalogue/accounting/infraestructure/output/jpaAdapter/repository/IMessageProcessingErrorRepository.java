package com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.MessageProcessingErrorEntity;

/**
 * Repositorio para gestionar los errores de procesamiento de mensajes.
 */
public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
}
