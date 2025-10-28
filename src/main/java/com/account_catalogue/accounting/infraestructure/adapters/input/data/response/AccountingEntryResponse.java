package com.account_catalogue.accounting.infraestructure.adapters.input.data.response;

import java.time.LocalDate;
import java.util.List;

import com.account_catalogue.accounting.domain.enums.AccountingEntryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountingEntryResponse {
    private Long id;
    private String code;
    private LocalDate date;
    private String description;
    private AccountingEntryStatus status; // Jackson serializará el Enum a un String (ej. "ACTIVE")
    private Long sourceDocumentId;

    // La lista debe ser del tipo DTO para evitar exponer el modelo de dominio
    // y para prevenir referencias circulares en la serialización JSON.
    private List<AccountingMovementResponse> movements;
}
