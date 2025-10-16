package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import java.util.List;

public interface ITaxRepository extends JpaRepository<TaxEntity, Long> {

    /**
     * Encuentra un TaxEntity por código y id de empresa 
     * 
     * @param code         el código del impuesto.
     * @param idEnterprise el id de la empresa.
     * @return el TaxEntity con el código y id de empresa dados. Si no se
     *         encuentra, se devuelve null.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.code=?1 AND a.idEnterprise=?2")
    TaxEntity findByCode(String code, String idEnterprise);

    /**
     * Encuentra un TaxEntity por ID y empresa
     * 
     * @param id el ID del impuesto.
     * @param idEnterprise el ID de la empresa.
     * @return el TaxEntity con el ID y empresa dados. Si no se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.id=?1 AND a.idEnterprise=?2 AND a.status = true")
    TaxEntity findByIdAndEnterpriseActive(Long id, String idEnterprise);

    /**
     * Encuentra todos los impuestos activos de una empresa.
     * 
     * @param idEnterprise el ID de la empresa.
     * @return lista de impuestos activos de la empresa.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.idEnterprise=?1 AND a.status = true")
    List<TaxEntity> findActiveByIdEnterprise(String idEnterprise);

    /**
     * Encuentra todos los impuestos de una empresa con paginación.
     * 
     * @param idEnterprise el ID de la empresa.
     * @param pageable configuración de paginación.
     * @return página de impuestos de la empresa.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.idEnterprise=?1")
    Page<TaxEntity> findAllByIdEnterprise(String idEnterprise, Pageable pageable);

    /**
     * Cuenta el total de impuestos por empresa.
     * 
     * @param idEnterprise el ID de la empresa.
     * @return número total de impuestos.
     */
    @Query("SELECT COUNT(a) FROM TaxEntity a WHERE a.idEnterprise=?1")
    long countByIdEnterprise(String idEnterprise);

    /**
     * Busca impuestos por empresa y descripción (búsqueda parcial case-insensitive).
     * 
     * @param idEnterprise el ID de la empresa.
     * @param description término de búsqueda.
     * @param pageable configuración de paginación.
     * @return página de impuestos que coinciden con la búsqueda.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.idEnterprise=?1 AND LOWER(a.description) LIKE LOWER(CONCAT('%', ?2, '%'))")
    Page<TaxEntity> findByIdEnterpriseAndDescriptionContainingIgnoreCase(String idEnterprise, String description, Pageable pageable);

    /**
     * Cuenta impuestos por empresa y descripción (búsqueda parcial case-insensitive).
     * 
     * @param idEnterprise el ID de la empresa.
     * @param description término de búsqueda.
     * @return número total de impuestos que coinciden con la búsqueda.
     */
    @Query("SELECT COUNT(a) FROM TaxEntity a WHERE a.idEnterprise=?1 AND LOWER(a.description) LIKE LOWER(CONCAT('%', ?2, '%'))")
    long countByIdEnterpriseAndDescriptionContainingIgnoreCase(String idEnterprise, String description);
}
