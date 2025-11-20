package com.account_catalogue.accounting.domain.services;

import java.math.BigDecimal;

import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

public class BalanceCalculationStrategy {
     /**
     * Calcula y actualiza el saldo de una cuenta basándose en un movimiento.
     * @param account La cuenta cuyo saldo será actualizado.
     * @param debit El monto del débito del movimiento.
     * @param credit El monto del crédito del movimiento.
     * @return La misma instancia de la cuenta con el saldo ya actualizado.
     */
    public AccountCatalogue updateBalance(AccountCatalogue account, BigDecimal debit, BigDecimal credit) {
        BigDecimal amountToUpdate = calculateImpact(account.getNature(), debit, credit);
        BigDecimal newBalance = account.getAmount().add(amountToUpdate);
        account.setAmount(newBalance);
        return account;
    }

    /**
     * Calcula el impacto neto (positivo o negativo) de un movimiento en una cuenta.
     * - Cuentas de naturaleza DÉBITO: Suman con los débitos, restan con los créditos.
     * - Cuentas de naturaleza CRÉDITO: Suman con los créditos, restan con los débitos.
     * @param nature La naturaleza de la cuenta.
     * @param debit El monto del débito.
     * @param credit El monto del crédito.
     * @return El valor neto a sumar al saldo de la cuenta.
     */
    public BigDecimal calculateImpact(NatureEnum nature, BigDecimal debit, BigDecimal credit) {
        if (nature == NatureEnum.DEBIT) {
            return debit.subtract(credit);
        } else if (nature == NatureEnum.CREDIT) {
            return credit.subtract(debit);
        } else {
            // Un comportamiento por defecto seguro. Considera lanzar una excepción si la naturaleza NUNCA debe ser nula.
            return debit.subtract(credit);
        }
    }
}
