package com.account_catalogue.taxes.application.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.taxes.application.input.ITaxDeleteInputPort;
import com.account_catalogue.taxes.application.output.ITaxDeleteOutputPort;

@Service
@AllArgsConstructor
public class TaxDeleteService implements ITaxDeleteInputPort {
    private final ITaxDeleteOutputPort taxDeleteOutputPort;

    /**
     * Elimina un impuesto por su ID.
     *
     * @param id ID del impuesto a eliminar
     * @return true si se elimin con xito, false de lo contrario
     */
    @Override
    public boolean deleteByCode(long id) {
        return taxDeleteOutputPort.deleteByCode(id);
    }
}
