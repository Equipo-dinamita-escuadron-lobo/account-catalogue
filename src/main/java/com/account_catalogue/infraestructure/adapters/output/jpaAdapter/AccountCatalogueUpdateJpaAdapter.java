package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountCatalogueUpdateJpaAdapter implements IAccountCatalogueUpdateOutputPort {

    @Autowired
    private IAccountCatalogueRepository accountCatalogueRepository;

    @Autowired
    private IAccountCatalogueUpdateMapper accountCatalogueUpdateMapper;
    
    @Override
    public AccountCatalogue updateAccountCatalogue(long id, AccountCatalogue accountCatalogue) {
        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueRepository.findById(id);

        if(accountCatalogueEntity==null){
            return null;
        }

        accountCatalogueEntity.setCode(accountCatalogue.getCode());
        accountCatalogueEntity.setDescription(accountCatalogue.getDescription());
        accountCatalogueEntity.setClassification(accountCatalogue.getClassification());
        accountCatalogueEntity.setFinancialStatus(accountCatalogue.getFinancialStatus());
        accountCatalogueEntity.setNature(accountCatalogue.getNature());
           
        accountCatalogueEntity = accountCatalogueRepository.save(accountCatalogueEntity);
        return accountCatalogueUpdateMapper.toAccountCatalogue(accountCatalogueEntity);
        
 
    }
}
