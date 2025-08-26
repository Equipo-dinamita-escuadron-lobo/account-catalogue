package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueChangeStateInputPort {
    
    /**
     * Cambia el estado (activo/inactivo) de una cuenta del catálogo.
     * 
     * @param id el ID de la cuenta
     * @param idEnterprise el ID de la empresa
     * @param status el nuevo estado (true = activo, false = inactivo)
     * @return la cuenta actualizada
     */
    AccountCatalogue changeState(Long id, String idEnterprise, Boolean status);
}
