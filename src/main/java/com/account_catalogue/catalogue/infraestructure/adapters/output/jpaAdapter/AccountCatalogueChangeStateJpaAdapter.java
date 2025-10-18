package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
     * Cambia el estado de una cuenta en la base de datos y todos sus descendientes.
     *
     * @param id el ID de la cuenta
     * @param status el nuevo estado
     * @return la cuenta actualizada
     */
    @Override
    @Transactional
    public AccountCatalogue changeState(Long id, Boolean status) {
        AccountCatalogueEntity accountCatalogueEntity = accountCatalogueRepository.findById(id).orElse(null);

        if (accountCatalogueEntity == null) {
            return null;
        }

        // Cambiar el estado de la cuenta
        accountCatalogueEntity.setStatus(status);
        accountCatalogueEntity = accountCatalogueRepository.save(accountCatalogueEntity);

        if (status) {
            // Si se está activando, activar toda la jerarquía de padres
            activateParentHierarchy(accountCatalogueEntity);
        } else {
            // Si se está inactivando, inactivar todos los hijos recursivamente
            List<Long> descendantIds = accountCatalogueRepository.findDescendantIds(accountCatalogueEntity.getId(), accountCatalogueEntity.getIdEnterprise(), accountCatalogueEntity.getTenantId());
            if (!descendantIds.isEmpty()) {
                accountCatalogueRepository.updateStatusByIds(status, descendantIds, accountCatalogueEntity.getIdEnterprise(), accountCatalogueEntity.getTenantId());
            }
        }

        return accountCatalogueUpdateMapper.toAccountCatalogue(accountCatalogueEntity);
    }

    /**
     * Activa recursivamente toda la jerarquía de padres de una cuenta.
     *
     * @param child la cuenta hija desde donde se inicia la activación ascendente
     */
    private void activateParentHierarchy(AccountCatalogueEntity child) {
        if (child.getParent() != null) {
            AccountCatalogueEntity parent = child.getParent();

            // Si el padre no está activo, activarlo
            if (!parent.getStatus()) {
                parent.setStatus(true);
                accountCatalogueRepository.save(parent);

                // Continuar activando recursivamente hacia arriba
                activateParentHierarchy(parent);
            }
        }
    }
}
