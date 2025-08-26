package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import lombok.Data;
import org.springframework.stereotype.Component;
/**
 * Adaptador para la creacion de cuentas usando JPA.
 * Implementa la interfaz IAccountCatalogueCreateOutputPort.
 */
@Component
@Data
public class AccountCatalogueCreateJpaAdapter implements IAccountCatalogueCreateOutputPort {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    private final IAccountCatalogueCreateMapper accountCatalogueCreateMapper;

    /**
     * Crea una cuenta
     *
     * @param accountCatalogue de la clase de dominio AccountCatalogue
     * @return el modelo de dominio del catalogo de cuenta
     */
    
    @Override
    public AccountCatalogue createAccountCatalogue(AccountCatalogue accountCatalogue) {

        AccountCatalogueEntity parent = null;
        if (accountCatalogue.getParent() != null) {
             parent = accountCatalogueRepository.findByCode(accountCatalogue.getParent().getCode(), accountCatalogue.getIdEnterprise());       
        }
        

        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueCreateMapper.toEntity(accountCatalogue, parent);
        if(accountCatalogueEntity==null){
            return null;
        }
        if(accountCatalogueRepository.findByCode(accountCatalogueEntity.getCode(), accountCatalogueEntity.getIdEnterprise())==null){
            accountCatalogueEntity=accountCatalogueRepository.save(accountCatalogueEntity);
        }else{
            accountCatalogueEntity=accountCatalogueRepository.findByCode(accountCatalogueEntity.getCode(),accountCatalogueEntity.getIdEnterprise());
        }
        return accountCatalogueCreateMapper.toModel(accountCatalogueEntity);
    }
}
