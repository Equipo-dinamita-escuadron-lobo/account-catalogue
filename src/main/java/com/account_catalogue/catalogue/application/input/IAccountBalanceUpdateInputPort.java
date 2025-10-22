package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.domain.models.AccountingEntry;

public interface IAccountBalanceUpdateInputPort {
    /**
     * Actualiza los saldos de todas las cuentas contables involucradas en un asiento contable.
     * Este método se encarga de la lógica de naturaleza (débito/crédito) y la actualización jerárquica.
     * @param accountingEntry El asiento contable que contiene los movimientos a procesar.
     */
    void updateBalancesFromAccountingEntry(AccountingEntry accountingEntry);
}
