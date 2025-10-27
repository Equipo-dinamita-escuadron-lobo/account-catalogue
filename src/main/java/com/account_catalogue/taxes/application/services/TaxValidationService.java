package com.account_catalogue.taxes.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueInactiveException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueNotFoundException;
import com.account_catalogue.commons.exceptions.taxes.DuplicateTaxAccountsException;
import com.account_catalogue.commons.exceptions.taxes.InvalidAccountDigitsException;
import com.account_catalogue.commons.exceptions.taxes.TaxAlreadyExistsException;
import com.account_catalogue.commons.exceptions.taxes.TaxNotFoundException;
import com.account_catalogue.commons.utils.StringStandardizationUtils;
import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaxValidationService {

    private final ITaxSearchOutputPort taxSearchOutputPort;
    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;

    /**
     * Valida que existan las cuentas de impuesto de venta e impuesto de compra por sus IDs, que
     * tengan exactamente 8 dígitos y que estén activas.
     *
     * @param salesTaxId ID de cuenta de impuesto de venta
     * @param purchaseTaxId  ID de cuenta de impuesto de compra
     * @param idEnterprise     ID de la empresa
     * @throws AccountCatalogueNotFoundException si la cuenta de impuesto de venta o
     *                                           impuesto de compra no existe
     * @throws AccountCatalogueInactiveException si la cuenta está inactiva
     * @throws InvalidAccountDigitsException     si alguna cuenta no tiene 8 dígitos
     */
    public void validateAccountDigits(Long salesTaxId, Long purchaseTaxId, String idEnterprise) {
        if (salesTaxId != null) {
            AccountCatalogue salesTax = accountCatalogueSearchOutputPort
                    .getAccountCatalogueByIdAndIdEnterprise(salesTaxId, idEnterprise);
            if (salesTax == null) {
                throw new AccountCatalogueNotFoundException("La cuenta especificada no existe");
            }
            if (!Boolean.TRUE.equals(salesTax.getStatus())) {
                throw new AccountCatalogueInactiveException(
                        "La cuenta '" + salesTax.getCode() + "' está inactiva");
            }
            if (salesTax.getCode() == null || salesTax.getCode().trim().length() != 8) {
                throw new InvalidAccountDigitsException();
            }
        }

        if (purchaseTaxId != null) {
            AccountCatalogue purchaseTax = accountCatalogueSearchOutputPort
                    .getAccountCatalogueByIdAndIdEnterprise(purchaseTaxId, idEnterprise);
            if (purchaseTax == null) {
                throw new AccountCatalogueNotFoundException("La cuenta especificada no existe");
            }
            if (!Boolean.TRUE.equals(purchaseTax.getStatus())) {
                throw new AccountCatalogueInactiveException(
                        "La cuenta '" + purchaseTax.getCode() + "' está inactiva");
            }
            if (purchaseTax.getCode() == null || purchaseTax.getCode().trim().length() != 8) {
                throw new InvalidAccountDigitsException();
            }
        }
    }

    /**
     * Valida que una cuenta esté activa.
     * 
     * @param accountId    ID de la cuenta
     * @param idEnterprise ID de la empresa
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     * @throws AccountCatalogueInactiveException si la cuenta está inactiva
     */
    public void validateAccountActive(Long accountId, String idEnterprise) {
        if (accountId != null) {
            AccountCatalogue account = accountCatalogueSearchOutputPort
                    .getAccountCatalogueByIdAndIdEnterprise(accountId, idEnterprise);
            if (account == null) {
                throw new AccountCatalogueNotFoundException("La cuenta especificada no existe");
            }
            if (!Boolean.TRUE.equals(account.getStatus())) {
                throw new AccountCatalogueInactiveException("La cuenta '" + account.getCode() + "' está inactiva");
            }
        }
    }

    /**
     * Valida que no exista un impuesto con el mismo código para la empresa.
     * La comparación se realiza usando normalización (ignorando mayúsculas/minúsculas y espacios).
     *
     * @param code         código del impuesto
     * @param idEnterprise ID de la empresa
     * @throws TaxAlreadyExistsException si el impuesto ya existe
     */
    public void validateTaxCodeNotExists(String code, String idEnterprise) {
        if (code == null || code.trim().isEmpty()) {
            return; // No validar códigos vacíos o nulos
        }

        String normalizedCode = StringStandardizationUtils.standardizeCode(code);

        // Buscar todos los impuestos de la empresa y comparar códigos normalizados
        List<Tax> allTaxes = taxSearchOutputPort.getTaxesByEnterprise(idEnterprise);

        for (Tax tax : allTaxes) {
            if (tax.getCode() != null) {
                String existingNormalizedCode = StringStandardizationUtils.standardizeCode(tax.getCode());
                if (normalizedCode.equals(existingNormalizedCode)) {
                    throw new TaxAlreadyExistsException(
                            "Ya existe un impuesto con código '" + code + "'");
                }
            }
        }
    }

    /**
     * Valida que no exista un impuesto con el mismo código para la empresa, excluyendo un ID específico.
     * Usado para validación en edición. La comparación se realiza usando normalización.
     *
     * @param code         código del impuesto
     * @param idEnterprise ID de la empresa
     * @param excludeId    ID del impuesto a excluir de la validación
     * @throws TaxAlreadyExistsException si el impuesto ya existe
     */
    public void validateTaxCodeNotExistsExcludingId(String code, String idEnterprise, Long excludeId) {
        if (code == null || code.trim().isEmpty()) {
            return; // No validar códigos vacíos o nulos
        }

        String normalizedCode = StringStandardizationUtils.standardizeCode(code);

        // Buscar todos los impuestos de la empresa y comparar códigos normalizados
        List<Tax> allTaxes = taxSearchOutputPort.getTaxesByEnterprise(idEnterprise);

        for (Tax tax : allTaxes) {
            // Excluir el impuesto que se está editando
            if (!tax.getId().equals(excludeId) && tax.getCode() != null) {
                String existingNormalizedCode = StringStandardizationUtils.standardizeCode(tax.getCode());
                if (normalizedCode.equals(existingNormalizedCode)) {
                    throw new TaxAlreadyExistsException(
                            "Ya existe un impuesto con código '" + code + "'");
                }
            }
        }
    }

    /**
     * Valida que exista un impuesto con el código e idEnterprise especificados.
     * 
     * @param code         código del impuesto
     * @param idEnterprise ID de la empresa
     * @throws TaxNotFoundException si el impuesto no existe
     */
    public void validateTaxExists(String code, String idEnterprise) {
        Tax tax = taxSearchOutputPort.getTax(code, idEnterprise);
        if (tax == null) {
            throw new TaxNotFoundException(
                    "No se encontró un impuesto con código '" + code + "'");
        }
    }

    /**
     * Valida que exista un impuesto con el ID e idEnterprise especificados.
     * Método optimizado para operaciones que requieren validar empresa.
     * 
     * @param id           ID del impuesto
     * @param idEnterprise ID de la empresa
     * @throws TaxNotFoundException si el impuesto no existe
     */
    public void validateTaxExists(Long id, String idEnterprise) {
        Tax tax = taxSearchOutputPort.getTaxByIdAndEnterprise(id, idEnterprise);
        if (tax == null) {
            throw new TaxNotFoundException(
                    "No se encontró un impuesto con ID '" + id + "'");
        }
    }

    /**
     * Valida que las cuentas de impuesto de venta e impuesto de compra sean diferentes.
     *
     * @param salesTaxId ID de la cuenta de impuesto de venta
     * @param purchaseTaxId ID de la cuenta de impuesto de compra
     * @throws DuplicateTaxAccountsException si ambas cuentas son iguales
     */
    public void validateDifferentTaxAccounts(Long salesTaxId, Long purchaseTaxId) {
        if (salesTaxId != null && purchaseTaxId != null && salesTaxId.equals(purchaseTaxId)) {
            throw new DuplicateTaxAccountsException(
                    "Las cuentas de impuesto de venta e impuesto de compra deben ser diferentes");
        }
    }
}
