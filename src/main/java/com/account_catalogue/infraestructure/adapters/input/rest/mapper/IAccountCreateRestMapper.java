package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.*;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import org.mapstruct.Mapper;



@Mapper
public interface IAccountCreateRestMapper {
    AccountCatalogue toDomainClase(AccountCatalogueCreateReq accountCatalogueCreateReq);
    AccountCatalogue toDomainGrupo(AccountCatalogueGrupoReq accountCatalogueGrupoReq);
    AccountCatalogue toDomainCuenta(AccountCatalogueCuentaReq accountCatalogueCuentaReq);
    AccountCatalogue toDomainSubCuenta(AccountCatalogueSubcuentaReq accountCatalogueSubcuentaReq);
    AccountCatalogue toDomainAuxiliar1(AccountCatalogueAuxiliarReq accountCatalogueAuxiliarReq);
    AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue);


}
