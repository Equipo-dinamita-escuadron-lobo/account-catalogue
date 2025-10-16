package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import java.util.List;

public interface ITaxRepository extends JpaRepository<TaxEntity, Long> {

    /**
     * Encuentra un TaxEntity por código y id de empresa (solo activos, no eliminados).
     * 
     * @param code         el código del impuesto.
     * @param idEnterprise el id de la empresa.
     * @return el TaxEntity con el código y id de empresa dados. Si no se
     *         encuentra, se devuelve null.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.code=?1 AND a.idEnterprise=?2")
    TaxEntity findByCode(String code, String idEnterprise);

    /**
     * Encuentra un TaxEntity por ID (solo activos, no eliminados).
     * 
     * @param id el ID del impuesto.
     * @return el TaxEntity con el ID dado. Si no se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.id=?1")
    TaxEntity findByIdActive(Long id);

    /**
     * Encuentra un TaxEntity por ID y empresa (solo activos, no eliminados).
     * Este método es más eficiente para operaciones que requieren validar empresa.
     * 
     * @param id el ID del impuesto.
     * @param idEnterprise el ID de la empresa.
     * @return el TaxEntity con el ID y empresa dados. Si no se encuentra, se devuelve null.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.id=?1 AND a.idEnterprise=?2")
    TaxEntity findByIdAndEnterpriseActive(Long id, String idEnterprise);


    boolean existsByCode(String code);

    void deleteByCode(String code) ;

    /**
     * Encuentra todos los impuestos de una empresa (solo activos, no eliminados).
     * 
     * @param idEnterprise el ID de la empresa.
     * @return lista de impuestos activos de la empresa.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.idEnterprise=?1")
    List<TaxEntity> findAllByIdEnterprise(String idEnterprise);

    /**
     * Verifica si existe un impuesto con el código e idEnterprise especificados (solo activos).
     * 
     * @param code código del impuesto
     * @param idEnterprise ID de la empresa
     * @return true si existe, false si no
     */
    @Query("SELECT COUNT(a) > 0 FROM TaxEntity a WHERE a.code=?1 AND a.idEnterprise=?2")
    boolean existsByCodeAndIdEnterprise(String code, String idEnterprise);
}
