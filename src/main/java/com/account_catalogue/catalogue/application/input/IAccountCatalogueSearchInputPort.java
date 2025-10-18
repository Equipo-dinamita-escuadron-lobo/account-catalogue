package com.account_catalogue.catalogue.application.input;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public interface IAccountCatalogueSearchInputPort {

    AccountCatalogue getAccountCatalogueByCode(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueTree(String code, String idEnterprise);

    AccountCatalogue getAccountCatalogueById(Long id, String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccounts(String idEnterprise);

    List<AccountCatalogue> getAuxiliaryAccountsWithCrossing(String idEnterprise);

    /**
     * Obtiene todos los catálogos de cuentas para una empresa específica con paginación.
     * Los resultados se ordenan por código para mantener la jerarquía.
     *
     * @param idEnterprise el ID de la empresa
     * @param pageable objeto de paginación con ordenamiento
     * @return página de catálogos de cuentas
     */
    Page<AccountCatalogue> getAllAccountCatalogues(String idEnterprise, Pageable pageable);
}
