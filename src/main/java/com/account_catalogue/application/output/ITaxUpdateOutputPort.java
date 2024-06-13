package com.account_catalogue.application.output;

import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;

public interface ITaxUpdateOutputPort {
    Tax update(TaxDTO taxDTO,long id);
}
