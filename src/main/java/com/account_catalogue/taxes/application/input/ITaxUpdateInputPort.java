package com.account_catalogue.taxes.application.input;

import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxUpdateInputPort {
    Tax update(TaxDTO taxDTO,long id);
}
