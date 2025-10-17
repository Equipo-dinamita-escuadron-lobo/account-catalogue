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
            
            activateParentHierarchy(accountCatalogueEntity);
        }else{
            accountCatalogueEntity=accountCatalogueRepository.findByCode(accountCatalogueEntity.getCode(),accountCatalogueEntity.getIdEnterprise());
        }
        return accountCatalogueCreateMapper.toModel(accountCatalogueEntity);
    }

    /**
     * Activa recursivamente toda la jerarquía de padres de una cuenta.
     *
     * @param child la cuenta hija desde donde se inicia la activación ascendente
     */
    private void activateParentHierarchy(AccountCatalogueEntity child) {
        String currentCode = child.getCode();

        // Calcular y activar recursivamente todos los padres basándose en el código
        while (currentCode.length() > 1) {
            String parentCode = getParentCode(currentCode);
            if (parentCode != null) {
                AccountCatalogueEntity parent = accountCatalogueRepository.findByCode(parentCode, child.getIdEnterprise());
                if (parent != null && !parent.getStatus()) {
                    parent.setStatus(true);
                    accountCatalogueRepository.save(parent);
                }
                currentCode = parentCode;
            } else {
                break;
            }
        }
    }

    /**
     * Obtiene el código del padre para una cuenta dada basado en las reglas PUC.
     *
     * @param code El código de la cuenta.
     * @return El código del padre o null si no tiene padre.
     */
    private String getParentCode(String code) {
        switch (code.length()) {
            case 1: return null; // Clase, no tiene padre
            case 2: return code.substring(0, 1); // Grupo -> Clase
            case 4: return code.substring(0, 2); // Cuenta -> Grupo
            case 6: return code.substring(0, 4); // Subcuenta -> Cuenta
            case 8: return code.substring(0, 6); // Auxiliar -> Subcuenta
            default: return null;
        }
    }
}
