package com.account_catalogue.accounting.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IAccountingSearchInputPort;
import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AccountingSearchService implements IAccountingSearchInputPort {

    private final IAccountingSearchOutputPort accountingSearchOutputPort;


    @Override
    public AccountingEntry findAccountingEntryById(Long id) {
        return accountingSearchOutputPort.findById(id)
                .orElseThrow();
    }

    @Override
    public AccountingEntry findAccountingEntryByReceiptId(Long receiptId) {
        return accountingSearchOutputPort.findByReceiptId(receiptId)
                .orElseThrow();
    }

    @Override
    public List<AccountingMovement> findMovementsByAccountId(Long accountId) {
        return accountingSearchOutputPort.findMovementsByAccountId(accountId);
    }

    @Override
    public List<AccountingMovement> findMovementsByThirdPartyId(Long thirdPartyId) {
        return accountingSearchOutputPort.findMovementsByThirdPartyId(thirdPartyId);
    }

    @Override
    public AccountingEntry findAccountingEntryBySourceDocumentIdAndType(Long sourceDocumentId, String type) {
        return accountingSearchOutputPort.findBySourceDocumentIdAndType(sourceDocumentId, type)
                .orElseThrow();
    }
}
