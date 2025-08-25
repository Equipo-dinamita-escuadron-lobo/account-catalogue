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
     * Realiza un soft delete marcando la cuenta como eliminada (isDeleted = true).
     * También marca como eliminadas todas las cuentas hijas en cascada.
     *
     * @param id del item de la cuenta que se va a marcar como eliminada
     *
     */
    @Override
    public void deleteById(Long id) {
        AccountCatalogueEntity entity = accountCatalogueRepository.findById(id.longValue());
        if (entity != null) {
            // Marcar la cuenta principal como eliminada
            markAsDeleted(entity);
        }
    }
    
    /**
     * Marca recursivamente una cuenta y todas sus hijas como eliminadas.
     * 
     * @param entity la cuenta a marcar como eliminada
     */
    private void markAsDeleted(AccountCatalogueEntity entity) {
        entity.setIsDeleted(true);
        
        // Marcar recursivamente todas las cuentas hijas como eliminadas
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            for (AccountCatalogueEntity child : entity.getChildren()) {
                markAsDeleted(child);
            }
        }
        
        accountCatalogueRepository.save(entity);
    }
}
