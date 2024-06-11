package com.account_catalogue.domain.services;

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

    @Override
    public Tax update(TaxDTO taxDTO,String code) {
        return null;
    }
}
