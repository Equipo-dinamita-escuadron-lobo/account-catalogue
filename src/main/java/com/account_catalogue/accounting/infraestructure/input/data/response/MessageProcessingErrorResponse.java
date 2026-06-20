package com.account_catalogue.accounting.infraestructure.input.data.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageProcessingErrorResponse {
    private Long id;
    private String eventType;
    private String errorDescription;
    private String messageData;
    private Instant errorTimestamp;
    private String entityType;
}
