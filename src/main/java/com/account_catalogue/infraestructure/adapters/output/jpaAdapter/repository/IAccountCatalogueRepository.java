package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

public interface IAccountCatalogueRepository extends JpaRepository<AccountCatalogueEntity, Long> {

    AccountCatalogueEntity findByCode(String code);

    AccountCatalogueEntity findById(long id);

    void deleteByCode(String code);
}
