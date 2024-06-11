package com.account_catalogue.domain.services;

import com.account_catalogue.application.input.ITaxCreateInputPort;
import com.account_catalogue.application.output.ITaxCreateOutputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data
public class TaxCreateService implements ITaxCreateInputPort {
    private final ITaxCreateOutputPort taxCreateOutputPort;
    @Override
    public Tax createTax(TaxDTO tax) {
        return taxCreateOutputPort.createTax(tax);
    }
}
