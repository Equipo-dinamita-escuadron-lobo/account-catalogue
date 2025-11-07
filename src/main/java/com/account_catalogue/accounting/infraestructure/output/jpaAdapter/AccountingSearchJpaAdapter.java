package com.account_catalogue.accounting.infraestructure.output.jpaAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.domain.models.ReceiptDetail;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.InvoiceReplicaEntity;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper.IAccountingEntryMapper;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper.IAccountingMovementMapper;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper.IInvoicePersistenceMapper;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper.IReceiptDetailMapper;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IAccountingEntryRepository;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IAccountingMovementRepository;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IInvoiceRepository;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IReceiptDetailRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountingSearchJpaAdapter implements IAccountingSearchOutputPort {
    private final IAccountingEntryRepository  accountingEntryRepository;
    private final IAccountingMovementRepository movementRepository;
    private final IAccountingEntryMapper accountingEntryMapper;
    private final IAccountingMovementMapper movementMapper;
    private final IInvoicePersistenceMapper invoiceMapper;
    private final IInvoiceRepository invoiceRepository;
    private final IReceiptDetailMapper receiptDetailMapper;
    private final IReceiptDetailRepository receiptDetailRepository;


    @Override
    public Optional<AccountingEntry> findById(Long id) {
        return  accountingEntryRepository.findByIdWithMovements(id).map(accountingEntryMapper::toDomain);
    }

    @Override
    public Optional<AccountingEntry> findByReceiptId(Long receiptId) {
        return  accountingEntryRepository.findBySourceDocumentId(receiptId).map(accountingEntryMapper::toDomain);
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

    @Override
    public Optional<AccountingEntry> findBySourceDocumentIdAndType(Long sourceDocumentId, String type) {
        return  accountingEntryRepository.findBySourceDocumentIdAndType(sourceDocumentId, type)
            .map(accountingEntryMapper::toDomain);
            
    }

    @Override
    public boolean existsBySourceDocumentIdAndType(Long sourceDocumentId, String type) {
        return  accountingEntryRepository.existsBySourceDocumentIdAndType(sourceDocumentId, type);
    }

     @Override
    public List<InvoiceReplica> findPendingInvoicesByClientIds(List<Long> clientIds) {
        List<InvoiceReplicaEntity> entities = invoiceRepository.findByPendingValueGreaterThanAndThirdIdIn(0L, clientIds);
        return invoiceMapper.toInvoiceReplicaList(entities);
    }

    @Override
    public List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId) {
        List<InvoiceReplicaEntity> entities = invoiceRepository.findByThirdIdAndPendingValueGreaterThan(clientId, 0L);
        return invoiceMapper.toInvoiceReplicaList(entities);
    }

    @Override
    public List<ReceiptDetail> findReceiptDetailsByInvoiceId(Long invoiceId) {
        // Asumiendo que IReceiptDetailMapper tiene un método toReceiptDetailList
        return receiptDetailMapper.toReceiptDetailList(
            receiptDetailRepository.findByOriginalInvoiceId(invoiceId)
        );
    }


}
