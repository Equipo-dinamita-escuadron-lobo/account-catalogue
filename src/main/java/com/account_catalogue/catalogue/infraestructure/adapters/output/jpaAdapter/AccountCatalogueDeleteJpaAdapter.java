package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
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
     * elimina una item del catalogo de cuenta.
     *
     * @param id de el item de la cuenta se va eliminar que se va eliminar
     *
     */

    @Override
    public void deleteById(Long id) {
       accountCatalogueRepository.deleteById(id);
    }
}
