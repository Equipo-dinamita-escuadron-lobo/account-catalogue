package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountTaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAccountTaxRepository extends JpaRepository<AccountTaxEntity,Long> {

}
