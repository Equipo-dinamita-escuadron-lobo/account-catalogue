package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Adaptador para eliminar un item del catalogo de cuenta usando JPA.
 * Implementa la interfaz IAccountCatalogueDeleteOutputPort.
 */
@Component
@Data
public class AccountCatalogueDeleteJpaAdapter implements IAccountCatalogueDeleteOutputPort {

    private final IAccountCatalogueRepository accountCatalogueRepository;

    /**
     * Realiza una eliminación de la cuenta y todas sus cuentas hijas en cascada.
     *
     * @param id del item de la cuenta que se va a eliminar
     *
     */
    @Override
    public void deleteById(Long id) {
        AccountCatalogueEntity entity = accountCatalogueRepository.findById(id.longValue());
        if (entity != null) {
            // Eliminar físicamente la cuenta principal y sus hijas
            deletePhysical(entity);
        }
    }
    
    /**
     * Elimina físicamente una cuenta y todas sus hijas recursivamente.
     * 
     * @param entity la cuenta a eliminar
     */
    private void deletePhysical(AccountCatalogueEntity entity) {
        // Eliminar recursivamente todas las cuentas hijas primero
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            for (AccountCatalogueEntity child : entity.getChildren()) {
                deletePhysical(child);
            }
        }
        
        accountCatalogueRepository.delete(entity);
    }
}
