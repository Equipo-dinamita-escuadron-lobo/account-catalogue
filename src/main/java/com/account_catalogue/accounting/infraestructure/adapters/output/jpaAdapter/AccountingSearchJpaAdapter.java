package com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter.mapper.IAccountingEntryMapper;
import com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter.mapper.IAccountingMovementMapper;
import com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter.repository.IAccountingEntryRepository;
import com.account_catalogue.accounting.infraestructure.adapters.output.jpaAdapter.repository.IAccountingMovementRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountingSearchJpaAdapter implements IAccountingSearchOutputPort {
    private final IAccountingEntryRepository entryRepository;
    private final IAccountingMovementRepository movementRepository;
    private final IAccountingEntryMapper entryMapper;
    private final IAccountingMovementMapper movementMapper;

    @Override
    public Optional<AccountingEntry> findById(Long id) {
        return entryRepository.findByIdWithMovements(id).map(entryMapper::toDomain);
    }

    @Override
    public Optional<AccountingEntry> findByReceiptId(Long receiptId) {
        return entryRepository.findBySourceDocumentId(receiptId).map(entryMapper::toDomain);
    }

    @Override
    public List<AccountingMovement> findMovementsByAccountId(Long accountId) {
        return movementRepository.findByAccount(accountId).stream()
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountingMovement> findMovementsByThirdPartyId(Long thirdPartyId) {
        return movementRepository.findByThirdPartyId(thirdPartyId).stream()
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }
}
