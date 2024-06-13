package com.account_catalogue.application.input;

import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;

public interface ITaxUpdateInputPort {
    Tax update(TaxDTO taxDTO,long id);
}
