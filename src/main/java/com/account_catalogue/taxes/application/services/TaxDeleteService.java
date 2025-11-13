package com.account_catalogue.taxes.application.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.taxes.application.input.ITaxDeleteInputPort;
import com.account_catalogue.taxes.application.output.ITaxDeleteOutputPort;
import jakarta.transaction.Transactional;

/**
 * @brief Servicio de aplicación para eliminación de impuestos
 *
 * Implementa la lógica de negocio para eliminar impuestos mediante soft delete
 * con validaciones de existencia previas.
 */
@Service
@AllArgsConstructor
public class TaxDeleteService implements ITaxDeleteInputPort {
    private final ITaxDeleteOutputPort taxDeleteOutputPort;
    private final TaxValidationService taxValidationService;

    /**
     * @brief Elimina impuesto mediante soft delete con validación previa
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
