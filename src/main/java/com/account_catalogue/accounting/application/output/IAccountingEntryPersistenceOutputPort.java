package com.account_catalogue.accounting.application.output;

import java.util.Optional;

import com.account_catalogue.accounting.domain.models.AccountingEntry;

public interface IAccountingEntryPersistenceOutputPort {
    AccountingEntry save(AccountingEntry accountingEntry);

    Optional<AccountingEntry> findBySourceDocumentId(Long sourceDocumentId);
}
