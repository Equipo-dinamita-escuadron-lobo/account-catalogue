package com.account_catalogue.taxes.application.output;

import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;

public interface ITaxUpdateOutputPort {
    Tax update(TaxDTO taxDTO,long id);
}
