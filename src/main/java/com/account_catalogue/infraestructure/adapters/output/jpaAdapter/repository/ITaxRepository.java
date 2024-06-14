package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITaxRepository extends JpaRepository<TaxEntity,Long> {

    @Query("SELECT a FROM TaxEntity a WHERE a.code=?1 AND a.idEnterprise= ?2")
    TaxEntity findByCode(String code ,String idEnterprise);


    boolean existsByCode(String code);

    void deleteByCode(String code) ;

    List<TaxEntity> findAllByIdEnterprise(String idEnterprise);
}
