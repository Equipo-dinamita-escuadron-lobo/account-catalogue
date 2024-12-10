package com.account_catalogue.application.services;

import com.account_catalogue.application.input.ITaxUpdateInputPort;
import com.account_catalogue.application.output.ITaxUpdateOutputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxUpdateService implements ITaxUpdateInputPort {
    @Autowired
    private ITaxUpdateOutputPort taxUpdateOutputPort;

    /**
     * Actualiza los detalles de un impuesto.
     *
     * @param taxDTO el objeto TaxDTO que contiene los detalles del impuesto a
     *               actualizar.
     * @param id     el identificador del impuesto a actualizar.
     * @return el objeto Tax actualizado.
     */
    @Override
    public Tax update(TaxDTO taxDTO, long id) {
        return taxUpdateOutputPort.update(taxDTO, id);
    }
}
