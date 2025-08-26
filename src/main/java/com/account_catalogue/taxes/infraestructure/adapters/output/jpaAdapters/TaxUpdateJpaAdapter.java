package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import com.account_catalogue.taxes.application.output.ITaxUpdateOutputPort;
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

    /**
     * Actualiza los detalles de un impuesto.
     *
     * @param taxDTO el objeto TaxDTO que contiene los detalles del impuesto a
     *               actualizar.
     * @param id     el identificador del impuesto a actualizar.
     * @return el objeto Tax actualizado.
     * @throws IllegalArgumentException si el impuesto con el código especificado
     *                                  ya existe, o si las cuentas de depósito o
     *                                  reembolso no existen.
     */
    @Override
    public Tax update(TaxDTO taxDTO, long id) {
        AccountCatalogueEntity depositAccount;
        AccountCatalogueEntity refundAccount;
        TaxEntity taxEntity = taxRepository.findByIdActive(id);

        if (taxEntity == null) {
            return null;
        }

        if (!taxEntity.getDepositAccount().getCode().equals(taxDTO.getDepositAccount())) {
            depositAccount = accountCatalogueRepository.findByCode(taxDTO.getDepositAccount(),
                    taxDTO.getIdEnterprise());
            if (depositAccount == null) {
                throw new IllegalArgumentException("No existe la cuenta de depósito que seleccionaste.");
            } else {
                taxEntity.setDepositAccount(depositAccount);
            }
        }

        if (!taxEntity.getRefundAccount().getCode().equals(taxDTO.getRefundAccount())) {
            refundAccount = accountCatalogueRepository.findByCode(taxDTO.getRefundAccount(), taxDTO.getIdEnterprise());
            if (refundAccount == null) {
                throw new IllegalArgumentException("No existe la cuenta de devolución que seleccionaste.");
            } else {
                taxEntity.setRefundAccount(refundAccount);
            }

        }

        if (!taxEntity.getCode().equals(taxDTO.getCode())) {
            if (taxRepository.existsByCode(taxDTO.getCode())) {
                throw new IllegalArgumentException("El impuesto con ese código ya existe.");
            } else {
                taxEntity.setCode(taxDTO.getCode());
            }

        }

        taxEntity.setDescription(taxDTO.getDescription());
        taxEntity.setInterest(taxDTO.getInterest());
        taxEntity = taxRepository.save(taxEntity);

        return taxUpdateMapper.toModel(taxEntity);
    }
}
