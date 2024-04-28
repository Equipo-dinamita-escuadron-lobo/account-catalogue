package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountCatalogueDeleteJpaAdapter implements IAccountCatalogueDeleteOutputPort {

    private final IAccountCatalogueRepository accountCatalogueRepository;
    @Override
    public void deleteByCode(String code) {
       accountCatalogueRepository.deleteByCode(code);
    }
}
