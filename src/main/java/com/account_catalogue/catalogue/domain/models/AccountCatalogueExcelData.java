package com.account_catalogue.catalogue.domain.models;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo que representa los datos de una cuenta contable parseados desde Excel.
 * Contiene los datos en formato raw antes de ser convertidos a entidades de dominio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCatalogueExcelData {

    /**
     * Número de fila en el Excel donde se encuentra este registro.
     */
    private Integer rowNumber;

    /**
     * Identificador de la empresa.
     */
    private String idEnterprise;

    /**
     * Código de la cuenta contable.
     */
    private String code;

    /**
     * Descripción/nombre de la cuenta.
     */
    private String description;

    /**
     * Naturaleza de la cuenta (Débito o Crédito).
     */
    private NatureEnum nature;

    /**
     * Estado financiero al que pertenece.
     */
    private FinancialStatusEnum financialStatus;

    /**
     * Clasificación de la cuenta.
     */
    private ClassificationEnum classification;

    /**
     * Indica si la cuenta permite cruce (solo para cuentas auxiliares de 8 dígitos).
     */
    private Boolean crossing;

    /**
     * Indica si la cuenta permite centro de costo (solo para cuentas auxiliares con Estado de Resultados).
     */
    private Boolean costCenter;

    /**
     * Verifica si los campos básicos requeridos están presentes.
     * Todos los campos son requeridos excepto crossing y costCenter.
     */
    public boolean hasRequiredFields() {
        return code != null && !code.trim().isEmpty()
                && description != null && !description.trim().isEmpty()
                && nature != null
                && financialStatus != null
                && classification != null;
    }

    /**
     * Verifica si es una cuenta auxiliar (8 dígitos).
     */
    public boolean isAuxiliaryAccount() {
        return code != null && code.trim().length() == 8;
    }

    /**
     * Obtiene una representación String del registro para logging y errores.
     */
    public String toLogString() {
        return String.format("Fila %d: Código=%s, Descripción=%s, Naturaleza=%s",
                rowNumber,
                code != null ? code : "N/A",
                description != null ? description : "N/A",
                nature != null ? nature.getState() : "N/A");
    }
}

