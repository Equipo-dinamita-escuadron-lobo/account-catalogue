package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;

import java.util.List;

public interface ITaxRepository extends JpaRepository<TaxEntity, Long> {

    /**
     * Encuentra un TaxEntity por código y id de empresa.
     * 
     * @param code         el código del impuesto.
     * @param idEnterprise el id de la empresa.
     * @return el TaxEntity con el código y id de empresa dados. Si no se
     *         encuentra, se devuelve null.
     */
    @Query("SELECT a FROM TaxEntity a WHERE a.code=?1 AND a.idEnterprise= ?2")
    TaxEntity findByCode(String code ,String idEnterprise);


    boolean existsByCode(String code);

    void deleteByCode(String code) ;

    List<TaxEntity> findAllByIdEnterprise(String idEnterprise);

    boolean existsByCodeAndIdEnterprise(String code, String idEnterprise);
}
