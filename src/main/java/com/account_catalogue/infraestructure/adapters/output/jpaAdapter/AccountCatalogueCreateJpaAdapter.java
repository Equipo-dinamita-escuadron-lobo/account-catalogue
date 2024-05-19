package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountCatalogueCreateJpaAdapter implements IAccountCatalogueCreateOutputPort {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    private final IAccountCatalogueCreateMapper accountCatalogueCreateMapper;
    
    @Override
    public AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue) {

        AccountCatalogueEntity parent = null;
        if (accountCatalogue.getParent() != null) {
             parent = accountCatalogueRepository.findByCode(accountCatalogue.getParent().getCode());        
        }
        

        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueCreateMapper.toEntity(accountCatalogue, parent);
        if(accountCatalogueEntity==null){
            return null;
        }
        if(accountCatalogueRepository.findByCode(accountCatalogueEntity.getCode())==null){
            accountCatalogueEntity=accountCatalogueRepository.save(accountCatalogueEntity);
        }else{
            accountCatalogueEntity=accountCatalogueRepository.findByCode(accountCatalogueEntity.getCode());
        }
        return accountCatalogueCreateMapper.toModel(accountCatalogueEntity);
    }
}
