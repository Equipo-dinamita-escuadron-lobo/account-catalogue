package com.account_catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum FinancialStatusEnum {
    INCOMESTATEMENT("Estado de Resultados"),
    STATEMENTFINANCIALPOSITION("Estado de Situación Financiero");
    private final String state;

    FinancialStatusEnum(String state){
        this.state=state;
    }
}
