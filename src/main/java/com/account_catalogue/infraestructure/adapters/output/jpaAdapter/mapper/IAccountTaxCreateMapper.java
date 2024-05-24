package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.AccountTax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountTaxEntity;
import org.mapstruct.Mapper;


@Mapper
public interface IAccountTaxCreateMapper {
    AccountTaxEntity toEntity(AccountTax accountTax);
    AccountTax toModel(AccountTaxEntity accountTaxEntity);


}
