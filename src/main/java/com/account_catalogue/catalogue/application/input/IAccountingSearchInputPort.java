package com.account_catalogue.catalogue.application.input;

import java.util.List;

import com.account_catalogue.catalogue.domain.models.AccountingEntry;
import com.account_catalogue.catalogue.domain.models.AccountingMovement;

public interface IAccountingSearchInputPort {

    AccountingEntry findAccountingEntryById(Long id);
    
    AccountingEntry findAccountingEntryByReceiptId(Long receiptId);

    List<AccountingMovement> findMovementsByAccountId(Long accountId);

    List<AccountingMovement> findMovementsByThirdPartyId(Long thirdPartyId);
    
} 
