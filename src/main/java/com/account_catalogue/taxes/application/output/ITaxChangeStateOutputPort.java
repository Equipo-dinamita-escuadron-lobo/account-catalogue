package com.account_catalogue.taxes.application.output;

import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxChangeStateOutputPort {
    
    /**
     * Cambia el estado de un impuesto en la base de datos.
     * 
     * @param id el ID del impuesto
     * @param status el nuevo estado
     * @return el impuesto actualizado
     */
    Tax changeState(Long id, Boolean status);
}
