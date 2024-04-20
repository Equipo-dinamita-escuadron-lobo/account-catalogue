package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.impl.AccountCatalogueCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountCatalogueCreateJpaAdapter implements IAccountCatalogueCreateOutputPort {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    private final AccountCatalogueCreateMapper accountCatalogueCreateMapper;
    @Override
    public AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue) {
        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueCreateMapper.toEntity(accountCatalogue);
        if(accountCatalogueEntity==null){
            return null;
        }
        accountCatalogueEntity=accountCatalogueRepository.save(accountCatalogueEntity);
        return accountCatalogueCreateMapper.toModel(accountCatalogueEntity);
    }
}
