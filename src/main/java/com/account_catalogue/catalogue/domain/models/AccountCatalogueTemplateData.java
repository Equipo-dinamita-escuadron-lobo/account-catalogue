package com.account_catalogue.catalogue.domain.models;

import lombok.Builder;
import lombok.Data;

/**
 * Modelo que representa una fila de datos en la plantilla de catálogo de cuentas
 */
@Data
@Builder
public class AccountCatalogueTemplateData {
    private String code;
    private String name;
    private String nature;
    private String financialStatus;
    private String classification;
    private Boolean cruce;
    private Boolean centroCosto;
}