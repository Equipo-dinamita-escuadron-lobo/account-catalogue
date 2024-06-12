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
        String codeAux;

        TaxEntity taxEntity=taxRepository.findByCode(code);


        AccountCatalogueEntity depositAccount;
        AccountCatalogueEntity refundAccount;

        if(taxEntity==null){
            return null;
        }

        if(!taxEntity.getDepositAccount().getCode().equals(taxDTO.getDepositAccount())) {

            taxEntity.setDepositAccount(accountCatalogueRepository.findByCode(taxDTO.getRefundAccount()));
        }
        if(!taxEntity.getRefundAccount().getCode().equals(taxDTO.getRefundAccount())){
            taxEntity.setRefundAccount(accountCatalogueRepository.findByCode(taxDTO.getRefundAccount()));
        }

       if(!taxEntity.getCode().equals(taxDTO.getCode())){

            taxEntity.setCode(taxDTO.getCode());
       }

        taxEntity.setDescription(taxDTO.getDescription());
        taxEntity.setInterest(taxDTO.getInterest());
        taxEntity=taxRepository.save(taxEntity);

        return taxUpdateMapper.toModel(taxEntity);
    }
}
