package com.account_catalogue.taxes.application.input;

import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxChangeStateInputPort {
    
    /**
     * Cambia el estado (activo/inactivo) de un impuesto.
     * 
     * @param id el ID del impuesto
     * @param idEnterprise el ID de la empresa
     * @param status el nuevo estado (true = activo, false = inactivo)
     * @return el impuesto actualizado
     */
    Tax changeState(Long id, String idEnterprise, Boolean status);
}
