package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Genera nombres de archivo para exportaciones de catálogo de cuentas.
 */
@Slf4j
@Component
public class AccountCatalogueExcelFileNameGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Genera nombre de archivo para plantilla de catálogo de cuentas.
     *
     * @return nombre del archivo con timestamp
     */
    public String generateTemplateFileName() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        return "Plantilla_Catalogo_Cuentas_" + timestamp + ".xlsx";
    }

    /**
     * Genera nombre de archivo para exportación de catálogo de cuentas.
     *
     * @param entId ID de la entidad
     * @param companyName nombre de la empresa (opcional)
     * @param status estado de filtrado (opcional: true=activos, false=inactivos)
     * @return nombre del archivo con timestamp
     */
    public String generateExportFileName(String entId, String companyName, Boolean status) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String companySuffix = (companyName != null && !companyName.trim().isEmpty())
            ? "_" + companyName.replaceAll("[^a-zA-Z0-9]", "_")
            : "";

        String statusSuffix = "";
        if (status != null) {
            if (status) {
                statusSuffix = "_activos";
            } else {
                statusSuffix = "_inactivos";
            }
        }

        return "Catalogo_Cuentas" + companySuffix + statusSuffix + "_" + timestamp + ".xlsx";
    }
}