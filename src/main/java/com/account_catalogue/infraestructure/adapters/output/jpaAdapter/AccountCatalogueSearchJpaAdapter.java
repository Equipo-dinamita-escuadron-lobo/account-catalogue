package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.projection.IAccountCatalogueInfoProjection;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class AccountCatalogueSearchJpaAdapter implements IAccountCatalogueSearchOutputPort {
    private final IAccountCatalogueRepository accountCatalogueRepository;
    private final IAccountCatalogueSearchMapper accountCatalogueSearchMapper;
    private final IItemAccountCatalogueSearchMapper itemAccountCatalogueSearchMapper;
    @Override
    public List<AccountCatalogueInfoDTO> getAllAccountCatalogue(String code) {
        List<IAccountCatalogueInfoProjection> accountCatalogue=accountCatalogueRepository.getAllAccountCatalogue(code);
        return accountCatalogueSearchMapper.toModelListAccountCatalogue(accountCatalogue);
    }

    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code) {
        AccountCatalogueEntity accountCatalogue=accountCatalogueRepository.findByCode(code);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }
}
