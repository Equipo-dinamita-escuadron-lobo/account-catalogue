package com.chartaccounts.infraestructure.adapters.output.jpaAdapter;

import com.chartaccounts.application.output.IAccountCatalogueCreateOutputPort;
import com.chartaccounts.domain.models.AccountCatalogue;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.mapper.impl.AccountCatalogueCreateMapper;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
