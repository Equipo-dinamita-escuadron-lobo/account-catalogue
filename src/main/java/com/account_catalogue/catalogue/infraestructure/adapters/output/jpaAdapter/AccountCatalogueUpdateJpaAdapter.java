package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountCatalogueUpdateJpaAdapter implements IAccountCatalogueUpdateOutputPort {


    private final  IAccountCatalogueRepository accountCatalogueRepository;

    private final IAccountCatalogueUpdateMapper accountCatalogueUpdateMapper;
    
    /**
     * Actualiza los detalles de un catálogo de cuentas existente en la base de datos.
     * 
     * @param id el ID del catálogo de cuentas a actualizar.
     * @param accountCatalogue el objeto AccountCatalogue que contiene los detalles
     *                         actualizados del catálogo de cuentas.
     * @return el objeto AccountCatalogue actualizado con los detalles
     *         actualizados del catálogo de cuentas, o null si el catálogo de
     *         cuentas no existe.
     */
        @Override
    public AccountCatalogue updateAccountCatalogue(long id, AccountCatalogue accountCatalogue) {
        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueRepository.findById(Long.valueOf(id)).orElse(null);

        if(accountCatalogueEntity==null){
            return null;
        }

        accountCatalogueEntity.setCode(accountCatalogue.getCode());
        accountCatalogueEntity.setDescription(accountCatalogue.getDescription());
        accountCatalogueEntity.setClassification(accountCatalogue.getClassification());
        accountCatalogueEntity.setFinancialStatus(accountCatalogue.getFinancialStatus());
        accountCatalogueEntity.setNature(accountCatalogue.getNature());
        accountCatalogueEntity.setCrossing(accountCatalogue.getCrossing());
        accountCatalogueEntity.setCostCenter(accountCatalogue.getCostCenter());
           
        accountCatalogueEntity = accountCatalogueRepository.save(accountCatalogueEntity);
        return accountCatalogueUpdateMapper.toAccountCatalogue(accountCatalogueEntity);
        
 
    }
}
