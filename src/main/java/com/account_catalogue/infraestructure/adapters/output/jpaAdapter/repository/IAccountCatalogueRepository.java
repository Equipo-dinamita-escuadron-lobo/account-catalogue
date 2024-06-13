package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

public interface IAccountCatalogueRepository extends JpaRepository<AccountCatalogueEntity, Long> {

    @Query("SELECT a FROM AccountCatalogueEntity a WHERE a.code = ?1 AND a.idEnterprise = ?2")
    AccountCatalogueEntity findByCode(String code, String idEnterprise);

    AccountCatalogueEntity findById(long id);

    AccountCatalogueEntity findByCode(String code);


}
