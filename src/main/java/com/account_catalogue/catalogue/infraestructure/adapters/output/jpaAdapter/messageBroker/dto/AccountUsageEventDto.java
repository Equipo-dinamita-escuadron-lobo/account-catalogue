package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.messageBroker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO para eventos de uso de cuentas contables
 *
 * Contiene la información necesaria para actualizar el contador de uso
 * cuando otros servicios notifican que han utilizado una cuenta contable.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountUsageEventDto {
    private Long accountCatalogueId;
    private String enterpriseId;
    private Integer quantityUsed;
}

