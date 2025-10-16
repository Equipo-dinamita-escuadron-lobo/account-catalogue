package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import com.account_catalogue.commons.exceptions.taxes.TaxAlreadyExistsException;
import com.account_catalogue.taxes.application.output.ITaxUpdateOutputPort;
import com.account_catalogue.taxes.application.services.TaxValidationService;
import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxUpdateMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxUpdateJpaAdapter implements ITaxUpdateOutputPort {

    private final ITaxRepository taxRepository;

    private final ITaxUpdateMapper taxUpdateMapper;

    private final IAccountCatalogueRepository accountCatalogueRepository;

    private final TaxValidationService taxValidationService;

    /**
     * Actualiza los detalles de un impuesto.
     *
     * @param taxDTO el objeto TaxDTO que contiene los detalles del impuesto a
     *               actualizar.
     * @param id     el identificador del impuesto a actualizar.
     * @return el objeto Tax actualizado.
     * @throws TaxAlreadyExistsException si el impuesto con el código especificado
     *                                  ya existe.
     */
    @Override
    public Tax update(TaxDTO taxDTO, long id) {
        AccountCatalogueEntity depositAccount;
        AccountCatalogueEntity refundAccount;
        TaxEntity taxEntity = taxRepository.findByIdActive(id);

        if (taxEntity == null) {
            return null;
        }


        taxValidationService.validateAccountDigits(taxDTO.getDepositAccountId(), taxDTO.getRefundAccountId(), taxDTO.getIdEnterprise());

        if (!taxEntity.getDepositAccount().getId().equals(taxDTO.getDepositAccountId())) {
            depositAccount = accountCatalogueRepository.findByIdAndIdEnterprise(taxDTO.getDepositAccountId(),
                    taxDTO.getIdEnterprise());

            taxEntity.setDepositAccount(depositAccount);
        }

        if (!taxEntity.getRefundAccount().getId().equals(taxDTO.getRefundAccountId())) {
            refundAccount = accountCatalogueRepository.findByIdAndIdEnterprise(taxDTO.getRefundAccountId(), taxDTO.getIdEnterprise());

            taxEntity.setRefundAccount(refundAccount);
        }

        if (!taxEntity.getCode().equals(taxDTO.getCode())) {
            taxValidationService.validateTaxCodeNotExists(taxDTO.getCode(), taxDTO.getIdEnterprise());
            taxEntity.setCode(taxDTO.getCode());
        }

        taxEntity.setDescription(taxDTO.getDescription());
        taxEntity.setInterest(taxDTO.getInterest());
        taxEntity = taxRepository.save(taxEntity);

        return taxUpdateMapper.toModel(taxEntity);
    }
}
