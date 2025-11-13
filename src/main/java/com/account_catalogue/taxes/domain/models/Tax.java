package com.account_catalogue.taxes.domain.models;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import lombok.*;

/**
 * @brief Modelo de dominio que representa un impuesto
 *
 * Contiene la información esencial de un impuesto incluyendo
 * cuentas contables de venta y compra asociadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tax {
    private Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private Double interest;
    private AccountCatalogueEntity salesTax;
    private AccountCatalogueEntity purchaseTax;
    private Boolean status;
}
