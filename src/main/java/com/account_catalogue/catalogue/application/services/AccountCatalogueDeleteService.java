package com.account_catalogue.catalogue.application.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;

@Service
@AllArgsConstructor
public class AccountCatalogueDeleteService implements IAccountCatalogueDeleteInputPort {

    private  final IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;

    /**
     * Elimina un catalogo de cuenta por su ID.
     * 
     * @param id ID del cat logo de cuenta a eliminar
     */
   @Transactional
    @Override
    public void deleteById(Long id) {
        accountCatalogueDeleteOutputPort.deleteById(id);
    }
}
