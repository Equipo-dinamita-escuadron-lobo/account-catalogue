package com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.application.input.IReceiptProcessInputPort;
import com.account_catalogue.catalogue.domain.models.Receipt;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.EventDTO;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.ReceiptEventDTO;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.mapper.IReceiptEventMapper;
import com.account_catalogue.catalogue.infraestructure.config.ReceiptRabbitConfig;
import com.rabbitmq.client.Channel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class ReceiptMessageListener {
    private final IReceiptProcessInputPort receiptProcessInputPort;
    private final IReceiptEventMapper receiptEventMapper;

    /**
     * Escucha en la cola de recibos. Spring AMQP, gracias al Jackson2JsonMessageConverter,
     * deserializará automáticamente el cuerpo del mensaje JSON al objeto EventDTO<ReceiptEventDTO>.
     * ¡No necesitamos usar ObjectMapper manualmente!
     */
    @RabbitListener(queues = ReceiptRabbitConfig.RECEIPT_ACCOUNTING_QUEUE)
    public void processReceiptEvent(EventDTO<ReceiptEventDTO> event, 
        Message message, Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String eventType = event.getType();
        ReceiptEventDTO receiptDTO = event.getData();
        
        // Validamos que la data no sea nulo para evitar NullPointerException
        if (receiptDTO == null) {
            log.error("Evento recibido con data nula. Tipo de evento: {}. El mensaje será descartado.", eventType);
            // No hacemos requeue porque el mensaje está malformado.
            throw new AmqpRejectAndDontRequeueException("Data del evento es nula");
        }
        
        log.info("Evento recibido: '{}' para el recibo: {}", eventType, receiptDTO.getReceiptCode());

        try {
            Receipt receipt = receiptEventMapper.toDomain(receiptDTO);

            // 2. Invocar el caso de uso apropiado de la aplicación basado en el tipo de evento
            switch (eventType) {
                case "RECEIPT_CREATED":
                    receiptProcessInputPort.processReceiptCreation(receipt);
                    break;
                case "RECEIPT_VOIDED":
                    receiptProcessInputPort.processReceiptVoid(receipt);
                    break;
                default:
                    log.warn("Tipo de evento no soportado: '{}'. El mensaje será ignorado.", eventType);
                    break;
            }

            log.info("Recibo {} procesado exitosamente.", receiptDTO.getReceiptCode());

        } catch (Exception e) {
            log.error("Error al procesar el evento para el recibo {}. Mensaje será enviado a DLQ.", receiptDTO.getReceiptCode(), e);
            // Lanzamos esta excepción para que RabbitMQ mueva el mensaje a la DLQ
            throw new AmqpRejectAndDontRequeueException("Error de procesamiento de negocio", e);
        }
    }
}
