package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class AccountCatalogueUpdateJpaAdapter implements IAccountCatalogueUpdateOutputPort {


    private final  IAccountCatalogueRepository accountCatalogueRepository;

    private final IAccountCatalogueUpdateMapper accountCatalogueUpdateMapper;
    
    /**
     * Actualiza los detalles de un catálogo de cuentas existente en la base de datos.
     * Si el código cambia, actualiza recursivamente los códigos de todas las cuentas hijas.
     *
     * @param id el ID del catálogo de cuentas a actualizar.
     * @param accountCatalogue el objeto AccountCatalogue que contiene los detalles
     *                         actualizados del catálogo de cuentas.
     * @return el objeto AccountCatalogue actualizado con los detalles
     *         actualizados del catálogo de cuentas, o null si el catálogo de
     *         cuentas no existe.
     */
    @Override
    @Transactional
    public AccountCatalogue updateAccountCatalogue(long id, AccountCatalogue accountCatalogue) {
        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueRepository.findById(Long.valueOf(id)).orElse(null);

        if(accountCatalogueEntity == null){
            return null;
        }

        // Guardar el código anterior para comparar
        String oldCode = accountCatalogueEntity.getCode();
        String newCode = accountCatalogue.getCode();

        // Actualizar los campos de la cuenta padre
        accountCatalogueEntity.setCode(newCode);
        accountCatalogueEntity.setDescription(accountCatalogue.getDescription());
        accountCatalogueEntity.setClassification(accountCatalogue.getClassification());
        accountCatalogueEntity.setFinancialStatus(accountCatalogue.getFinancialStatus());
        accountCatalogueEntity.setNature(accountCatalogue.getNature());
        accountCatalogueEntity.setCrossing(accountCatalogue.getCrossing());
        accountCatalogueEntity.setCostCenter(accountCatalogue.getCostCenter());
        accountCatalogueEntity.setAmount(accountCatalogue.getAmount());
        accountCatalogueEntity = accountCatalogueRepository.save(accountCatalogueEntity);

        // Si el código cambió, actualizar códigos de hijos en cascada
        if (!oldCode.equals(newCode)) {
            updateChildrenCodes(accountCatalogueEntity.getId(), oldCode, newCode, accountCatalogue.getIdEnterprise());
        }

        return accountCatalogueUpdateMapper.toAccountCatalogue(accountCatalogueEntity);
    }

    /**
     * Actualiza recursivamente los códigos de todas las cuentas hijas cuando cambia el código del padre.
     * Reemplaza el prefijo del código padre antiguo por el nuevo en todos los descendientes.
     *
     * @param parentId ID de la cuenta padre cuyo código cambió
     * @param oldParentCode Código antiguo del padre
     * @param newParentCode Código nuevo del padre
     * @param idEnterprise ID de la empresa
     */
    private void updateChildrenCodes(Long parentId, String oldParentCode, String newParentCode, String idEnterprise) {
        // Obtener todos los hijos directos del padre
        List<AccountCatalogueEntity> children = accountCatalogueRepository.findByParentIdAndIdEnterprise(parentId, idEnterprise);

        for (AccountCatalogueEntity child : children) {
            String oldChildCode = child.getCode();

            // Verificar que el código del hijo comience con el código del padre antiguo
            if (oldChildCode.startsWith(oldParentCode)) {
                // Reemplazar el prefijo del código padre antiguo por el nuevo
                String newChildCode = newParentCode + oldChildCode.substring(oldParentCode.length());

                // Actualizar el código del hijo
                child.setCode(newChildCode);
                accountCatalogueRepository.save(child);

                // Actualizar recursivamente los códigos de los descendientes de este hijo
                updateChildrenCodes(child.getId(), oldChildCode, newChildCode, idEnterprise);
            }
        }
    }
}
