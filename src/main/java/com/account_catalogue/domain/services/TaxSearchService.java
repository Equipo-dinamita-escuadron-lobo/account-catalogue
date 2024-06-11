package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.ITaxSearchInputPort;
import com.account_catalogue.application.output.ITaxSearchOutputPort;
import com.account_catalogue.domain.models.Tax;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaxSearchService implements ITaxSearchInputPort {
    private final ITaxSearchOutputPort taxSearchOutputPort;

    @Override
    public Tax getTax(String code) {
        return taxSearchOutputPort.getTax(code);
    }

    @Override
    public List<Tax> getTaxes() {
        return taxSearchOutputPort.getTaxes();
    }
}
