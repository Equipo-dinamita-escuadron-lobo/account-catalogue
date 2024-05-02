package com.account_catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.*;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.util.AdjustEnumAccount;
import org.mapstruct.Mapper;



@Mapper
public interface IAccountCreateRestMapper {
    AdjustEnumAccount adjustEnum=new AdjustEnumAccount();
   default AccountCatalogue toDomainClase(AccountCatalogueCreateReq accountCatalogueCreateReq){
     // AdjustEnumAccount adjustEnum=new AdjustEnumAccount();

       if(accountCatalogueCreateReq==null){
           return null;
       }
       return AccountCatalogue.builder()
               .code(accountCatalogueCreateReq.getCode())
               .description(accountCatalogueCreateReq.getDescription())
               .nature(adjustEnum.adjustNatureEnum(accountCatalogueCreateReq.getNature()))
               .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueCreateReq.getFinancialStatus()))
               .classification(adjustEnum.adjustClassificationEnum(accountCatalogueCreateReq.getClassification()))
               .build();
    }
    default AccountCatalogue toDomainGrupo(AccountCatalogueGrupoReq accountCatalogueGrupoReq){
       // AdjustEnumAccount adjustEnum=new AdjustEnumAccount();
        if(accountCatalogueGrupoReq==null){
            return null;
        }
        return AccountCatalogue.builder()
                .code(accountCatalogueGrupoReq.getCode())
                .description(accountCatalogueGrupoReq.getDescription())
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueGrupoReq.getNature()))
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueGrupoReq.getFinancialStatus()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueGrupoReq.getClassification()))
                .build();

    }
    default AccountCatalogue toDomainCuenta(AccountCatalogueCuentaReq accountCatalogueCuentaReq){
        ///AdjustEnumAccount adjustEnum=new AdjustEnumAccount();
        if(accountCatalogueCuentaReq==null){
            return null;
        }
        return AccountCatalogue.builder()
                .code(accountCatalogueCuentaReq.getCode())
                .description(accountCatalogueCuentaReq.getDescription())
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueCuentaReq.getNature()))
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueCuentaReq.getFinancialStatus()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueCuentaReq.getClassification()))
                .build();

    }
    default AccountCatalogue toDomainSubCuenta(AccountCatalogueSubcuentaReq accountCatalogueSubcuentaReq){
        //AdjustEnumAccount adjustEnum=new AdjustEnumAccount();
        if(accountCatalogueSubcuentaReq==null){
            return null;
        }
        return AccountCatalogue.builder()
                .code(accountCatalogueSubcuentaReq.getCode())
                .description(accountCatalogueSubcuentaReq.getDescription())
                .nature(adjustEnum.adjustNatureEnum(accountCatalogueSubcuentaReq.getNature()))
                .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueSubcuentaReq.getFinancialStatus()))
                .classification(adjustEnum.adjustClassificationEnum(accountCatalogueSubcuentaReq.getClassification()))
                .build();

    }
   default AccountCatalogue toDomainAuxiliar1(AccountCatalogueAuxiliarReq accountCatalogueAuxiliarReq){
      // AdjustEnumAccount adjustEnum=new AdjustEnumAccount();
       if(accountCatalogueAuxiliarReq==null){
           return null;
       }
       return AccountCatalogue.builder()
               .code(accountCatalogueAuxiliarReq.getCode())
               .description(accountCatalogueAuxiliarReq.getDescription())
               .nature(adjustEnum.adjustNatureEnum(accountCatalogueAuxiliarReq.getNature()))
               .financialStatus(adjustEnum.adjustFinancialStatusEnum(accountCatalogueAuxiliarReq.getFinancialStatus()))
               .classification(adjustEnum.adjustClassificationEnum(accountCatalogueAuxiliarReq.getClassification()))
               .build();
   }
    AccountCatalogueCreateRes toCreateResponse(AccountCatalogue accountCatalogue);


}
