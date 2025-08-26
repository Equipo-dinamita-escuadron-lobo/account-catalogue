package com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response.TaxChangeStateRes;

public interface ITaxChangeStateRestMapper {
    
    /**
     * Convierte un objeto Tax a TaxChangeStateRes.
     * 
     * @param tax el objeto de dominio
     * @return el DTO de respuesta
     */
    default TaxChangeStateRes toChangeStateResponse(Tax tax) {
        if (tax == null) {
            return null;
        }

        String statusMessage = tax.getStatus() ? "Impuesto activado exitosamente" : "Impuesto desactivado exitosamente";

        return TaxChangeStateRes.builder()
                .id(tax.getId())
                .code(tax.getCode())
                .description(tax.getDescription())
                .status(tax.getStatus())
                .message(statusMessage)
                .build();
    }
}
