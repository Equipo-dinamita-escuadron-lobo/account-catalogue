package com.account_catalogue.accounting.application.output;

import java.util.List;
import java.util.Optional;

import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;

public interface IAccountingSearchOutputPort {
    Optional<AccountingEntry> findById(Long id);

    Optional<AccountingEntry> findByReceiptId(Long receiptId);

    List<AccountingMovement> findMovementsByAccountId(Long accountId);
    
    List<AccountingMovement> findMovementsByThirdPartyId(Long thirdPartyId);
}
