package com.account_catalogue.accounting.infraestructure.output.messageBroker.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.account_catalogue.accounting.application.input.IWriteOffProcessInputPort;
import com.account_catalogue.accounting.domain.exception.ValidationException;
import com.account_catalogue.accounting.domain.models.PortfolioWriteOff;
import com.account_catalogue.accounting.domain.ports.IMessageErrorHandlingPort;
import com.account_catalogue.accounting.infraestructure.config.WriteOffRabbitConfig;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.EventDTO;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PortfolioWriteOffResponse;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.base.AbstractMessageListener;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.mapper.IPortfolioWriteOffEventMapper;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.utils.JsonUtils;
import com.rabbitmq.client.Channel;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@AllArgsConstructor
@Slf4j
public class WriteOffMessageListener extends AbstractMessageListener <EventDTO<PortfolioWriteOffResponse>> {
     private final IWriteOffProcessInputPort writeOffProcessInputPort;
    private final IPortfolioWriteOffEventMapper writeOffEventMapper;

    @Qualifier("messageErrorHandlingAdapter")
    private final IMessageErrorHandlingPort messageErrorHandlingPortImpl;

    @PostConstruct
    private void init() {
        this.messageErrorHandlingPort = messageErrorHandlingPortImpl;
    }

    @RabbitListener(queues = WriteOffRabbitConfig.WRITEOFF_ACCOUNTING_QUEUE)
    public void processWriteOffEvent(EventDTO<PortfolioWriteOffResponse> event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("Evento de Castigo recibido: '{}' para el código: {}",
                event.getType(),
                event.getData() != null ? event.getData().getCode() : "N/A");
        handleMessage(event, channel, deliveryTag);
    }

    @Override
    protected void validateEvent(EventDTO<PortfolioWriteOffResponse> event) throws ValidationException {
        if (event == null) throw new ValidationException("Validation failed: Event is null");
        if (event.getData() == null) throw new ValidationException("Validation failed: Event data is null");
        if (event.getType() == null) throw new ValidationException("Validation failed: Event type is null");

        PortfolioWriteOffResponse data = event.getData();
        if (data.getCode() == null || data.getCode().isBlank()) throw new ValidationException("Validation failed: Code is null or blank");
        if (data.getThirdId() == null) throw new ValidationException("Validation failed: ThirdId is null");
        if (data.getEnterpriseId() == null) throw new ValidationException("Validation failed: EnterpriseId is null");
        if (data.getStatus() == null) throw new ValidationException("Validation failed: Status is null");
    }

    @Override
    protected void processEvent(EventDTO<PortfolioWriteOffResponse> event) {
        PortfolioWriteOffResponse writeOffDTO = event.getData();
        try {
            PortfolioWriteOff writeOff = writeOffEventMapper.toDomain(writeOffDTO);

            switch (event.getType()) {
                case "WRITEOFF_CONFIRMED":  
                    writeOffProcessInputPort.processWriteOffConfirmation(writeOff);
                    break;
                case "WRITEOFF_VOIDED":
                    writeOffProcessInputPort.processWriteOffAnnulment(writeOff);
                    break;
                default:
                    log.warn("Tipo de evento de castigo no soportado: '{}'. Mensaje ignorado.", event.getType());
                    break;
            }
            log.info("Evento de castigo {} procesado exitosamente.", writeOffDTO.getCode());
        } catch (Exception e) {
            log.error("Error al procesar evento para castigo {}.", writeOffDTO.getCode(), e);
            throw e;
        }
    }

    @Override
    protected String getEntityType() { return "PortfolioWriteOff"; }

    @Override
    protected String extractEventType(EventDTO<PortfolioWriteOffResponse> event) {
        return event != null ? event.getType() : "unknown";
    }

    @Override
    protected String convertEventToJson(EventDTO<PortfolioWriteOffResponse> event) {
        if (event == null || event.getData() == null) {
            return "{\"error\": \"Event or event data is null\"}";
        }
        return JsonUtils.writeOffDtoToJsonWithNullHandling(event.getData());
    }
}
