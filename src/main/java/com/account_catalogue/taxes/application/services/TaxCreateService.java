package com.account_catalogue.taxes.application.services;

import com.account_catalogue.taxes.application.input.ITaxCreateInputPort;
import com.account_catalogue.taxes.application.output.ITaxCreateOutputPort;
import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
public class TaxCreateService implements ITaxCreateInputPort {
    private final ITaxCreateOutputPort taxCreateOutputPort;
    private final TaxValidationService taxValidationService;

    /**
     * Crea una nueva entrada de impuesto en el sistema.
     *
     * @param tax el objeto TaxDTO que contiene los detalles del impuesto a crear.
     * @return el objeto Tax creado.
     */
    @Override
    public Tax createTax(TaxDTO tax) {
        // Validar unicidad del código usando normalización
        taxValidationService.validateTaxCodeNotExists(tax.getCode(), tax.getIdEnterprise());

        // Validar cuentas de impuesto
        taxValidationService.validateAccountDigits(tax.getSalesTaxId(), tax.getPurchaseTaxId(), tax.getIdEnterprise());
        taxValidationService.validateDifferentTaxAccounts(tax.getSalesTaxId(), tax.getPurchaseTaxId());

        return taxCreateOutputPort.createTax(tax);
    }
}
