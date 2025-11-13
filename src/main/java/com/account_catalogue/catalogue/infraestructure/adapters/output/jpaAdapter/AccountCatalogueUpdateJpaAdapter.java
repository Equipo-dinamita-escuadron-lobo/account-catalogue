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

/**
 * @brief Adaptador JPA para operaciones de actualización con cambios jerárquicos
 *
 * Gestiona actualizaciones de cuentas con lógica compleja:
 * - Actualización de campos individuales de cuenta padre
 * - Cambio automático de códigos en jerarquía completa al cambiar código padre
 * - Propagación recursiva de cambios de código hacia descendientes
 */
@Component
@Data
@RequiredArgsConstructor
public class AccountCatalogueUpdateJpaAdapter implements IAccountCatalogueUpdateOutputPort {


    private final  IAccountCatalogueRepository accountCatalogueRepository;

    private final IAccountCatalogueUpdateMapper accountCatalogueUpdateMapper;
    
    /**
     * @brief Actualiza cuenta con propagación automática de cambios de código jerárquico
     *
     * Proceso complejo que incluye:
     * - Validación de existencia de cuenta
     * - Actualización de campos principales
     * - Detección de cambios de código
     * - Propagación recursiva de cambios de código a toda jerarquía descendiente
     * @param id identificador único de cuenta a actualizar
     * @param accountCatalogue datos actualizados para aplicar a la cuenta
     * @return cuenta actualizada con todos los cambios aplicados o null si no existe
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
     * @brief Propaga cambios de código padre a toda jerarquía descendiente
     *
     * Algoritmo recursivo que actualiza códigos de forma jerárquica:
     * - Reemplaza prefijo del código padre en todos los descendientes
     * - Mantiene estructura PUC (Plan Único de Cuentas) al cambiar códigos
     * - Actualiza recursivamente hacia abajo en la jerarquía
     * @param parentId ID del padre cuyos cambios de código se propagan
     * @param oldParentCode código anterior del padre para reemplazar
     * @param newParentCode código nuevo del padre para aplicar
     * @param idEnterprise filtro de empresa para aislamiento de datos
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
