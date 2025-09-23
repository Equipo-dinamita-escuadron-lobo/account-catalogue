package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.domain.models.Receipt;

public interface IReceiptProcessInputPort {
    /**
     * Procesa la lógica de negocio cuando se crea un nuevo recibo.
     * @param receipt El objeto de dominio que representa el recibo creado.
     */
    void processReceiptCreation(Receipt receipt);
    
    /**
     * Procesa la lógica de negocio cuando un recibo es anulado.
     * @param receipt El objeto de dominio que representa el recibo anulado.
     */
    void processReceiptVoid(Receipt receipt);
}
