package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.projection.IAccountCatalogueInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IAccountCatalogueRepository extends JpaRepository<AccountCatalogueEntity, Long> {

    AccountCatalogueEntity findByCode(String code);
    void deleteByCode(String code);
    @Query("SELECT a.id AS id, a.code AS code , a.description AS description FROM AccountCatalogueEntity a WHERE a.code LIKE :code%")
    List<IAccountCatalogueInfoProjection> getAllAccountCatalogue(@Param("code") String code);
}
