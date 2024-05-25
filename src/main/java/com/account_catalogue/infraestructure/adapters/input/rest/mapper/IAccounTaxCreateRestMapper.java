package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountTax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountTaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountTaxCreateRes;
import org.mapstruct.Mapper;

@Mapper
public interface IAccounTaxCreateRestMapper {
    AccountTaxCreateRes toCreateResponse(AccountTax accountTax);

    AccountTax toDomian(AccountTaxCreateReq accountTaxCreateReq);
}
