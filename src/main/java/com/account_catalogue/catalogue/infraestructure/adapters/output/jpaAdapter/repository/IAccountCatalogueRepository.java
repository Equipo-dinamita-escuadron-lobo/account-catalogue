package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.codeLength = 8 AND a.idEnterprise = ?1 AND a.status = true ORDER BY a.code ASC")
    List<AccountCatalogueEntity> findAuxiliaryAccountsByIdEnterprise(String idEnterprise);

    /**
     * Encuentra todas las cuentas auxiliares (8 dígitos) activas que tienen el campo crossing activo para una empresa específica.
     * 
     * @param idEnterprise el id de la empresa.
     * @return lista de AccountCatalogueEntity con códigos de 8 dígitos y crossing = true para la empresa dada.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.codeLength = 8 AND a.idEnterprise = ?1 AND a.status = true AND a.crossing = true ORDER BY a.code ASC")
    List<AccountCatalogueEntity> findAuxiliaryAccountsWithCrossingByIdEnterprise(String idEnterprise);

    /**
     * Encuentra todos los IDs de descendientes de una cuenta (jerarquía recursiva).
     * 
     * @param parentId el ID del padre.
     * @param idEnterprise el id de la empresa.
     * @param tenantId el id del tenant.
     * @return lista de IDs de descendientes.
     */
    @Query(value = "WITH RECURSIVE descendants AS ( " +
                   "SELECT id FROM account WHERE id = :parentId AND id_enterprise = :idEnterprise AND tenant_id = :tenantId " +
                   "UNION ALL " +
                   "SELECT ac.id FROM account ac INNER JOIN descendants d ON ac.parent_id = d.id WHERE ac.id_enterprise = :idEnterprise AND ac.tenant_id = :tenantId " +
                   ") SELECT id FROM descendants WHERE id != :parentId", nativeQuery = true)
    List<Long> findDescendantIds(@Param("parentId") Long parentId, @Param("idEnterprise") String idEnterprise, @Param("tenantId") String tenantId);

    /**
     * Actualiza el estado de múltiples cuentas por sus IDs.
     * 
     * @param status el nuevo estado.
     * @param ids lista de IDs a actualizar.
     * @param idEnterprise el id de la empresa.
     */
    @Modifying
    @Query("UPDATE AccountCatalogueEntity a SET a.status = :status WHERE a.id IN :ids AND a.idEnterprise = :idEnterprise AND a.tenantId = :tenantId")
    void updateStatusByIds(@Param("status") Boolean status, @Param("ids") List<Long> ids, @Param("idEnterprise") String idEnterprise, @Param("tenantId") String tenantId);

    /**
     * Actualiza el estado de cuentas por códigos.
     * 
     * @param status el nuevo estado.
     * @param codes lista de códigos a actualizar.
     * @param idEnterprise el id de la empresa.
     */
    @Modifying
    @Query("UPDATE AccountCatalogueEntity a SET a.status = :status WHERE a.code IN :codes AND a.idEnterprise = :idEnterprise AND a.tenantId = :tenantId")
    void updateStatusByCodes(@Param("status") Boolean status, @Param("codes") List<String> codes, @Param("idEnterprise") String idEnterprise, @Param("tenantId") String tenantId);

    /**
     * Encuentra todas las cuentas (activas e inactivas) para una empresa específica con paginación.
     * Ordenadas por código para mantener la jerarquía.
     *
     * @param idEnterprise el id de la empresa.
     * @param pageable objeto de paginación.
     * @return página de AccountCatalogueEntity para la empresa dada.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.idEnterprise = ?1 ORDER BY a.code ASC")
    Page<AccountCatalogueEntity> findAllByIdEnterpriseOrderByCode(String idEnterprise, Pageable pageable);

    /**
     * Encuentra todas las cuentas activas para una empresa específica con paginación.
     * Ordenadas por código para mantener la jerarquía.
     *
     * @param idEnterprise el id de la empresa.
     * @param pageable objeto de paginación.
     * @return página de AccountCatalogueEntity activas para la empresa dada.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.idEnterprise = ?1 AND a.status = true ORDER BY a.code ASC")
    Page<AccountCatalogueEntity> findAllActiveByIdEnterpriseOrderByCode(String idEnterprise, Pageable pageable);

    /**
     * Encuentra todas las cuentas inactivas para una empresa específica con paginación.
     * Ordenadas por código para mantener la jerarquía.
     *
     * @param idEnterprise el id de la empresa.
     * @param pageable objeto de paginación.
     * @return página de AccountCatalogueEntity inactivas para la empresa dada.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.idEnterprise = ?1 AND a.status = false ORDER BY a.code ASC")
    Page<AccountCatalogueEntity> findAllInactiveByIdEnterpriseOrderByCode(String idEnterprise, Pageable pageable);


    /**
     * Encuentra toda la jerarquía de una cuenta por código e id de empresa usando consulta recursiva nativa.
     * Devuelve una lista de arrays con [id, code, description, parent_id, ...] para construir el árbol.
     * 
     * @param code el código de la cuenta raíz.
     * @param idEnterprise el id de la empresa.
     * @return lista de Object[] con los datos de la jerarquía.
     */
    @Query(value = """
        WITH RECURSIVE hierarchy AS (
            SELECT id, code, description, parent_id, nature, financial_status, classification, crossing, cost_center, status, id_enterprise
            FROM account
            WHERE code = :code AND id_enterprise = :idEnterprise
            UNION ALL
            SELECT a.id, a.code, a.description, a.parent_id, a.nature, a.financial_status, a.classification, a.crossing, a.cost_center, a.status, a.id_enterprise
            FROM account a
            INNER JOIN hierarchy h ON a.parent_id = h.id
            WHERE a.id_enterprise = :idEnterprise
        )
        SELECT * FROM hierarchy
        """, nativeQuery = true)
    List<Object[]> findHierarchyByCode(@Param("code") String code, @Param("idEnterprise") String idEnterprise);

    /**
     * Encuentra todas las cuentas para una empresa específica ordenadas por código.
     *
     * @param idEnterprise el id de la empresa.
     * @return lista de AccountCatalogueEntity ordenadas por código.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.idEnterprise = ?1 ORDER BY a.code ASC")
    List<AccountCatalogueEntity> findByIdEnterpriseOrderByCode(String idEnterprise);

    /**
     * Encuentra cuentas que coinciden con el criterio de búsqueda (código o descripción) para una empresa específica.
     * Búsqueda inteligente por código o descripción, ordenada por código ascendente.
     *
     * @param idEnterprise el id de la empresa.
     * @param search el término de búsqueda.
     * @return lista de AccountCatalogueEntity que coinciden con la búsqueda.
     */
    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.idEnterprise = ?1 AND (a.code LIKE %?2% OR UPPER(a.description) LIKE UPPER(CONCAT('%', ?2, '%'))) ORDER BY a.code ASC")
    List<AccountCatalogueEntity> findByIdEnterpriseAndCodeOrDescription(String idEnterprise, String search);
}
