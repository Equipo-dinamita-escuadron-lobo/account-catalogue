package com.account_catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum NatureEnum {
    DEBIT("Debito"),
    CREDIT("Credito");

    private final String state;
    NatureEnum(String state){
        this.state=state;
    }
}
