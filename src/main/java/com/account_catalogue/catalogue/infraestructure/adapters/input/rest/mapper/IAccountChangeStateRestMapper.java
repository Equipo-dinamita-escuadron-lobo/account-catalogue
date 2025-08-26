package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueChangeStateRes;

public interface IAccountChangeStateRestMapper {
    
    /**
     * Convierte un objeto AccountCatalogue a AccountCatalogueChangeStateRes.
     * 
     * @param accountCatalogue el objeto de dominio
     * @return el DTO de respuesta
     */
    default AccountCatalogueChangeStateRes toChangeStateResponse(AccountCatalogue accountCatalogue) {
        if (accountCatalogue == null) {
            return null;
        }

        String statusMessage = accountCatalogue.getStatus() ? "Cuenta activada exitosamente" : "Cuenta desactivada exitosamente";

        return AccountCatalogueChangeStateRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .status(accountCatalogue.getStatus())
                .message(statusMessage)
                .build();
    }
}
