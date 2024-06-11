package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.ITaxUpdateOutputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxUpdateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.ITaxRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxUpdateJpaAdapter implements ITaxUpdateOutputPort {
    @Autowired
     private ITaxRepository taxRepository;
    @Autowired
    private ITaxUpdateMapper taxUpdateMapper;

    @Autowired
    private IAccountCatalogueRepository accountCatalogueRepository;
    @Override
    public Tax update(TaxDTO taxDTO,String code) {

        TaxEntity taxEntity=taxRepository.findByCode(code);

        AccountCatalogueEntity depositAccount;
        AccountCatalogueEntity refundAccount;

        if(taxEntity==null){
            return null;

        }
        if(taxEntity.getDepositAccount().getCode()!=taxDTO.getDepositAccount()){
                    depositAccount=accountCatalogueRepository.findByCode(taxDTO.getRefundAccount());
        }else{
            depositAccount=taxEntity.getDepositAccount();
        }
        if(taxEntity.getRefundAccount().getCode()!=taxDTO.getRefundAccount()){
            refundAccount =accountCatalogueRepository.findByCode(taxDTO.getRefundAccount());

        }else{
            refundAccount=taxEntity.getRefundAccount();
        }
           Tax tax= Tax.builder()
                .code(taxDTO.getCode())
                .description(taxDTO.getDescription())
                .interest(taxDTO.getInterest())
                .depositAccount(depositAccount)
                .refundAccount(refundAccount)
                .build();

        TaxEntity taxEntityAux=taxUpdateMapper.toEntity(tax);
        taxEntityAux=taxRepository.save(taxEntity);

        return taxUpdateMapper.toModel(taxEntityAux);
    }
}
