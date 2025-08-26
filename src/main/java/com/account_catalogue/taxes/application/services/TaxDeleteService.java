package com.account_catalogue.taxes.application.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.taxes.application.input.ITaxDeleteInputPort;
import com.account_catalogue.taxes.application.output.ITaxDeleteOutputPort;
import jakarta.transaction.Transactional;

@Service
@AllArgsConstructor
public class TaxDeleteService implements ITaxDeleteInputPort {
    private final ITaxDeleteOutputPort taxDeleteOutputPort;
    private final TaxValidationService taxValidationService;

    /**
     * Elimina (soft delete) un impuesto por su ID y empresa.
     * Valida que el impuesto exista para la empresa específica antes de eliminarlo.
     *
     * @param id ID del impuesto a eliminar
     * @param idEnterprise ID de la empresa
     * @return true si se eliminó con éxito, false de lo contrario
     */
    @Override
    @Transactional
    public boolean deleteByCode(long id, String idEnterprise) {
        // Validar que el impuesto existe para la empresa específica
        taxValidationService.validateTaxExists(Long.valueOf(id), idEnterprise);
        
        // Realizar el soft delete
        return taxDeleteOutputPort.deleteByCode(id, idEnterprise);
    }
}
