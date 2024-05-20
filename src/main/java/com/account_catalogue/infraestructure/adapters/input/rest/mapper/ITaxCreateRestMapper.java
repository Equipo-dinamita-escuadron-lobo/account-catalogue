package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import org.mapstruct.Mapper;

@Mapper
public interface ITaxCreateRestMapper {
    Tax toDomain(TaxCreateReq taxCreateReq);
    TaxCreateRes toCreateResponse(Tax tax);
}
