package com.account_catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum financialStatusEnum {
    StatementFinancialPosition("Estado de Situación Financiero"),
    IncomeStatement("Estado de Resultados");

    private final String state;

    financialStatusEnum(String state){
        this.state=state;
    }

}
