package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
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
     * verifica la existencia y formato de las cuentas de depósito y reembolso asociadas con el
     * impuesto a través del servicio de validación.
     *
     * Después de la validación, se crea un nuevo TaxEntity y se guarda en el
     * repositorio.
     *
     * @param tax el objeto TaxDTO que contiene los detalles del impuesto a crear
     * @return el objeto Tax creado
     * @throws TaxAlreadyExistsException si el código de impuesto ya existe para la
     *                                  empresa
     */
    @Override
    public Tax createTax(TaxDTO tax) {
        // Validaciones usando el servicio centralizado
        taxValidationService.validateTaxCodeNotExists(tax.getCode(), tax.getIdEnterprise());
        taxValidationService.validateAccountDigits(tax.getSalesTaxId(), tax.getPurchaseTaxId(), tax.getIdEnterprise());
        taxValidationService.validateDifferentTaxAccounts(tax.getSalesTaxId(), tax.getPurchaseTaxId());

        AccountCatalogueEntity salesTax = accountCatalogueRepository.findByIdAndIdEnterprise(tax.getSalesTaxId(),
                tax.getIdEnterprise());


        AccountCatalogueEntity purchaseTax = accountCatalogueRepository.findByIdAndIdEnterprise(tax.getPurchaseTaxId(),
                tax.getIdEnterprise());


        Tax taxAux = Tax.builder()
                .code(tax.getCode())
                .idEnterprise(tax.getIdEnterprise())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .purchaseTax(purchaseTax)
                .salesTax(salesTax)
                .build();

        TaxEntity taxEntity = taxCreateMapper.toEntity(taxAux);
        salesTax.getSalesTaxes().add(taxEntity);
        purchaseTax.getPurchaseTaxes().add(taxEntity);
        taxEntity = taxRepository.save(taxEntity);
        return taxCreateMapper.toModel(taxEntity);
    }
}
