package com.account_catalogue.accounting.infraestructure.output.messageBroker.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.account_catalogue.accounting.application.input.IWriteOffProcessInputPort;
import com.account_catalogue.accounting.domain.models.PortfolioWriteOff;
import com.account_catalogue.accounting.infraestructure.config.WriteOffRabbitConfig;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.EventDTO;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PortfolioWriteOffResponse;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.mapper.IPortfolioWriteOffEventMapper;
import com.rabbitmq.client.Channel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@AllArgsConstructor
@Slf4j
public class WriteOffMessageListener {
    private final IWriteOffProcessInputPort writeOffProcessInputPort;
    private final IPortfolioWriteOffEventMapper writeOffEventMapper;

    /**
     * Escucha en la cola dedicada a los eventos de Castigo de Cartera.
     * Deserializa automáticamente a EventDTO<PortfolioWriteOffResponse>.
     */
    @RabbitListener(queues = WriteOffRabbitConfig.WRITEOFF_ACCOUNTING_QUEUE)
    public void processWriteOffEvent(EventDTO<PortfolioWriteOffResponse> event,
                                     Message message, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        String eventType = event.getType();
        PortfolioWriteOffResponse writeOffDTO = event.getData();

        if (writeOffDTO == null) {
            log.error("Evento de castigo recibido con data nula. Tipo: {}. Descartando.", eventType);
            throw new AmqpRejectAndDontRequeueException("Data del evento de castigo es nula");
        }

        log.info("Evento de Castigo recibido: '{}' para el código: {}", eventType, writeOffDTO.getCode());

        try {
            // 1. Mapear DTO a Modelo de Dominio
            PortfolioWriteOff writeOff = writeOffEventMapper.toDomain(writeOffDTO);

            // 2. Invocar el caso de uso apropiado
            switch (eventType) {
                case "WRITEOFF_CONFIRMED":  
                    writeOffProcessInputPort.processWriteOffConfirmation(writeOff);
                    break;

                case "WRITEOFF_VOIDED":
                    writeOffProcessInputPort.processWriteOffAnnulment(writeOff);
                    break;

                default:
                    log.warn("Tipo de evento de castigo no soportado: '{}'. Mensaje ignorado.", eventType);
                    break;
            }

            log.info("Evento de castigo {} procesado exitosamente.", writeOffDTO.getCode());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Error al procesar evento para castigo {}. Mensaje será enviado a DLQ.", writeOffDTO.getCode(), e);
            throw new AmqpRejectAndDontRequeueException("Error de procesamiento de negocio para castigo", e);
        }
    }
}
