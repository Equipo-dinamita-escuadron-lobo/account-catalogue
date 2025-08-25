package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IAccountSearchRestMapper {

    /**
     * Este método transforma un objeto AccountCatalogue en un objeto
     * AccountCatalogueListRes,
     * que es el objeto que se utiliza para devolver la lista de cuentas en la API
     * REST.
     * Establece el id, código, descripción, naturaleza, estado financiero y
     * clasificación
     * del objeto AccountCatalogue en los campos correspondientes del objeto
     * AccountCatalogueListRes.
     * También establece los hijos del objeto AccountCatalogue en los hijos del
     * objeto
     * AccountCatalogueListRes después de transformar cada uno de ellos en objetos
     * AccountCatalogueListRes utilizando este mismo método.
     * Establece el padre del objeto AccountCatalogue en el padre del objeto
     * AccountCatalogueListRes si el padre del objeto AccountCatalogue no es null.
     * De lo contrario, establece el padre del objeto AccountCatalogueListRes en
     * null.
     * 
     * @param accountCatalogue el objeto AccountCatalogue a transformar.
     * @return el objeto AccountCatalogueListRes que representa el objeto
     *         AccountCatalogue transformado.
     */
    default AccountCatalogueListRes toAccountCatalogueListRes(AccountCatalogue accountCatalogue) {

        if (accountCatalogue == null) {
            return null;
        }
        List<AccountCatalogueListRes> children = accountCatalogue.getChildren().stream()
                .map(this::toAccountCatalogueListRes)
                .collect(Collectors.toList());
        return AccountCatalogueListRes.builder()
                .id(accountCatalogue.getId())
                .code(accountCatalogue.getCode())
                .description(accountCatalogue.getDescription())
                .nature(accountCatalogue.getNature().getState())
                .financialStatus(accountCatalogue.getFinancialStatus().getState())
                .classification(accountCatalogue.getClassification().getState())
                .crossing(accountCatalogue.getCrossing())
                .costCenter(accountCatalogue.getCostCenter())
                .children(children)
                .parent(accountCatalogue.getParent().getCode() == null ? null : accountCatalogue.getParent().getCode())
                .build();
    }
}
