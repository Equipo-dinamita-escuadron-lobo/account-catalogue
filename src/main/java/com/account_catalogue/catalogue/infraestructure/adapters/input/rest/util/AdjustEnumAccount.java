package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.util;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;

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
     * - Ingresos Operacionales -> OPERATINGREVENUES
     * 
     * @param state la clasificación de cadena.
     * @return la ClassificationEnum equivalente.
     * @throws IllegalArgumentException si el estado no es válido.
     */
    public ClassificationEnum adjustClassificationEnum(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("La clasificación no puede ser nula o vacía");
        }

        switch (state.trim()) {
            case "Activo Corriente":
                return ClassificationEnum.CURRENTASSETS;
            case "Activo No Corriente":
                return ClassificationEnum.NONCURRENTASSETS;
            case "Pasivo Corriente":
                return ClassificationEnum.CURRENTLIABILITIES;
            case "Pasivo No Corriente":
                return ClassificationEnum.NONCURRENTLIABILITIES;
            case "Patrimonio":
                return ClassificationEnum.EQUITY;
            case "Ingresos No Operacionales":
                return ClassificationEnum.NONOPERATINGINCOME;
            case "Gastos Operacionales":
                return ClassificationEnum.OPERATINGEXPENSES;
            case "Ingresos Operacionales":
                return ClassificationEnum.OPERATINGREVENUES;
            default:
                throw new IllegalArgumentException("Clasificación no válida: " + state);
        }
    }

    /**
     * Ajusta el estado financiero de cadena a la correspondiente FinancialStatusEnum.
     * Los valores posibles son:
     * - Estado de Resultados -> INCOMESTATEMENT
     * - Estado de Situacion Financiero -> STATEMENTFINANCIALPOSITION
     * 
     * @param state el estado financiero de cadena.
     * @return la FinancialStatusEnum equivalente.
     * @throws IllegalArgumentException si el estado no es válido.
     */
    public FinancialStatusEnum adjustFinancialStatusEnum(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado financiero no puede ser nulo o vacío");
        }

        switch (state.trim()) {
            case "Estado de Resultados":
                return FinancialStatusEnum.INCOMESTATEMENT;
            case "Estado de Situacion Financiero":
                return FinancialStatusEnum.STATEMENTFINANCIALPOSITION;
            default:
                throw new IllegalArgumentException("Estado financiero no válido: " + state);
        }
    }


    /**
     * Ajusta la naturaleza de cadena a la correspondiente NatureEnum.
     * Los valores posibles son:
     * - Crédito -> CREDIT
     * - Débito -> DEBIT
     * 
     * @param state la naturaleza de cadena.
     * @return la NatureEnum equivalente.
     * @throws IllegalArgumentException si el estado no es válido.
     */
    public NatureEnum adjustNatureEnum(String state) {
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("La naturaleza no puede ser nula o vacía");
        }

        switch (state.trim()) {
            case "Credito":
                return NatureEnum.CREDIT;
            case "Debito":
                return NatureEnum.DEBIT;
            default:
                throw new IllegalArgumentException("Naturaleza no válida: " + state);
        }
    }

}
