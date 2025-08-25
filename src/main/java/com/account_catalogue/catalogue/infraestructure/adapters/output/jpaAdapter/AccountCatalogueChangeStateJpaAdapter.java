package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueChangeStateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AccountCatalogueChangeStateJpaAdapter implements IAccountCatalogueChangeStateOutputPort {

    private final IAccountCatalogueRepository accountCatalogueRepository;
    private final IAccountCatalogueUpdateMapper accountCatalogueUpdateMapper;

    /**
     * Cambia el estado de una cuenta en la base de datos.
     * 
     * @param id el ID de la cuenta
     * @param status el nuevo estado
     * @return la cuenta actualizada
     */
    @Override
    public AccountCatalogue changeState(Long id, Boolean status) {
        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueRepository.findById(id.longValue());
        
        if (accountCatalogueEntity == null) {
            return null;
        }

        accountCatalogueEntity.setStatus(status);
        accountCatalogueEntity = accountCatalogueRepository.save(accountCatalogueEntity);
        
        return accountCatalogueUpdateMapper.toAccountCatalogue(accountCatalogueEntity);
    }
}
