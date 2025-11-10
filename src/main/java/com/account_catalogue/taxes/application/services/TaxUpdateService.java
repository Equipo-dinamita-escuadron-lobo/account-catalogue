package com.account_catalogue.taxes.application.services;

import com.account_catalogue.taxes.application.input.ITaxUpdateInputPort;
import com.account_catalogue.taxes.application.output.ITaxUpdateOutputPort;
import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @brief Servicio de aplicación para actualización de impuestos
 *
 * Implementa la lógica de negocio para actualizar impuestos existentes
 * con validaciones de unicidad y cuentas contables.
 */
@Service
@AllArgsConstructor
@Data
public class TaxUpdateService implements ITaxUpdateInputPort {
    private final ITaxUpdateOutputPort taxUpdateOutputPort;
    private final TaxValidationService taxValidationService;

    /**
     * @brief Actualiza impuesto existente con validaciones
     * @param taxDTO datos actualizados del impuesto
     * @param id identificador del impuesto a actualizar
     * @return impuesto actualizado
     */
    @Override
    public Tax update(TaxDTO taxDTO, long id) {
        // Validar que el impuesto existe
        taxValidationService.validateTaxExists(id, taxDTO.getIdEnterprise());

        // Validar unicidad del código usando normalización, excluyendo el registro actual
        taxValidationService.validateTaxCodeNotExistsExcludingId(taxDTO.getCode(), taxDTO.getIdEnterprise(), id);

        // Validar cuentas de impuesto
        taxValidationService.validateAccountDigits(taxDTO.getSalesTaxId(), taxDTO.getPurchaseTaxId(), taxDTO.getIdEnterprise());
        taxValidationService.validateDifferentTaxAccounts(taxDTO.getSalesTaxId(), taxDTO.getPurchaseTaxId());

        return taxUpdateOutputPort.update(taxDTO, id);
    }
}
