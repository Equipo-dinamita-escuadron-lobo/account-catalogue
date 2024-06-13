package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.ITaxDeleteInputPort;
import com.account_catalogue.application.output.ITaxDeleteOutputPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaxDeleteService implements ITaxDeleteInputPort {
    private final ITaxDeleteOutputPort taxDeleteOutputPort;
    @Override
    public boolean deleteByCode(long id) {
        return taxDeleteOutputPort.deleteByCode(id);
    }
}
