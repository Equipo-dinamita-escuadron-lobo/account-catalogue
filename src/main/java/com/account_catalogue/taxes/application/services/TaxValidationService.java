package com.account_catalogue.taxes.application.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.commons.exceptions.taxes.InvalidAccountDigitsException;
import com.account_catalogue.commons.exceptions.taxes.TaxAlreadyExistsException;
import com.account_catalogue.commons.exceptions.taxes.TaxNotFoundException;
import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaxValidationService {

    private final ITaxSearchOutputPort taxSearchOutputPort;

    /**
     * Valida que las cuentas de depósito y devolución tengan exactamente 4 dígitos.
     * 
     * @param depositAccountCode código de cuenta de depósito
     * @param refundAccountCode código de cuenta de devolución
     * @throws InvalidAccountDigitsException si alguna cuenta no tiene 4 dígitos
     */
    public void validateAccountDigits(String depositAccountCode, String refundAccountCode) {
        if (depositAccountCode != null && depositAccountCode.trim().length() != 4) {
            throw new InvalidAccountDigitsException(
                "La cuenta de depósito '" + depositAccountCode + "' debe tener exactamente 4 dígitos"
            );
        }
        
        if (refundAccountCode != null && refundAccountCode.trim().length() != 4) {
            throw new InvalidAccountDigitsException(
                "La cuenta de devolución '" + refundAccountCode + "' debe tener exactamente 4 dígitos"
            );
        }
    }

    /**
     * Valida que no exista un impuesto con el mismo código para la empresa.
     * 
     * @param code código del impuesto
     * @param idEnterprise ID de la empresa
     * @throws TaxAlreadyExistsException si el impuesto ya existe
     */
    public void validateTaxCodeNotExists(String code, String idEnterprise) {
        Tax existingTax = taxSearchOutputPort.getTax(code, idEnterprise);
        if (existingTax != null) {
            throw new TaxAlreadyExistsException(
                "El impuesto con código '" + code + "' ya fue creado para esta empresa"
            );
        }
    }

    /**
     * Valida que exista un impuesto con el ID especificado.
     * 
     * @param id ID del impuesto
     * @throws TaxNotFoundException si el impuesto no existe
     */
    public void validateTaxExists(Long id) {
        Tax tax = taxSearchOutputPort.getTaxById(id);
        if (tax == null) {
            throw new TaxNotFoundException(
                "No se encontró un impuesto con el ID '" + id + "'"
            );
        }
    }

    /**
     * Valida que exista un impuesto con el código e idEnterprise especificados.
     * 
     * @param code código del impuesto
     * @param idEnterprise ID de la empresa
     * @throws TaxNotFoundException si el impuesto no existe
     */
    public void validateTaxExists(String code, String idEnterprise) {
        Tax tax = taxSearchOutputPort.getTax(code, idEnterprise);
        if (tax == null) {
            throw new TaxNotFoundException(
                "No se encontró un impuesto con código '" + code + "'"
            );
        }
    }

    /**
     * Valida que exista un impuesto con el ID e idEnterprise especificados.
     * Método optimizado para operaciones que requieren validar empresa.
     * 
     * @param id ID del impuesto
     * @param idEnterprise ID de la empresa
     * @throws TaxNotFoundException si el impuesto no existe
     */
    public void validateTaxExists(Long id, String idEnterprise) {
        Tax tax = taxSearchOutputPort.getTaxByIdAndEnterprise(id, idEnterprise);
        if (tax == null) {
            throw new TaxNotFoundException(
                "No se encontró un impuesto con ID '" + id + "'"
            );
        }
    }
}
