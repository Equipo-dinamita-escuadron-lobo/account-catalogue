package com.account_catalogue.taxes.application.services;

import com.account_catalogue.taxes.application.input.ITaxSearchInputPort;
import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaxSearchService implements ITaxSearchInputPort {
    private final ITaxSearchOutputPort taxSearchOutputPort;
    private final TaxValidationService taxValidationService;

    /**
     * Obtiene la información del impuesto basada en el código y el ID de empresa
     * proporcionados.
     *
     * @param code         el código del impuesto
     * @param idEnterprise el ID de la empresa
     * @return el impuesto correspondiente al código y ID de empresa proporcionados
     * @throws TaxNotFoundException si el impuesto no existe
     */
    @Override
    public Tax getTax(String code, String idEnterprise) {
        // Validar que el impuesto existe
        taxValidationService.validateTaxExists(code, idEnterprise);
        
        // Si existe, obtenerlo y devolverlo
        return taxSearchOutputPort.getTax(code, idEnterprise);
    }

    /**
     * Obtiene una lista de impuestos activos asociados al ID de empresa proporcionado.
     *
     * @param idEnterprise el ID de la empresa
     * @return una lista de impuestos activos asociados al ID de empresa proporcionado
     */
    @Override
    public List<Tax> getActiveTaxes(String idEnterprise) {
        return taxSearchOutputPort.getActiveTaxes(idEnterprise);
    }

    /**
     * Obtiene una página de impuestos por empresa con paginación y ordenamiento.
     *
     * @param idEnterprise el ID de la empresa
     * @param page número de página
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (asc/desc)
     * @return página de impuestos
     */
    @Override
    public Page<Tax> getTaxesPaginated(String idEnterprise, int page, int size, String sortField, String sortOrder) {
        return taxSearchOutputPort.getTaxesPaginated(idEnterprise, page, size, sortField, sortOrder);
    }

    /**
     * Obtiene una página de impuestos por empresa y descripción con paginación y ordenamiento.
     *
     * @param idEnterprise el ID de la empresa
     * @param search término de búsqueda en la descripción
     * @param page número de página
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (asc/desc)
     * @return página de impuestos que coinciden con la búsqueda
     */
    @Override
    public Page<Tax> getTaxesByDescriptionPaginated(String idEnterprise, String search, int page, int size, String sortField, String sortOrder) {
        return taxSearchOutputPort.getTaxesByDescriptionPaginated(idEnterprise, search, page, size, sortField, sortOrder);
    }

    /**
     * Cuenta el total de impuestos por empresa.
     *
     * @param idEnterprise el ID de la empresa
     * @return número total de impuestos
     */
    @Override
    public long countTaxesByEnterprise(String idEnterprise) {
        return taxSearchOutputPort.countTaxesByEnterprise(idEnterprise);
    }

    /**
     * Cuenta impuestos por empresa y descripción.
     *
     * @param idEnterprise el ID de la empresa
     * @param search término de búsqueda en la descripción
     * @return número total de impuestos que coinciden con la búsqueda
     */
    @Override
    public long countTaxesByEnterpriseAndDescription(String idEnterprise, String search) {
        return taxSearchOutputPort.countTaxesByEnterpriseAndDescription(idEnterprise, search);
    }
}
