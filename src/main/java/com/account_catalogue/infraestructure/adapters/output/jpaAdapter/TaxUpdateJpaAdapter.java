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
import org.springframework.stereotype.Component;


@Component
@Data
public class TaxUpdateJpaAdapter implements ITaxUpdateOutputPort {

     private final ITaxRepository taxRepository;

    private final  ITaxUpdateMapper taxUpdateMapper;


    private final IAccountCatalogueRepository accountCatalogueRepository;
    @Override
    public Tax update(TaxDTO taxDTO,long id) {
        AccountCatalogueEntity depositAccount;
        AccountCatalogueEntity refundAccount;
        TaxEntity taxEntity=taxRepository.findById(id).orElse(null);

        if(taxEntity==null){
           return null;
        }


        if(!taxEntity.getDepositAccount().getCode().equals(taxDTO.getDepositAccount())) {
            depositAccount=accountCatalogueRepository.findByCode(taxDTO.getDepositAccount(),taxDTO.getIdEnterprise());
           if(depositAccount==null){
               throw new IllegalArgumentException("No existe la cuenta de depósito que seleccionaste.");
           }else{
               taxEntity.setDepositAccount(depositAccount);
           }
        }

        if(!taxEntity.getRefundAccount().getCode().equals(taxDTO.getRefundAccount())){
           refundAccount=accountCatalogueRepository.findByCode(taxDTO.getRefundAccount(),taxDTO.getIdEnterprise());
          if(refundAccount==null){
              throw new IllegalArgumentException("No existe la cuenta de devolución que seleccionaste.");
          }else{
              taxEntity.setRefundAccount(refundAccount);
          }



        }



       if(!taxEntity.getCode().equals(taxDTO.getCode())){
            if(taxRepository.existsByCode(taxDTO.getCode())){
                throw new IllegalArgumentException("El impuesto con ese código ya existe.");
            }else{
                taxEntity.setCode(taxDTO.getCode());
            }

       }

        taxEntity.setDescription(taxDTO.getDescription());
        taxEntity.setInterest(taxDTO.getInterest());
        taxEntity=taxRepository.save(taxEntity);

        return taxUpdateMapper.toModel(taxEntity);
    }
}
