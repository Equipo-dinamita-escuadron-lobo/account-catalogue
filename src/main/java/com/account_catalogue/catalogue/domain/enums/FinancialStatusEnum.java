package com.account_catalogue.catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum FinancialStatusEnum {
    STATEMENTFINANCIALPOSITION("Estado de Situacion Financiero"),
    INCOMESTATEMENT("Estado de Resultados");
    private final String state;

    FinancialStatusEnum(String state){
        this.state=state;
    }
}
