package com.account_catalogue.domain.enums;

public enum NatureEnum {
    Debit("Debito"),
    Credit("Credito");

    private final String state;
    NatureEnum(String state){
        this.state=state;
    }
}
