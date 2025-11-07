package com.account_catalogue.accounting.infraestructure.output.messageBroker.listener;

import com.account_catalogue.accounting.application.input.IInvoiceProcessInputPort;
import com.account_catalogue.accounting.domain.ports.IEventRecoveryActionPort;
import com.account_catalogue.accounting.domain.ports.IMessageErrorHandlingPort;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.adapter.InvoicePersistenceAdapter;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.base.AbstractMessageListener;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.utils.JsonUtils;
import com.account_catalogue.accounting.infraestructure.config.RabbitConfig;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.EventDTO;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.InvoiceSyncDto;
import com.rabbitmq.client.Channel;

import java.time.LocalDate;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceEventListener extends AbstractMessageListener<EventDTO<InvoiceSyncDto>> {
    private final InvoicePersistenceAdapter invoicePersistenceAdapter;
    private final IMessageErrorHandlingPort messageErrorHandlingPortImpl;
    private final IEventRecoveryActionPort<EventDTO<InvoiceSyncDto>> productRecoveryActionPort;
    private final IInvoiceProcessInputPort invoiceProcessInputPort;

    @PostConstruct
    private void init() {
        this.messageErrorHandlingPort = messageErrorHandlingPortImpl;
        this.eventRecoveryActionPort = productRecoveryActionPort;
    }

    @RabbitListener(queues = RabbitConfig.INVOICE_ACCOUNTING_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleInvoiceEvent(
            Message message,
            EventDTO<InvoiceSyncDto> event, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        log.info("Received event type '{}' for invoice with ID: {}",
                event.getType(),
                event.getData() != null ? event.getData().getFactCode() : "NA");

        handleMessage(event, channel, tag);
    }

    @Override
    protected void processEvent(EventDTO<InvoiceSyncDto> event) {
        InvoiceSyncDto dto = event.getData();
        String factCode = dto.getFactCode().toString() != null ? dto.getFactCode().toString() : "N/A";

        // --- INICIO DE LA MODIFICACIÓN ---
        // Si la fecha de creación es nula en el DTO recibido,
        // la establecemos a la fecha del día actual.
        if (dto.getCreationDate() == null) {
            log.warn("CreationDate is null for invoice factCode: {}. Setting to current date.", factCode);
            dto.setCreationDate(LocalDate.now());
        }
        // --- FIN DE LA MODIFICACIÓN ---

        try {

            switch (event.getType()) {
                case "SALE":
                    log.info("Processing SALE event for invoice factCode: {}", dto.getFactCode());
                    invoicePersistenceAdapter.saveOrUpdate(dto);
                    invoiceProcessInputPort.processInvoiceCreation(dto);
                    log.info("Successfully processed sale for invoice factCode: {}", dto.getFactCode());
                    break;

                case "DELETED":
                    log.info("Processing DELETE event for invoice factCode: {}", dto.getFactCode());
                    invoicePersistenceAdapter.delete(dto.getFactCode());
                    log.info("Successfully processed delete for invoice factCode: {}", dto.getFactCode());
                    break;

                default:
                    log.warn("Unknown event type '{}' for invoice. Message will be acknowledged and ignored.",
                            event.getType());
            }

        } catch (Exception e) {
            log.error("Database operation failed for invoice factCode: {}. Error: {}", factCode, e.getMessage());
            throw e;
        }
    }

    /**
     * @brief Validates invoice event data integrity
     * @param event Invoice event to validate
     * @return True if event is valid, false otherwise
     */
    @Override
    protected boolean isValidEvent(EventDTO<InvoiceSyncDto> event) {
        if (event == null) {
            log.warn("Event is null");
            return false;
        }
        if (event.getData() == null) {
            log.warn("Event data is null");
            return false;
        }
        if (event.getType() == null) {
            log.warn("Event type is null");
            return false;
        }

        // Validar campos obligatorios
        InvoiceSyncDto data = event.getData();
        if (data.getFactCode() == null) {
            log.warn("FactCode is null");
            return false;
        }

        if (data.getAccountingAccount() == null) {
            log.warn("AccountingAccount is null");
            return false;
        }

        if (data.getEntId() == null) {
            log.warn("EnterpriseId is null");
            return false;
        }

        if (data.getExpirationDate() == null) {
            log.warn("ExpirationDate is null");
            return false;
        }

        if (data.getPendingValue() == null) {
            log.warn("PendingValue is null");
            return false;
        }

        if (data.getThirdId() == null) {
            log.warn("ThirdId is null");
            return false;
        }

        if (data.getTotalPay() == null) {
            log.warn("TotalPay is null");
            return false;
        }

        if (data.getTotalValue() == null) {
            log.warn("TotalValue is null");
            return false;
        }

        if (data.getCreationDate() == null) {
            log.warn("CreationDate is null");
            return false;
        }

        return true;
    }

    @Override
    protected String getEntityType() {
        return "Invoice";
    }

    @Override
    protected String extractEventType(EventDTO<InvoiceSyncDto> event) {
        if (event == null) {
            return null;
        }

        return event.getType() != null ? event.getType() : null;
    }

    @Override
    protected String convertEventToJson(EventDTO<InvoiceSyncDto> event) {
        if (event == null) {
            return "{\"error\": \"Event is null\"}";
        }

        if (event.getData() == null) {
            return "{\"error\": \"Event data is null\", \"eventType\": \"" +
                    (event.getType() != null ? event.getType() : "null") + "\"}";
        }

        return JsonUtils.toJsonWithNullHandling(event.getData());
    }
}
