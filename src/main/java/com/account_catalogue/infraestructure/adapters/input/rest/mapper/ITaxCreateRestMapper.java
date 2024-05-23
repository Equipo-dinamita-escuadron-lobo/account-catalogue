package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface ITaxCreateRestMapper {

    @Mapping(target = "accounts", ignore = true)
    Tax toDomain(TaxCreateReq taxCreateReq);
    TaxCreateRes toCreateResponse(Tax tax);
}
