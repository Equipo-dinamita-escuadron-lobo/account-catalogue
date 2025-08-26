package com.account_catalogue.commons.exceptions.banks;

import com.account_catalogue.commons.exceptions.BaseBusinessException;

public class BankAlreadyExistsException extends BaseBusinessException {

    public BankAlreadyExistsException(String field, String value, String enterpriseId) {
        super(BankErrorCode.BANK_ALREADY_EXISTS, 
              String.format("Ya existe un banco con %s '%s' para la empresa '%s'", field, value, enterpriseId));
    }
}
