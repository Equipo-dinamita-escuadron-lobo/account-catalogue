package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

public interface IAccountCatalogueRepository extends JpaRepository<AccountCatalogueEntity, Long> {

    /**
     * Encuentra un AccountCatalogueEntity por código y id de empresa (no eliminado).
     * 
     * @param code         el código de la cuenta.
     * @param idEnterprise el id de la empresa.
     * @return el AccountCatalogueEntity con el código y id de empresa dados. Si no
     *         se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a LEFT JOIN FETCH a.parent WHERE a.code = ?1 AND a.idEnterprise = ?2")
    AccountCatalogueEntity findByCode(String code, String idEnterprise);

    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.id = ?1")
    AccountCatalogueEntity findById(long id);

    @Query("SELECT a FROM AccountCatalogueEntity a LEFT JOIN FETCH a.children WHERE a.id = ?1")
    AccountCatalogueEntity findByIdWithChildren(long id);

    /**
     * Encuentra un AccountCatalogueEntity por ID y id de empresa (no eliminado).
     * 
     * @param id           el ID de la cuenta.
     * @param idEnterprise el id de la empresa.
     * @return el AccountCatalogueEntity con el ID y id de empresa dados. Si no
     *         se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.id = ?1 AND a.idEnterprise = ?2")
    AccountCatalogueEntity findByIdAndIdEnterprise(Long id, String idEnterprise);

    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.code = ?1")
    AccountCatalogueEntity findByCode(String code);

    /**
     * Encuentra un AccountCatalogueEntity por descripción (case-insensitive) y id de empresa (no eliminado).
     * 
     * @param description  la descripción de la cuenta (case-insensitive).
     * @param idEnterprise el id de la empresa.
     * @return el AccountCatalogueEntity con la descripción y id de empresa dados. Si no
     *         se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE UPPER(a.description) = UPPER(?1) AND a.idEnterprise = ?2")
    AccountCatalogueEntity findByDescriptionIgnoreCaseAndIdEnterprise(String description, String idEnterprise);

    /**
     * Encuentra todas las cuentas auxiliares (8 dígitos) activas para una empresa específica.
     * 
     * @param idEnterprise el id de la empresa.
     * @return lista de AccountCatalogueEntity con códigos de 8 dígitos para la empresa dada.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE LENGTH(a.code) = 8 AND a.idEnterprise = ?1 AND a.status = true ORDER BY a.code ASC")
    List<AccountCatalogueEntity> findAuxiliaryAccountsByIdEnterprise(String idEnterprise);

    /**
     * Encuentra todas las cuentas auxiliares (8 dígitos) activas que tienen el campo crossing activo para una empresa específica.
     * 
     * @param idEnterprise el id de la empresa.
     * @return lista de AccountCatalogueEntity con códigos de 8 dígitos y crossing = true para la empresa dada.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE LENGTH(a.code) = 8 AND a.idEnterprise = ?1 AND a.status = true AND a.crossing = true ORDER BY a.code ASC")
    List<AccountCatalogueEntity> findAuxiliaryAccountsWithCrossingByIdEnterprise(String idEnterprise);

    /**
     * Encuentra todas las cuentas hijas directas de un código padre para una empresa específica.
     * 
     * @param parentCode el código del padre.
     * @param idEnterprise el id de la empresa.
     * @return lista de AccountCatalogueEntity hijos directos del código padre dado.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.parent.code = ?1 AND a.idEnterprise = ?2")
    List<AccountCatalogueEntity> findByParentCode(String parentCode, String idEnterprise);

}
