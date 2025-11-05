package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import java.util.List;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AuxiliaryAccountListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.ItemAccountCatalogueSearchRes;

import org.mapstruct.Mapper;

@Mapper
public interface IAuxiliaryAccountRestMapper {
    
    /**
     * Transforma una lista de AccountCatalogue en AuxiliaryAccountListRes.
     * 
     * @param auxiliaryAccounts lista de cuentas auxiliares del dominio
     * @param idEnterprise el ID de la empresa
     * @return el objeto AuxiliaryAccountListRes con la lista transformada
     */
    AuxiliaryAccountListRes toAuxiliaryAccountListRes(List<AccountCatalogue> auxiliaryAccounts, String idEnterprise);
    
    /**
     * Transforma un AccountCatalogue en ItemAccountCatalogueSearchRes.
     * 
     * @param accountCatalogue el objeto AccountCatalogue a transformar
     * @return el objeto ItemAccountCatalogueSearchRes transformado
     */
    ItemAccountCatalogueSearchRes toItemAccountCatalogueSearchRes(AccountCatalogue accountCatalogue);

    /**
     * Mapea un objeto AccountCatalogue (parent) a String (código del padre).
     * Método auxiliar requerido por MapStruct para la conversión automática.
     * 
     * @param parent el objeto AccountCatalogue padre
     * @return el código del padre como String, o null si no existe
     */
    default String map(AccountCatalogue parent) {
        return parent != null && parent.getCode() != null ? parent.getCode() : null;
    }
}
