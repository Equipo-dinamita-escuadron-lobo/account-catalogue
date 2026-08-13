package com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO;

public record AccountingResultEventDto(String eventId, String sourceEventId, String operation,
                                       String documentType, Long documentId, boolean accepted,
                                       Long accountingEntryId, String reason, String tenantId,
                                       String enterpriseId) {}
