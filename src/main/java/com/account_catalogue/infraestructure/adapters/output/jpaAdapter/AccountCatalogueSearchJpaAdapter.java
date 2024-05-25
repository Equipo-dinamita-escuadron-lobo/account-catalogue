package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;


@Component
@Data
public class AccountCatalogueSearchJpaAdapter implements IAccountCatalogueSearchOutputPort {
    private final IAccountCatalogueRepository accountCatalogueRepository;
    private final IItemAccountCatalogueSearchMapper itemAccountCatalogueSearchMapper;


    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue=accountCatalogueRepository.findByCode(code, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }

    @Override
    public AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue=accountCatalogueRepository.findByCode(code, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomainTree(accountCatalogue);
    }
}
