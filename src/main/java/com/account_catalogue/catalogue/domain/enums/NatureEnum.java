package com.account_catalogue.catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum NatureEnum {
    DEBIT("Debito"),
    CREDIT("Credito"),
    EMPTY("Por defecto");

    private final String state;
    NatureEnum(String state){
        this.state=state;
    }
}
