package com.account_catalogue.application.services;

import com.account_catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
