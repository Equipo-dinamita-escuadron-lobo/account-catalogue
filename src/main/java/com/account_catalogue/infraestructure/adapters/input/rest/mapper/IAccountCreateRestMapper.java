package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.dto.ClaseDTO;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.*;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.Assistant1Res;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.Assistant2Res;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.GrupRes;
import org.mapstruct.Mapper;



@Mapper
public interface IAccountCreateRestMapper {
    AccountCatalogue toDomainClase(AccountCatalogueCreateReq accountCatalogueCreateReq);
    AccountCatalogue toDomainGrupo(AccountCatalogueGrupoReq accountCatalogueGrupoReq);
    AccountCatalogue toDomainCuenta(AccountCatalogueCuentaReq accountCatalogueCuentaReq);
    AccountCatalogue toDomainSubCuenta(AccountCatalogueSubcuentaReq accountCatalogueSubcuentaReq);
    AccountCatalogue toDomainAuxiliar1(AccountCatalogueAuxiliarReq accountCatalogueAuxiliarReq);
    AccountCatalogue toDomainAUxiliar2(AccountCatalogueAuxiliar2Req accountCatalogueAuxiliar2Req);
    AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue);


}
