package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker.dto.AccountUsedEventDto;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueUsagePort;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker.dto.EventDto;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker.enums.EventUsageType;
import com.account_catalogue.catalogue.infraestructure.config.rabbitConfig.RabbitAccountCatalogueConfig;
import com.account_catalogue.commons.config.base.AbstractMessageListener;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Listener para eventos de uso de productos desde PEPS
 *
 * Escucha eventos de RabbitMQ cuando PEPS notifica que ha utilizado un producto,
 * actualizando el contador de uso correspondiente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountUsageListener extends AbstractMessageListener<EventDto<AccountUsedEventDto, EventUsageType>> {

    private final IAccountCatalogueUsagePort accountCatalogueUsagePort;

    private static final String ACCOUNT_TYPE_ID = "ID";
    private static final String ACCOUNT_TYPE_CODE = "CODE";

    /**
     * @brief Maneja eventos de uso de productos desde la cola
     * @param event Evento con información del producto usado
     */
    @RabbitListener(queues = RabbitAccountCatalogueConfig.ACCOUNT_USED_QUEUE)
    public void handleAccountUsageEvent(
            EventDto<AccountUsedEventDto, EventUsageType> event,
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        handleMessage(event, channel, deliveryTag);
    }

    /**
     * @brief Valida la integridad del evento recibido
     * @param event Evento a validar
     * @return true si el evento es válido
     */
    protected boolean isValidEvent(EventDto<AccountUsedEventDto, EventUsageType> event) {
        if (event == null) {
            log.warn("Event is null");
            return false;
        }

        if (event.getData() == null) {
            log.warn("Event data is null");
            return false;
        }

        AccountUsedEventDto data = event.getData();

        if (data.getAccount() == null) {
            log.warn("Account is null - required field");
            return false;
        }

        if (data.getEnterpriseId() == null || data.getEnterpriseId().trim().isEmpty()) {
            log.warn("EnterpriseId is null or empty - required field");
            return false;
        }

        if (data.getSourceAccountType() == null || data.getSourceAccountType().trim().isEmpty()) {
            log.warn("SourceAccountType is null or empty - required field");
            return false;
        }

        if (!ACCOUNT_TYPE_ID.equalsIgnoreCase(data.getSourceAccountType()) && !ACCOUNT_TYPE_CODE.equalsIgnoreCase(data.getSourceAccountType())) {
            log.warn("SourceAccountType must be '{}' or '{}', but was: {}", ACCOUNT_TYPE_ID, ACCOUNT_TYPE_CODE, data.getSourceAccountType());
            return false;
        }

        return true;
    }

    @Override
    protected void processEvent(EventDto<AccountUsedEventDto, EventUsageType> event) {
        log.info("Received account usage event");

        try {
            if (!isValidEvent(event)) {
                log.warn("Invalid account usage event received");
                return;
            }

            AccountUsedEventDto data = event.getData();
            log.info("Processing usage for account: {} (type: {}) in enterprise: {}",
                     data.getAccount(), data.getSourceAccountType(), data.getEnterpriseId());

            accountCatalogueUsagePort.incrementUsageCount(data);

            log.info("Account usage event processed successfully for account: {} (type: {}) in enterprise: {}",
                     data.getAccount(), data.getSourceAccountType(), data.getEnterpriseId());

        } catch (Exception e) {
            log.error("Error processing account usage event: {}", e.getMessage(), e);
            // En caso de error, el mensaje se pierde intencionalmente para no bloquear la cola
            // Se podría implementar DLQ o reintentos según necesidades del negocio
        }
    }

    @Override
    protected String getEntityType() {
        return "AccountUsage";
    }

    @Override
    protected String extractEventType(EventDto<AccountUsedEventDto, EventUsageType> event) {
        // Este método se requiere por la interfaz AbstractMessageListener
        // pero no se utiliza en el contexto de AccountUsageListener
        // ya que no se implementa manejo de errores en base de datos
        return null;
    }

    @Override
    protected String convertEventToJson(EventDto<AccountUsedEventDto, EventUsageType> event) {
        // Este método se requiere por la interfaz AbstractMessageListener
        // pero no se utiliza en el contexto de AccountUsageListener
        // ya que no se implementa manejo de errores en base de datos
        return "{}";
    }
}

