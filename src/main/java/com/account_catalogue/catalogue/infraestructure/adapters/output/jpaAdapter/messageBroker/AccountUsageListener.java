package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker.dto.AccountUsageEventDto;
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
public class AccountUsageListener extends AbstractMessageListener<EventDto<AccountUsageEventDto, EventUsageType>> {

    private final IAccountCatalogueUsagePort accountCatalogueUsagePort;

    /**
     * @brief Maneja eventos de uso de productos desde la cola
     * @param event Evento con información del producto usado
     */
    @RabbitListener(queues = RabbitAccountCatalogueConfig.ACCOUNT_USED_QUEUE)
    public void handleAccountUsageEvent(
            EventDto<AccountUsageEventDto, EventUsageType> event,
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
    protected boolean isValidEvent(EventDto<AccountUsageEventDto, EventUsageType> event) {
        if (event == null) {
            log.warn("Event is null");
            return false;
        }
        
        if (event.getData() == null) {
            log.warn("Event data is null");
            return false;
        }
        
        AccountUsageEventDto data = event.getData();
        
        if (data.getAccountCatalogueId() == null) {
            log.warn("AccountId is null - required field");
            return false;
        }
        
        if (data.getEnterpriseId() == null || data.getEnterpriseId().trim().isEmpty()) {
            log.warn("EnterpriseId is null or empty - required field");
            return false;
        }
        
        if (data.getQuantityUsed() == null || data.getQuantityUsed() <= 0) {
            log.warn("QuantityUsed is null or invalid - required field");
            return false;
        }
        
        return true;
    }

    @Override
    protected void processEvent(EventDto<AccountUsageEventDto, EventUsageType> event) {
        log.info("Received account usage event");
        
        try {
            if (!isValidEvent(event)) {
                log.warn("Invalid account usage event received");
                return;
            }
            
            AccountUsageEventDto data = event.getData();
            log.info("Processing usage for accountId: {}, enterpriseId: {}, quantity: {}",
                     data.getAccountCatalogueId(), data.getEnterpriseId(), data.getQuantityUsed());

            accountCatalogueUsagePort.incrementUsageCount(data.getAccountCatalogueId(), data.getEnterpriseId());

            log.info("Account usage event processed successfully for accountId: {} in enterprise: {}",
                     data.getAccountCatalogueId(), data.getEnterpriseId());
            
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
    protected String extractEventType(EventDto<AccountUsageEventDto, EventUsageType> event) {
        // Este método se requiere por la interfaz AbstractMessageListener
        // pero no se utiliza en el contexto de AccountUsageListener
        // ya que no se implementa manejo de errores en base de datos
        return null;
    }

    @Override
    protected String convertEventToJson(EventDto<AccountUsageEventDto, EventUsageType> event) {
        // Este método se requiere por la interfaz AbstractMessageListener
        // pero no se utiliza en el contexto de AccountUsageListener
        // ya que no se implementa manejo de errores en base de datos
        return "{}";
    }
}

