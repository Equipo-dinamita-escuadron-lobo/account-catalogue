package com.account_catalogue.accounting.infraestructure.output.messageBroker.listener;

import com.account_catalogue.accounting.application.service.TreasuryAccountingService;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.infraestructure.config.TreasuryRabbitConfig;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.AccountingResultEventDto;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PayableWriteOffEventDto;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PaymentVoucherEventDto;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.Pp8EventEnvelopeDto;
import com.account_catalogue.commons.config.aspect.JwtTokenService;
import com.account_catalogue.commons.multitenancy.utils.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.LongString;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TreasuryAccountingListener {
    private final TreasuryAccountingService service;
    private final ObjectMapper mapper;
    private final RabbitTemplate rabbit;
    private final JwtTokenService jwtTokenService;

    public TreasuryAccountingListener(TreasuryAccountingService service, ObjectMapper mapper,
            @Qualifier("rabbitTemplate") RabbitTemplate rabbit, JwtTokenService jwtTokenService) {
        this.service = service;
        this.mapper = mapper;
        this.rabbit = rabbit;
        this.jwtTokenService = jwtTokenService;
    }

    @RabbitListener(queues = TreasuryRabbitConfig.TREASURY_QUEUE,
            containerFactory = "treasuryRabbitListenerContainerFactory")
    public void listen(Message message) throws Exception {
        String headerTenant = requiredHeader(message, "x-tenant-id");
        Pp8EventEnvelopeDto envelope = mapper.readValue(
                new String(message.getBody(), StandardCharsets.UTF_8), Pp8EventEnvelopeDto.class);
        if (!headerTenant.equals(envelope.tenantId())) {
            throw new SecurityException("Tenant de header y envelope no coincide");
        }
        TenantContext.setTenantId(headerTenant);
        try {
            process(envelope, headerTenant);
        } finally {
            TenantContext.clear();
        }
    }

    private void process(Pp8EventEnvelopeDto envelope, String tenantId) throws Exception {
        String type = envelope.eventType();
        String operation = type.contains("VOID") ? "VOID" : "CREATE";
        try {
            AccountingEntry entry;
            String documentType;
            Long documentId;
            if (type.startsWith("PAYMENT_VOUCHER")) {
                PaymentVoucherEventDto event = mapper.treeToValue(envelope.payload(), PaymentVoucherEventDto.class);
                validatePayloadTenant(tenantId, event.tenantId());
                documentType = "PAYMENT_VOUCHER";
                documentId = event.id();
                entry = "VOID".equals(operation)
                        ? service.voidEntry(documentId, documentType)
                        : service.createVoucher(event);
            } else if (type.startsWith("PAYABLE_WRITEOFF")) {
                PayableWriteOffEventDto event = mapper.treeToValue(envelope.payload(), PayableWriteOffEventDto.class);
                validatePayloadTenant(tenantId, event.tenantId());
                documentType = "PAYABLE_WRITEOFF";
                documentId = event.id();
                entry = "VOID".equals(operation)
                        ? service.voidEntry(documentId, documentType)
                        : service.createWriteOff(event);
            } else {
                throw new IllegalArgumentException("Tipo de evento de Tesorería no soportado: " + type);
            }
            publish(envelope, operation, documentType, documentId, true, entry.getId(), null);
        } catch (IllegalArgumentException functionalError) {
            Long id = envelope.sourceDocument() == null ? null : envelope.sourceDocument().id();
            String documentType = envelope.sourceDocument() == null ? "UNKNOWN" : envelope.sourceDocument().type();
            publish(envelope, operation, documentType, id, false, null, functionalError.getMessage());
        }
    }

    private void publish(Pp8EventEnvelopeDto source, String operation, String documentType,
            Long documentId, boolean accepted, Long entryId, String reason) throws Exception {
        String resultEventId = "result-" + source.eventId();
        AccountingResultEventDto payload = new AccountingResultEventDto(
                resultEventId, source.eventId(), operation, documentType, documentId,
                accepted, entryId, reason, source.tenantId(), source.enterpriseId());
        var envelope = new java.util.LinkedHashMap<String, Object>();
        envelope.put("eventId", resultEventId);
        envelope.put("eventType", "ACCOUNTING_RESULT");
        envelope.put("eventVersion", 1);
        envelope.put("occurredAt", Instant.now());
        envelope.put("tenantId", source.tenantId());
        envelope.put("enterpriseId", source.enterpriseId());
        envelope.put("correlationId", source.correlationId());
        envelope.put("sourceDocument", java.util.Map.of("type", documentType, "id", documentId));
        envelope.put("payload", payload);

        CorrelationData correlation = new CorrelationData(resultEventId);
        rabbit.convertAndSend(TreasuryRabbitConfig.RESULT_EXCHANGE, "", envelope, message -> {
            message.getMessageProperties().setMessageId(resultEventId);
            message.getMessageProperties().setHeader("eventId", resultEventId);
            message.getMessageProperties().setHeader("eventType", "ACCOUNTING_RESULT");
            message.getMessageProperties().setHeader("tenantId", source.tenantId());
            message.getMessageProperties().setHeader("x-tenant-id", source.tenantId());
            message.getMessageProperties().setHeader("x-jwt-token", jwtTokenService.getToken());
            return message;
        }, correlation);
        CorrelationData.Confirm confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);
        if (!confirm.isAck()) throw new IllegalStateException("RabbitMQ rechazó el resultado contable: " + confirm.getReason());
        if (correlation.getReturned() != null) throw new IllegalStateException("RabbitMQ retornó el resultado contable sin ruta");
    }

    private void validatePayloadTenant(String expected, String actual) {
        if (!expected.equals(actual)) throw new SecurityException("Tenant de payload inconsistente");
    }

    private String requiredHeader(Message message, String name) {
        Object value = message.getMessageProperties().getHeaders().get(name);
        if (value instanceof LongString longString) return longString.toString();
        if (value instanceof String text && !text.isBlank()) return text;
        throw new SecurityException("Falta header Rabbit requerido: " + name);
    }
}
