package com.account_catalogue.taxes.application.services;

import com.account_catalogue.taxes.application.input.ITaxSearchInputPort;
import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;

import lombok.AllArgsConstructor;
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
     * Obtiene una lista de impuestos asociados al ID de empresa proporcionado.
     *
     * @param idEnterprise el ID de la empresa
     * @return una lista de impuestos asociados al ID de empresa proporcionado
     */
    @Override
    public List<Tax> getTaxes(String idEnterprise) {
        return taxSearchOutputPort.getTaxes(idEnterprise);
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
}
