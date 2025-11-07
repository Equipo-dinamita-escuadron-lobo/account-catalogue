package com.account_catalogue.accounting.application.output;

import java.util.List;
import java.util.Optional;

import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.domain.models.ReceiptDetail;

public interface IAccountingSearchOutputPort {
    Optional<AccountingEntry> findById(Long id);

    Optional<AccountingEntry> findByReceiptId(Long receiptId);

    List<AccountingMovement> findMovementsByAccountId(Long accountId);
    
    List<AccountingMovement> findMovementsByThirdPartyId(Long thirdPartyId);

    /**
     * Busca un asiento contable por el ID y TIPO del documento de origen.
     */
    Optional<AccountingEntry> findBySourceDocumentIdAndType(Long sourceDocumentId, String type);

    /**
     * Verifica si existe un asiento contable asociado a un ID y TIPO de documento de origen.
     */
    boolean existsBySourceDocumentIdAndType(Long sourceDocumentId, String type);

     /**
     * Busca todas las facturas con saldo pendiente para una lista de IDs de clientes.
     */
    List<InvoiceReplica> findPendingInvoicesByClientIds(List<Long> clientIds);

    /**
     * Busca todas las facturas con saldo pendiente para un cliente específico.
     */
    List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId);

    /**
     * Busca todos los detalles de pago (abonos) aplicados a una factura específica.
     */
    List<ReceiptDetail> findReceiptDetailsByInvoiceId(Long invoiceId);
}
