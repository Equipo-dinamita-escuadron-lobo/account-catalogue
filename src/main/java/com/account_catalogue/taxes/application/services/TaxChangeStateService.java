package com.account_catalogue.taxes.application.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.taxes.application.input.ITaxChangeStateInputPort;
import com.account_catalogue.taxes.application.output.ITaxChangeStateOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaxChangeStateService implements ITaxChangeStateInputPort {

    private final ITaxChangeStateOutputPort taxChangeStateOutputPort;
    private final TaxValidationService validationService;

    /**
     * Cambia el estado (activo/inactivo) de un impuesto.
     * Valida que el impuesto exista antes de cambiar su estado.
     * 
     * @param id el ID del impuesto
     * @param idEnterprise el ID de la empresa
     * @param status el nuevo estado (true = activo, false = inactivo)
     * @return el impuesto actualizado
     */
    @Transactional
    @Override
    public Tax changeState(Long id, String idEnterprise, Boolean status) {
        // Validar que el impuesto existe para la empresa específica
        validationService.validateTaxExists(id);
        
        // Cambiar el estado
        return taxChangeStateOutputPort.changeState(id, status);
    }
}
