package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxSearchRes;
import org.mapstruct.Mapper;

@Mapper
public interface ITaxSearchRestMapper {
    TaxSearchRes toSearchResponse(Tax tax);
}
