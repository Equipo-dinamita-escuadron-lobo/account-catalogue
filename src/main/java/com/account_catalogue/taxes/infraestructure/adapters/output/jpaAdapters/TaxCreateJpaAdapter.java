package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import com.account_catalogue.commons.exceptions.taxes.InvalidDepositAccountException;
import com.account_catalogue.commons.exceptions.taxes.InvalidRefundAccountException;
import com.account_catalogue.taxes.application.output.ITaxCreateOutputPort;
import com.account_catalogue.taxes.application.services.TaxValidationService;
import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxCreateMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxCreateJpaAdapter implements ITaxCreateOutputPort {

    private final ITaxCreateMapper taxCreateMapper;
    private final ITaxRepository taxRepository;
    private final IAccountCatalogueRepository accountCatalogueRepository;
    private final TaxValidationService taxValidationService;

    /**
     * Crea una nueva entrada de impuesto en el sistema.
     *
     * Este método verifica si ya existe un impuesto con el código dado para la
     * empresa especificada. Si es así, se lanza una TaxAlreadyExistsException. También
     * verifica la existencia de las cuentas de depósito y reembolso asociadas con el
     * impuesto.
     * Si alguna de estas cuentas no existe, se lanza una InvalidDepositAccountException o InvalidRefundAccountException.
     *
     * Después de la validación, se crea un nuevo TaxEntity y se guarda en el
     * repositorio.
     *
     * @param tax el objeto TaxDTO que contiene los detalles del impuesto a crear
     * @return el objeto Tax creado
     * @throws TaxAlreadyExistsException si el código de impuesto ya existe para la
     *                                  empresa,
     *                                  o si las cuentas de depósito o reembolso no
     *                                  existen
     */
    @Override
    public Tax createTax(TaxDTO tax) {
        // Validaciones usando el servicio centralizado
        taxValidationService.validateTaxCodeNotExists(tax.getCode(), tax.getIdEnterprise());
        taxValidationService.validateAccountDigits(tax.getDepositAccount(), tax.getRefundAccount());

        AccountCatalogueEntity depositAccount = accountCatalogueRepository.findByCode(tax.getDepositAccount(),
                tax.getIdEnterprise());
        if (depositAccount == null) {
            throw new InvalidDepositAccountException("No existe la cuenta que seleccionaste.");
        }

        AccountCatalogueEntity refundAccount = accountCatalogueRepository.findByCode(tax.getRefundAccount(),
                tax.getIdEnterprise());
        if (refundAccount == null) {
            throw new InvalidRefundAccountException("No existe la cuenta de devolución que seleccionaste.");
        }

        Tax taxAux = Tax.builder()
                .code(tax.getCode())
                .idEnterprise(tax.getIdEnterprise())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(refundAccount)
                .depositAccount(depositAccount)
                .build();

        TaxEntity taxEntity = taxCreateMapper.toEntity(taxAux);
        depositAccount.getDepositAccounts().add(taxEntity);
        refundAccount.getRefundAccounts().add(taxEntity);
        taxEntity = taxRepository.save(taxEntity);
        return taxCreateMapper.toModel(taxEntity);
    }
}
