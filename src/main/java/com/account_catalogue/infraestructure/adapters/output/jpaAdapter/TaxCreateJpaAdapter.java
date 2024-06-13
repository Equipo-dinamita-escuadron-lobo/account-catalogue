package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.ITaxCreateOutputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.ITaxRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxCreateJpaAdapter implements ITaxCreateOutputPort {

    private final ITaxCreateMapper taxCreateMapper;

    private final ITaxRepository taxRepository;


    private final IAccountCatalogueRepository accountCatalogueRepository;
    @Override
    public Tax createTax(TaxDTO tax) {

    AccountCatalogueEntity depositAccount=accountCatalogueRepository.findByCode(tax.getDepositAccount(),tax.getIdEnterprise());
    AccountCatalogueEntity refundAccount=accountCatalogueRepository.findByCode(tax.getRefundAccount(),tax.getIdEnterprise());

        Tax  taxAux=Tax.builder()
                .code(tax.getCode())
                .idEnterprise(tax.getIdEnterprise())
                .description(tax.getDescription())
                .interest(tax.getInterest())
                .refundAccount(refundAccount)
                .depositAccount(depositAccount)
                .build();

        TaxEntity  taxEntity=taxCreateMapper.toEntity(taxAux);


        depositAccount.getDepositAccounts().add(taxEntity);
        refundAccount.getRefundAccounts().add(taxEntity);
        taxEntity=taxRepository.save(taxEntity);
        return taxCreateMapper.toModel(taxEntity);

    }
}
