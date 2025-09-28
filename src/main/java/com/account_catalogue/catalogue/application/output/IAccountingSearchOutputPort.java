package com.account_catalogue.catalogue.application.output;

import java.util.List;
import java.util.Optional;

import com.account_catalogue.catalogue.domain.models.AccountingEntry;
import com.account_catalogue.catalogue.domain.models.AccountingMovement;

public interface IAccountingSearchOutputPort {
    Optional<AccountingEntry> findById(Long id);

    Optional<AccountingEntry> findByReceiptId(Long receiptId);

    List<AccountingMovement> findMovementsByAccountId(Long accountId);
    
    List<AccountingMovement> findMovementsByThirdPartyId(Long thirdPartyId);
}
