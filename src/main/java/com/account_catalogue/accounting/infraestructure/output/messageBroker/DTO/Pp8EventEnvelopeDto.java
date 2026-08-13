package com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public record Pp8EventEnvelopeDto(
        String eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        String tenantId,
        String enterpriseId,
        String correlationId,
        SourceDocument sourceDocument,
        JsonNode payload) {
    public record SourceDocument(String type, Long id) {}
}
