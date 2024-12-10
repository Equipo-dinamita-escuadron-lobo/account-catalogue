package com.account_catalogue.infraestructure.adapters.input.rest.util;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdjustEnumAccount {

    /**
     * Ajusta la clasificación de cadena a la correspondiente ClassificationEnum.
     * Los valores posibles son:
     * - Activo Corriente -> CURRENTASSETS
     * - Activo No Corriente -> NONCURRENTASSETS
     * - Pasivo Corriente -> CURRENTLIABILITIES
     * - Pasivo No Corriente -> NONCURRENTLIABILITIES
     * - Patrimonio -> EQUITY
     * - Ingresos No Operacionales -> NONOPERATINGINCOME
     * - Gastos Operacionales -> OPERATINGEXPENSES
     * - Ingresos Operacionales -> OPERATINGINCOME
     * - Cualquier otro valor -> EMPTY
     * 
     * @param state la clasificación de cadena.
     * @return la ClassificationEnum equivalente.
     */
    public ClassificationEnum adjustClassificationEnum(String state) {

        ClassificationEnum enumState;
        switch (state) {
            case "Activo Corriente":
                enumState = ClassificationEnum.CURRENTASSETS;
                break;
            case "Activo No Corriente":
                enumState = ClassificationEnum.NONCURRENTASSETS;
                break;
            case "Pasivo Corriente":
                enumState = ClassificationEnum.CURRENTLIABILITIES;
                break;
            case "Pasivo No Corriente":
                enumState = ClassificationEnum.NONCURRENTLIABILITIES;
                break;
            case "Patrimonio":
                enumState = ClassificationEnum.EQUITY;
                break;
            case "Ingresos No Operacionales":
                enumState = ClassificationEnum.NONOPERATINGINCOME;
                break;
            case "Gastos Operacionales":
                enumState = ClassificationEnum.OPERATINGEXPENSES;
                break;
            case "Ingresos Operacionales":
                enumState = ClassificationEnum.OPERATINGINCOME;
                break;

            default:
                enumState = ClassificationEnum.EMPTY;

        }

        return enumState;
    }

    /**
     * Ajusta el estado financiero de cadena a la correspondiente FinancialStatusEnum.
     * Los valores posibles son:
     * - Estado de Resultados -> INCOMESTATEMENT
     * - Estado de Situacion Financiero -> STATEMENTFINANCIALPOSITION
     * - Cualquier otro valor -> EMPTY
     * 
     * @param state el estado financiero de cadena.
     * @return la FinancialStatusEnum equivalente.
     */
    public FinancialStatusEnum adjustFinancialStatusEnum(String state) {
        FinancialStatusEnum enumState;
        switch (state) {
            case "Estado de Resultados":
                enumState = FinancialStatusEnum.INCOMESTATEMENT;
                break;
            case "Estado de Situacion Financiero":
                enumState = FinancialStatusEnum.STATEMENTFINANCIALPOSITION;
                break;
            default:
                enumState = FinancialStatusEnum.EMPTY;

        }
        return enumState;
    }


    /**
     * Ajusta la naturaleza de cadena a la correspondiente NatureEnum.
     * Los valores posibles son:
     * - Crédito -> CREDIT
     * - Débito -> DEBIT
     * - Cualquier otro valor -> EMPTY
     * 
     * @param state la naturaleza de cadena.
     * @return la NatureEnum equivalente.
     */
    public NatureEnum adjustNatureEnum(String state) {
        NatureEnum enumState;
        switch (state) {
            case "Crédito":
                enumState = NatureEnum.CREDIT;
                break;
            case "Débito":
                enumState = NatureEnum.DEBIT;
                break;
            default:
                enumState = NatureEnum.EMPTY;

        }
        return enumState;
    }

}
