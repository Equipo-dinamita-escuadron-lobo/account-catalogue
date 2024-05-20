package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository;

import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaxRepository extends JpaRepository<TaxEntity,Long> {

 TaxEntity findByCode(String code);
}
