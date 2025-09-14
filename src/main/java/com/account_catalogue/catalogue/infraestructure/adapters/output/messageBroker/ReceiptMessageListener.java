package com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.application.input.IReceiptProcessInputPort;
import com.account_catalogue.catalogue.infraestructure.adapters.config.ReceiptRabbitConfig;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.ReceiptEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class ReceiptMessageListener {
    private final IReceiptProcessInputPort receiptProcessInputPort;
    private final ObjectMapper objectMapper; // Para deserializar el JSON

    @RabbitListener(queues = ReceiptRabbitConfig.RECEIPT_ACCOUNTING_QUEUE)
    public void receiveReceiptEvent(String message) {
        log.info("Received message from RabbitMQ queue '{}': {}", ReceiptRabbitConfig.RECEIPT_ACCOUNTING_QUEUE, message);
        try {
            ReceiptEventDTO eventDTO = objectMapper.readValue(message, ReceiptEventDTO.class);
            receiptProcessInputPort.processReceiptEvent(eventDTO);
            log.info("Receipt event with original ID: {} processed successfully.", eventDTO.getId());
        } catch (JsonProcessingException e) {
            log.error("Error processing receipt event from RabbitMQ. Message will be retried or moved to DLQ. Error: {}", e.getMessage(), e);
            // La configuración de RabbitMQ que tienes (DLQ y retry) se encargará de esto.
            // Para que Spring AMQP sepa que el mensaje debe ser rechazado y potencialmente re-enviado a la DLX,
            // debes lanzar una AmqpRejectAndDontRequeueException si no quieres que reintente en la misma cola,
            // o simplemente una RuntimeException si quieres que reintente de acuerdo a tu config del listener.
            // Dada tu configuración de DLX y retry queue, Spring AMQP lo manejará por ti al lanzar una excepción.
            throw new AmqpRejectAndDontRequeueException("Error processing receipt event", e);
        }
    }
}
