package com.account_catalogue.unitAudit.AuditCommon;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;

import com.account_catalogue.commons.audit.builder.OperationEventDto;
import com.account_catalogue.commons.audit.publisher.AuditEventPublisher;
import com.account_catalogue.commons.config.RabbitAuditPublisherConfig;

@ExtendWith(MockitoExtension.class)
class AuditEventPublisherTest {

    @Mock
    private AmqpTemplate amqpTemplate;
    @InjectMocks
    private AuditEventPublisher publisher;

    @Test
    @DisplayName("publish - Debe llamar a convertAndSend con exchange, routing key y dto correctos")
    void publish_llamaConvertAndSend() {
        OperationEventDto dto = buildDto();

        publisher.publish(dto);

        verify(amqpTemplate).convertAndSend(
                RabbitAuditPublisherConfig.AUDIT_EXCHANGE,
                RabbitAuditPublisherConfig.OPERATION_EVENT_ROUTING_KEY,
                dto);
    }

    @Test
    @DisplayName("publish - No debe propagar excepción si RabbitMQ falla")
    void publish_errorRabbit_noLanzaExcepcion() {
        OperationEventDto dto = buildDto();
        doThrow(new AmqpException("Conexión perdida"))
                .when(amqpTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // La auditoría nunca debe romper el flujo del negocio
        assertDoesNotThrow(() -> publisher.publish(dto));
    }

    @Test
    @DisplayName("publish - Debe llamar a convertAndSend exactamente una vez")
    void publish_llamaExactamenteUnaVez() {
        OperationEventDto dto = buildDto();

        publisher.publish(dto);

        verify(amqpTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    private OperationEventDto buildDto() {
        return OperationEventDto.builder()
                .enterpriseId("ENT-001")
                .userId("user-123")
                .userName("jdoe")
                .operationType("CREATE")
                .operationAt(Instant.now())
                .moduleName("BANKS")
                .affectedTable("BANK")
                .registerId("1")
                .dataObject(java.util.Map.of("entity", java.util.Map.of("id", 1L)))
                .build();
    }
}
