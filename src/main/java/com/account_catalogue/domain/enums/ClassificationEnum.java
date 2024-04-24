package com.account_catalogue.domain.enums;

public enum ClassificationEnum {
   	CurrentAssets("Activo Corriente"),
    NonCurrentAssets("Activo No Corriente"),
    CurrentLiabilities("Pasivo Corriente"),
    NonCurrentLiabilities("Pasivo No Corriente"),
    equity("patrimonio"),
    OperatingRevenues("Ingresos Operacionales"),
    NonoperatingIncome("Ingresos No Operacionales"),
    OperatingExpenses("Gastos Operacionales"),
    OperatingIncome("Ingresos Operacionales");

    private final String state;

    ClassificationEnum(String state){
     this.state=state;
    }
}
