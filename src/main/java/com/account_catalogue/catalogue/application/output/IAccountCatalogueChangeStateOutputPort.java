package com.account_catalogue.catalogue.application.output;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueChangeStateOutputPort {
    
    /**
     * Cambia el estado de una cuenta en la base de datos.
     * 
     * @param id el ID de la cuenta
     * @param status el nuevo estado
     * @return la cuenta actualizada
     */
    AccountCatalogue changeState(Long id, Boolean status);
}
