package com.account_catalogue.catalogue.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueNotFoundException;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountCatalogueSearchService implements IAccountCatalogueSearchInputPort {

    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * Obtiene el catálogo de cuenta por código y ID de empresa con validación de existencia.
     *
     * @param code         el código del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el catálogo de cuenta
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise) {
        return validationService.validateAccountExists(code, idEnterprise);
    }

    /**
     * Obtiene el árbol del catálogo de cuenta por código y ID de empresa con validación de existencia.
     *
     * @param code         el código del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el árbol del catálogo de cuenta
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    @Override
    public AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise) {
        // Primero validar que existe
        validationService.validateAccountExists(code, idEnterprise);
        // Luego obtener el árbol
        return accountCatalogueSearchOutputPort.getAccountCatalogueTreeByCode(code, idEnterprise);
    }

    /**
     * Obtiene los árboles de catálogo de cuentas para los códigos raíz (1-9) de una empresa específica.
     * Solo incluye cuentas que existen, omitiendo las que no se encuentren.
     *
     * @param idEnterprise el ID de la empresa
     * @return lista de árboles de catálogos de cuentas para códigos 1-9 existentes
     */
    @Override
    public List<AccountCatalogue> getAccountCatalogueTrees(String idEnterprise) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para buscar árboles de catálogos");
        }

        List<AccountCatalogue> trees = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            try {
                AccountCatalogue tree = getAccountCatalogueTree(String.valueOf(i), idEnterprise.trim());
                trees.add(tree);
            } catch (AccountCatalogueNotFoundException e) {
                // Si la cuenta con código 'i' no existe, simplemente continúa con el siguiente
                // No lanza excepción, solo omite la cuenta inexistente
            }
        }
        return trees;
    }

    /**
     * Obtiene un catálogo de cuenta por ID y empresa.
     *
     * @param id el ID del catálogo de cuenta
     * @param idEnterprise el ID de la empresa
     * @return el catálogo de cuenta
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    @Override
    public AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise) {
        return validationService.validateAccountExistsByIdAndEnterprise(id, idEnterprise);
    }

    /**
     * Obtiene todas las cuentas auxiliares (8 dígitos) activas para una empresa específica.
     *
     * @param idEnterprise el ID de la empresa
     * @return lista de cuentas auxiliares
     * @throws IllegalArgumentException si el idEnterprise es null o vacío
     */
    @Override
    public List<AccountCatalogue> getAuxiliaryAccounts(String idEnterprise) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para buscar cuentas auxiliares");
        }
        
        return accountCatalogueSearchOutputPort.getAuxiliaryAccountsByIdEnterprise(idEnterprise.trim());
    }

    /**
     * Obtiene todas las cuentas auxiliares (8 dígitos) activas que tienen el campo crossing activo para una empresa específica.
     *
     * @param idEnterprise el ID de la empresa
     * @return lista de cuentas auxiliares con crossing activo
     * @throws IllegalArgumentException si el idEnterprise es null o vacío
     */
    @Override
    public List<AccountCatalogue> getAuxiliaryAccountsWithCrossing(String idEnterprise) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para buscar cuentas auxiliares con crossing");
        }
        
        return accountCatalogueSearchOutputPort.getAuxiliaryAccountsWithCrossingByIdEnterprise(idEnterprise.trim());
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
    public Page<AccountCatalogue> getAllAccountCatalogues(String idEnterprise, Pageable pageable) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para buscar catálogos de cuentas");
        }

        return accountCatalogueSearchOutputPort.getAllAccountCataloguesByIdEnterprise(idEnterprise.trim(), pageable);
    }

    @Override
    public Page<AccountCatalogue> getAllAccountCataloguesByStatus(String idEnterprise, Boolean status, Pageable pageable) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para buscar catálogos de cuentas");
        }

        return accountCatalogueSearchOutputPort.getAllAccountCataloguesByIdEnterpriseAndStatus(idEnterprise.trim(), status, pageable);
    }

    /**
     * Obtiene todas las cuentas para una empresa específica ordenadas por código.
     *
     * @param idEnterprise el ID de la empresa
     * @return lista de todas las cuentas ordenadas por código
     */
    @Override
    public List<AccountCatalogue> getAllAccountsByEnterprise(String idEnterprise) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para obtener cuentas");
        }

        return accountCatalogueSearchOutputPort.getAllAccountsByEnterprise(idEnterprise.trim());
    }

    /**
     * Obtiene las cuentas que coinciden con el criterio de búsqueda (código o descripción) para una empresa específica.
     * Búsqueda inteligente por código o descripción, ordenada por código ascendente.
     *
     * @param idEnterprise el ID de la empresa
     * @param search el término de búsqueda (código o descripción)
     * @return lista de cuentas que coinciden con el criterio de búsqueda
     */
    @Override
    public List<AccountCatalogue> getAccountsByCodeOrDescription(String idEnterprise, String search) {
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa es requerido para buscar cuentas");
        }
        if (search == null || search.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda es requerido");
        }

        return accountCatalogueSearchOutputPort.getAccountsByCodeOrDescription(idEnterprise.trim(), search.trim());
    }

}
