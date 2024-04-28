package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;

@Component
@Data
public class AccountCatalogueUpdateJpaAdapter implements IAccountCatalogueUpdateOutputPort {
    private  final IAccountCatalogueRepository accountCatalogueRepository;
    private  final IAccountCatalogueUpdateMapper accountCatalogueUpdateMapper;
    @Override
    public AccountCatalogue updateAccountCatalogue(long id, AccountCatalogue accountCatalogue) {

        Optional<AccountCatalogueEntity> optionalAccount = accountCatalogueRepository.findById(id);
        if (optionalAccount.isPresent()) {
            AccountCatalogueEntity accountCatalogueEntity=optionalAccount.get();
            accountCatalogueEntity.setCode(accountCatalogue.getCode());
            accountCatalogueEntity.setDescription(accountCatalogue.getDescription());
            accountCatalogueEntity.setNature(accountCatalogue.getNature());
            accountCatalogueEntity.setFinancialStatus(accountCatalogue.getFinancialStatus());
            accountCatalogueEntity.setClassification(accountCatalogue.getClassification());

            AccountCatalogueEntity updateaccountCatalogueEntity = accountCatalogueRepository.save(accountCatalogueEntity);
            return accountCatalogueUpdateMapper.toAccountCatalogue( updateaccountCatalogueEntity);
        }
      return null;
    }
}
