package com.account_catalogue.accounting.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IAccountBalanceUpdateInputPort;
import com.account_catalogue.accounting.application.input.IReceiptProcessInputPort;
import com.account_catalogue.accounting.application.output.IAccountingEntryPersistenceOutputPort;
import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.application.output.IReceiptPersistenceOutputPort;
import com.account_catalogue.accounting.domain.enums.AccountingEntryStatus;
import com.account_catalogue.accounting.domain.enums.ProcessingStatus;
import com.account_catalogue.accounting.domain.enums.SourceDocumentType;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.domain.models.Receipt;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.models.ReceiptDetail;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class ReceiptProcessService implements IReceiptProcessInputPort {
        private final IAccountingSearchOutputPort accountingSearchOutputPort;
        private final IReceiptPersistenceOutputPort receiptPersistenceOutputPort;
        private final IAccountingEntryPersistenceOutputPort accountingEntryPersistenceOutputPort;
        private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
        private final IAccountBalanceUpdateInputPort accountBalanceUpdateInputPort;

        @Override
        @Transactional
        public void processReceiptCreation(Receipt receipt) {
                log.info("Iniciando procesamiento de creacion para recibo de origen ID: {}", receipt.getReceiptCode());

                // Verifica si ya existe un recibo con este código de negocio único.
                if (receiptPersistenceOutputPort.existsByReceiptCode(receipt.getReceiptCode())) {
                        log.warn("El recibo con código {} ya ha sido procesado. Ignorando mensaje duplicado.",
                                        receipt.getReceiptCode());
                        return; // Termina la ejecución. Spring AMQP enviará el ack.
                }

                receipt.setProcessingStatus(ProcessingStatus.PENDING);
                Receipt savedReceipt = receiptPersistenceOutputPort.save(receipt);
                log.info("Recibo de origen {} guardado localmente con ID: {}", savedReceipt.getOriginalReceiptId(),
                                savedReceipt.getId());

                try {
                        AccountingEntry accountingEntry = buildAccountingEntryFromReceipt(savedReceipt);
                        accountingEntryPersistenceOutputPort.save(accountingEntry);
                        log.info("Asiento contable {} generado para el recibo {}", accountingEntry.getCode(),
                                        savedReceipt.getId());

                        // Actualizar saldos de cuentas involucradas
                        accountBalanceUpdateInputPort.updateBalancesFromAccountingEntry(accountingEntry);

                        savedReceipt.setProcessingStatus(ProcessingStatus.PROCESSED);
                        receiptPersistenceOutputPort.save(savedReceipt);

                } catch (Exception e) {
                        log.error("Error crítico al procesar el asiento contable para recibo {}. Se hará rollback.",
                                        savedReceipt.getId(), e);
                        // La anotación @Transactional se encargará de revertir los cambios en la BD.
                        // Lanzamos una excepción para que Spring AMQP mueva el mensaje a la DLQ.
                        throw new IllegalStateException(
                                        "Fallo al procesar el asiento contable para el recibo " + savedReceipt.getId(),
                                        e);
                }
        }

        private AccountingEntry buildAccountingEntryFromReceipt(Receipt receipt) {
                List<AccountingMovement> movements = new ArrayList<>();

                Function<String, AccountCatalogue> findAccountByCode = code -> accountCatalogueSearchOutputPort
                                .getAccountCatalogueByCode(code, receipt.getEnterpriseId());

                AccountCatalogue accountCheck = findAccountByCode.apply(receipt.getPaymentMethodAccount().toString());
                log.info("Verificando cuenta contable para el método de pago: {}", receipt.getPaymentMethodAccount());

                if (accountCheck == null) {
                        throw new IllegalStateException(
                                        "No se encontro la cuenta contable para el código: "
                                                        + receipt.getPaymentMethodAccount());
                }

                // 1. OBTENER LA CUENTA DE DÉBITO (Caja/Banco)
                AccountCatalogue debitAccount = findAccountByCode.apply(receipt.getPaymentMethodAccount().toString());

                movements.add(AccountingMovement.builder()
                                .account(debitAccount.getId())
                                .thirdPartyId(receipt.getThirdPartyId())
                                .description("Ingreso por Recibo de Caja " + receipt.getReceiptCode())
                                .debit(receipt.getTotalAmount())
                                .credit(BigDecimal.ZERO)
                                .build());

                // 2. DETERMINAR LAS CUENTAS DE CRÉDITO SEGÚN EL TIPO DE RECIBO
                if (receipt.getReceiptTypeId() == 1L) { // TIPO: ABONO A FACTURA
                        log.info("Procesando recibo {} como ABONO A FACTURA.", receipt.getReceiptCode());
                        if (receipt.getDetails() == null || receipt.getDetails().isEmpty()) {
                                throw new IllegalStateException("Recibo tipo 'Abono' (ID 1) no tiene detalles.");
                        }
                        for (ReceiptDetail detail : receipt.getDetails()) {
                                AccountCatalogue creditAccount = findAccountByCode
                                                .apply(detail.getAccountingAccount().toString());
                                movements.add(AccountingMovement.builder()
                                                .account(creditAccount.getId())
                                                .thirdPartyId(receipt.getThirdPartyId())
                                                .description("Abono a factura " + detail.getInvoiceCode())
                                                .debit(BigDecimal.ZERO)
                                                .credit(detail.getAmountPaid())
                                                .build());
                        }
                } else if (receipt.getReceiptTypeId() == 2L) { // TIPO: INGRESO DIRECTO
                        log.info("Procesando recibo {} como INGRESO DIRECTO.", receipt.getReceiptCode());
                        if (receipt.getLedgerAccountId() == null) {
                                throw new IllegalStateException(
                                                "Recibo tipo 'Ingreso Directo' (ID 2) no tiene un ledgerAccountId.");
                        }
                        AccountCatalogue creditAccount = findAccountByCode
                                        .apply(receipt.getLedgerAccountId().toString());
                        movements.add(AccountingMovement.builder()
                                        .account(creditAccount.getId())
                                        .thirdPartyId(receipt.getThirdPartyId())
                                        .description("Ingreso directo por Recibo de Caja " + receipt.getReceiptCode())
                                        .debit(BigDecimal.ZERO)
                                        .credit(receipt.getTotalAmount())
                                        .build());
                } else {
                        throw new IllegalStateException("Tipo de recibo desconocido: " + receipt.getReceiptTypeId());
                }

                // 3. VALIDAR PARTIDA DOBLE Y CONSTRUIR EL ASIENTO
                validateDoubleEntry(movements);

                return AccountingEntry.builder()
                                .code("AE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                                .date(receipt.getIssueDate())
                                .description("Contabilización de Recibo de Caja " + receipt.getReceiptCode())
                                .status(AccountingEntryStatus.ACTIVE)
                                .sourceDocumentId(receipt.getId()) // Importante: usamos el ID del recibo guardado
                                .type(SourceDocumentType.RECEIPT.name())
                                .idEnterprise(receipt.getEnterpriseId())
                                .movements(movements)
                                .build();
        }

        @Override
        @Transactional
        public void processReceiptVoid(Receipt receiptEventData) {
                log.info("Iniciando procesamiento de ANULACIÓN para recibo con código: {}",
                                receiptEventData.getReceiptCode());

                // 1. BUSCAR EL RECIBO LOCAL por su identificador de negocio.
                Receipt localReceipt = receiptPersistenceOutputPort
                                .findByReceiptCode(receiptEventData.getReceiptCode())
                                .orElseThrow(() -> {
                                        log.error("Se recibió un evento de anulación para el recibo {}, pero no se encontró un registro local. El mensaje será descartado.",
                                                        receiptEventData.getReceiptCode());
                                        // Es un estado irrecuperable. Lanzamos excepción para mover a DLQ.
                                        return new IllegalStateException(
                                                        "No se puede anular un recibo que no fue procesado previamente: "
                                                                        + receiptEventData.getReceiptCode());
                                });

                // 2. VERIFICAR IDEMPOTENCIA: ¿Ya fue anulado?
                if (localReceipt.getProcessingStatus() == ProcessingStatus.VOIDED) {
                        log.warn("El recibo {} ya ha sido anulado previamente. Omitiendo mensaje duplicado.",
                                        localReceipt.getReceiptCode());
                        return;
                }



                // 3. BUSCAR EL ASIENTO CONTABLE ORIGINAL
                AccountingEntry originalEntry = accountingSearchOutputPort
                                .findBySourceDocumentIdAndType(localReceipt.getOriginalReceiptId(), SourceDocumentType.RECEIPT.name())
                                .orElseThrow(() -> {
                                        log.error("Inconsistencia de datos: se encontró el recibo local ID {}, pero no su asiento contable asociado.",
                                                        localReceipt.getId());
                                        return new IllegalStateException(
                                                        "No se encontró el asiento contable original para el recibo " + localReceipt.getReceiptCode());
                                });

                // 4. VERIFICAR IDEMPOTENCIA (capa extra): ¿El asiento ya está anulado?
                if (originalEntry.getStatus() == AccountingEntryStatus.VOIDED) {
                        log.warn("El asiento contable original {} ya estaba VOIDED. Sincronizando estado del recibo y finalizando.",
                                        originalEntry.getCode());
                        localReceipt.setProcessingStatus(ProcessingStatus.VOIDED);
                        localReceipt.setStatus("VOIDED");
                        receiptPersistenceOutputPort.save(localReceipt);
                        return;
                }

                // 5. REVERTIR LOS SALDOS DE LAS CUENTAS USANDO EL ASIENTO ORIGINAL
                accountBalanceUpdateInputPort.reverseBalancesFromAccountingEntry(originalEntry);
                log.info("Los saldos de las cuentas afectadas por el asiento {} han sido revertidos.",
                                originalEntry.getCode());

                // 6. ACTUALIZAR ESTADOS Y PERSISTIR (todo dentro de una transacción)
                originalEntry.setStatus(AccountingEntryStatus.VOIDED);
                accountingEntryPersistenceOutputPort.save(originalEntry);
                log.info("Asiento contable original {} marcado como VOIDED.", originalEntry.getCode());

                // La creación del asiento de reversión ha sido eliminada.

                localReceipt.setProcessingStatus(ProcessingStatus.VOIDED);
                localReceipt.setStatus("VOIDED"); // Sincroniza el estado del recibo
                receiptPersistenceOutputPort.save(localReceipt);
                log.info("Recibo local {} actualizado a estado VOIDED.", localReceipt.getId());
        }

        private void validateDoubleEntry(List<AccountingMovement> movements) {
                BigDecimal totalDebits = movements.stream()
                                .map(AccountingMovement::getDebit)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCredits = movements.stream()
                                .map(AccountingMovement::getCredit)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalDebits.compareTo(totalCredits) != 0) {
                        log.error("Partida doble no cuadra. Débitos: {}, Créditos: {}", totalDebits, totalCredits);
                        throw new IllegalStateException(
                                        "Error de partida doble: la suma de débitos no es igual a la suma de créditos.");
                }
        }
}
