package com.account_catalogue.catalogue.application.output;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchOutputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTreeByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise);

    AccountCatalogue getAccountCatalogueByIdAndIdEnterprise(Long id, String idEnterprise);

    AccountCatalogue getAccountCatalogueTreeByIdAndIdEnterprise(Long id, String idEnterprise);
    
    AccountCatalogue getAccountCatalogueByDescriptionIgnoreCaseAndIdEnterprise(String description, String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsByIdEnterprise(String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsWithCrossingByIdEnterprise(String idEnterprise);

    /**
     * Obtiene todos los catálogos de cuentas para una empresa específica con paginación.
     * Los resultados se ordenan por código para mantener la jerarquía.
     *
     * @param idEnterprise el ID de la empresa
     * @param pageable objeto de paginación con ordenamiento
     * @return página de catálogos de cuentas
     */
    Page<AccountCatalogue> getAllAccountCataloguesByIdEnterprise(String idEnterprise, Pageable pageable);

    /**
     * Obtiene todas las cuentas (activas e inactivas) para una empresa específica ordenadas por código.
     *
     * @param idEnterprise el ID de la empresa
     * @return lista de todas las cuentas ordenadas por código
     */
    List<AccountCatalogue> getAllAccountsByEnterprise(String idEnterprise);

    /**
     * Obtiene las cuentas (activas e inactivas) que coinciden con el criterio de búsqueda (código o descripción) para una empresa específica.
     * Búsqueda inteligente por código o descripción, ordenada por código ascendente.
     *
     * @param idEnterprise el ID de la empresa
     * @param search el término de búsqueda (código o descripción)
     * @return lista de cuentas que coinciden con el criterio de búsqueda
     */
    List<AccountCatalogue> getAccountsByCodeOrDescription(String idEnterprise, String search);
}
