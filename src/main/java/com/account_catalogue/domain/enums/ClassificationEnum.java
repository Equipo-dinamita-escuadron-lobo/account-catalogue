package com.account_catalogue.domain.enums;

import lombok.Getter;

@Getter
public enum ClassificationEnum {
    CURRENTASSETS("Activo Corriente"),
    NONCURRENTASSETS("Activo No Corriente"),
    CURRENTLIABILITIES("Pasivo Corriente"),
    NONCURRENTLIABILITIES("Pasivo No Corriente"),
    EQUITY("Patrimonio"),
    OPERATINGREVENUES("Ingresos Operacionales"),
    NONOPERATINGINCOME("Ingresos No Operacionales"),
    OPERATINGEXPENSES("Gastos Operacionales"),
    OPERATINGINCOME("Ingresos Operacionales");

    private final String state;

    ClassificationEnum(String state){
     this.state=state;
    }
}
