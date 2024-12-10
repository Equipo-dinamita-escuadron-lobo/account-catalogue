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

    /**
     * Obtiene un catálogo de cuentas por código y id de empresa.
     * 
     * @param code         el código del catálogo de cuentas
     * @param idEnterprise el id de la empresa
     * @return el catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByCode(code, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }

    /**
     * Obtiene el árbol del catálogo de cuentas por código y id de empresa.
     * 
     * @param code         el código del catálogo de cuentas
     * @param idEnterprise el id de la empresa
     * @return el árbol del catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByCode(code, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomainTree(accountCatalogue);
    }

    /**
     * Obtiene el catálogo de cuentas por ID.
     * 
     * @param id el ID del catálogo de cuentas
     * @return el catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueById(Long id) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findById(id).orElse(null);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }
}
