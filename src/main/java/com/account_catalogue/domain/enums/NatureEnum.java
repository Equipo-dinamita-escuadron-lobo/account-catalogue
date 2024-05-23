package com.account_catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum NatureEnum {
    DEBIT("Débito"),
    CREDIT("Credíto"),
    EMPTY("Por defecto");

    private final String state;
    NatureEnum(String state){
        this.state=state;
    }
}
