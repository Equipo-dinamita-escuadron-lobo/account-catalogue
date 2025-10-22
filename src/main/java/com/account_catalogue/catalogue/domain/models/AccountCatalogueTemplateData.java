package com.account_catalogue.catalogue.domain.models;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
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
    private NatureEnum nature;
    private FinancialStatusEnum financialStatus;
    private ClassificationEnum classification;
    private Boolean cruce;
    private Boolean centroCosto;
}