package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

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
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.code = ?1 AND a.idEnterprise = ?2 AND a.isDeleted = false")
    AccountCatalogueEntity findByCode(String code, String idEnterprise);

    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.id = ?1 AND a.isDeleted = false")
    AccountCatalogueEntity findById(long id);

    @Query("SELECT a FROM AccountCatalogueEntity a LEFT JOIN FETCH a.children WHERE a.id = ?1 AND a.isDeleted = false")
    AccountCatalogueEntity findByIdWithChildren(long id);

    /**
     * Encuentra un AccountCatalogueEntity por ID y id de empresa (no eliminado).
     * 
     * @param id           el ID de la cuenta.
     * @param idEnterprise el id de la empresa.
     * @return el AccountCatalogueEntity con el ID y id de empresa dados. Si no
     *         se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.id = ?1 AND a.idEnterprise = ?2 AND a.isDeleted = false")
    AccountCatalogueEntity findByIdAndIdEnterprise(Long id, String idEnterprise);

    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.code = ?1 AND a.isDeleted = false")
    AccountCatalogueEntity findByCode(String code);

    /**
     * Encuentra un AccountCatalogueEntity por descripción (case-insensitive) y id de empresa (no eliminado).
     * 
     * @param description  la descripción de la cuenta (case-insensitive).
     * @param idEnterprise el id de la empresa.
     * @return el AccountCatalogueEntity con la descripción y id de empresa dados. Si no
     *         se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE UPPER(a.description) = UPPER(?1) AND a.idEnterprise = ?2 AND a.isDeleted = false")
    AccountCatalogueEntity findByDescriptionIgnoreCaseAndIdEnterprise(String description, String idEnterprise);

}
