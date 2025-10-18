package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import java.util.List;
import java.util.stream.Collectors;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountCatalogueSearchJpaAdapter implements IAccountCatalogueSearchOutputPort {
    private final IAccountCatalogueRepository accountCatalogueRepository;
    private final IItemAccountCatalogueSearchMapper itemAccountCatalogueSearchMapper;

    /**
     * Obtiene un catálogo de cuentas por código y id de empresa (solo la cuenta, sin hijas).
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
     * Obtiene el árbol completo del catálogo de cuentas por código y id de empresa.
     * Incluye todas las cuentas hijas en la jerarquía.
     * 
     * @param code         el código del catálogo de cuentas
     * @param idEnterprise el id de la empresa
     * @return el árbol del catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueTreeByCode(String code, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByCode(code, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomainTree(accountCatalogue);
    }

    /**
     * Obtiene el catálogo de cuentas por ID y id de empresa (solo la cuenta, sin hijas).
     * 
     * @param id el ID del catálogo de cuentas
     * @param idEnterprise el id de la empresa
     * @return el catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByIdAndIdEnterprise(id, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }

    /**
     * Obtiene el catálogo de cuentas por ID y id de empresa (solo la cuenta, sin hijas).
     * Filtra por empresa.
     * 
     * @param id el ID del catálogo de cuentas
     * @param idEnterprise el id de la empresa
     * @return el catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueByIdAndIdEnterprise(Long id, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByIdAndIdEnterprise(id, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }

    /**
     * Obtiene el árbol completo del catálogo de cuentas por ID y id de empresa.
     * Incluye todas las cuentas hijas en la jerarquía.
     * 
     * 
     * @param id           el ID del catálogo de cuentas
     * @param idEnterprise el id de la empresa
     * @return el árbol del catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueTreeByIdAndIdEnterprise(Long id, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByIdAndIdEnterprise(id, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomainTree(accountCatalogue);
    }

    /**
     * Obtiene el catálogo de cuentas por descripción (case-insensitive) y id de empresa.
     * 
     * @param description  la descripción del catálogo de cuentas (case-insensitive)
     * @param idEnterprise el id de la empresa
     * @return el catálogo de cuentas si existe, null en caso contrario
     */
    @Override
    public AccountCatalogue getAccountCatalogueByDescriptionIgnoreCaseAndIdEnterprise(String description, String idEnterprise) {
        AccountCatalogueEntity accountCatalogue = accountCatalogueRepository.findByDescriptionIgnoreCaseAndIdEnterprise(description, idEnterprise);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }

    /**
     * Obtiene todas las cuentas auxiliares (8 dígitos) activas para una empresa específica.
     * 
     * @param idEnterprise el id de la empresa
     * @return lista de cuentas auxiliares ordenadas por código
     */
    @Override
    public List<AccountCatalogue> getAuxiliaryAccountsByIdEnterprise(String idEnterprise) {
        List<AccountCatalogueEntity> auxiliaryAccountsEntities = accountCatalogueRepository.findAuxiliaryAccountsByIdEnterprise(idEnterprise);
        
        return auxiliaryAccountsEntities.stream()
                .map(itemAccountCatalogueSearchMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las cuentas auxiliares (8 dígitos) activas que tienen el campo crossing activo para una empresa específica.
     * 
     * @param idEnterprise el id de la empresa
     * @return lista de cuentas auxiliares con crossing activo ordenadas por código
     */
    @Override
    public List<AccountCatalogue> getAuxiliaryAccountsWithCrossingByIdEnterprise(String idEnterprise) {
        List<AccountCatalogueEntity> auxiliaryAccountsWithCrossingEntities = accountCatalogueRepository.findAuxiliaryAccountsWithCrossingByIdEnterprise(idEnterprise);
        
        return auxiliaryAccountsWithCrossingEntities.stream()
                .map(itemAccountCatalogueSearchMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los catálogos de cuentas para una empresa específica con paginación.
     * Los resultados se ordenan por código para mantener la jerarquía.
     *
     * @param idEnterprise el ID de la empresa
     * @param pageable objeto de paginación con ordenamiento
     * @return página de catálogos de cuentas
     */
    @Override
    public Page<AccountCatalogue> getAllAccountCataloguesByIdEnterprise(String idEnterprise, Pageable pageable) {
        Page<AccountCatalogueEntity> entities = accountCatalogueRepository.findAllByIdEnterpriseOrderByCode(idEnterprise, pageable);
        return entities.map(itemAccountCatalogueSearchMapper::toDomain);
    }
}
