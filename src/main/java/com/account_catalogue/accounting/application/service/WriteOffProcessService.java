package com.account_catalogue.accounting.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IAccountBalanceUpdateInputPort;
import com.account_catalogue.accounting.application.input.IWriteOffProcessInputPort;
import com.account_catalogue.accounting.application.output.IAccountingEntryPersistenceOutputPort;
import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.application.output.IInvoiceProviderPort;
import com.account_catalogue.accounting.domain.enums.AccountingEntryStatus;
import com.account_catalogue.accounting.domain.enums.InvoiceStatus;
import com.account_catalogue.accounting.domain.enums.SourceDocumentType;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.domain.models.PortfolioWriteOff;
import com.account_catalogue.accounting.domain.models.WriteOffDetail;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import java.util.function.Function;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WriteOffProcessService implements IWriteOffProcessInputPort {
    private final IAccountingSearchOutputPort accountingSearchOutputPort;
    private final IAccountingEntryPersistenceOutputPort accountingEntryPersistencePort;
    private final IAccountBalanceUpdateInputPort accountBalanceUpdatePort;
    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchPort;
    private final IInvoiceProviderPort invoiceProviderPort;

    @Override
    @Transactional
    public void processWriteOffConfirmation(PortfolioWriteOff writeOff) {
        log.info("Iniciando procesamiento de confirmación para castigo de cartera: {}", writeOff.getCode());

        // Idempotency Check: Verifica si ya existe un asiento para este ID de origen y
        // TIPO.
        if (accountingSearchOutputPort.existsBySourceDocumentIdAndType(
                writeOff.getOriginalWriteOffId(),
                SourceDocumentType.PORTFOLIO_WRITEOFF.name())) {
            log.warn(
                    "El castigo con código {} (ID Origen: {}) ya tiene un asiento contable. Ignorando mensaje duplicado.",
                    writeOff.getCode(), writeOff.getOriginalWriteOffId());
            return;
        }

        try {

            // --- INICIO DE LÓGICA DE ACTUALIZACIÓN DE FACTURAS ---
            log.info("Castigo de cartera {} confirmado. Actualizando estado de facturas...", writeOff.getCode());
            for (WriteOffDetail detail : writeOff.getDetails()) {
                InvoiceReplica invoice = invoiceProviderPort.findInvoiceById(detail.getInvoiceId())
                        .orElseThrow(() -> new IllegalStateException("No se encontró la factura con ID "
                                + detail.getInvoiceId() + " para ser castigada."));

                invoice.setPendingValue(0L); // El saldo pendiente se reduce a cero
                invoice.setStatus(InvoiceStatus.WRITTEN_OFF); // El estado cambia a castigada

                invoiceProviderPort.updateInvoice(invoice);
                log.info("Factura {} actualizada a estado WRITTEN_OFF.", invoice.getFactCode());
            }
            // --- FIN DE LÓGICA DE ACTUALIZACIÓN DE FACTURAS ---
            AccountingEntry accountingEntry = buildAccountingEntryFromWriteOff(writeOff);
            accountingEntryPersistencePort.save(accountingEntry);
            log.info("Asiento contable {} generado para el castigo {}", accountingEntry.getCode(), writeOff.getCode());

            accountBalanceUpdatePort.updateBalancesFromAccountingEntry(accountingEntry);

        } catch (Exception e) {
            log.error("Error crítico al procesar el asiento para el castigo {}. Se hará rollback.", writeOff.getCode(),
                    e);
            // La anotación @Transactional se encargará de revertir los cambios.
            throw new IllegalStateException("Fallo al procesar el asiento para el castigo " + writeOff.getCode(), e);
        }
    }

    private AccountingEntry buildAccountingEntryFromWriteOff(PortfolioWriteOff writeOff) {
        List<AccountingMovement> movements = new ArrayList<>();
        Function<Long, AccountCatalogue> findAccountByCode = code -> accountCatalogueSearchPort
                .getAccountCatalogueByCode(String.valueOf(code), writeOff.getEnterpriseId());

        // 1. Débito a la cuenta de gasto/provisión
        AccountCatalogue debitAccount = findAccountByCode.apply(writeOff.getDebitAuxiliaryAccount());
        if (debitAccount == null) {
            throw new IllegalStateException(
                    "No se encontró la cuenta contable de débito con código: " + writeOff.getDebitAuxiliaryAccount());
        }
        movements.add(AccountingMovement.builder()
                .account(debitAccount.getId())
                .thirdPartyId(writeOff.getThirdId())
                .description("Castigo de cartera " + writeOff.getCode())
                .debit(BigDecimal.valueOf(writeOff.getTotalAmount()))
                .credit(BigDecimal.ZERO)
                .build());

        // 2. Créditos a las cuentas por cobrar de cada factura
        for (WriteOffDetail detail : writeOff.getDetails()) {
            AccountCatalogue creditAccount = findAccountByCode.apply(detail.getCreditInvoiceAccount());
            if (creditAccount == null) {
                throw new IllegalStateException("No se encontró la cuenta contable de crédito para la factura "
                        + detail.getInvoiceCode() + " con código: " + detail.getCreditInvoiceAccount());
            }
            movements.add(AccountingMovement.builder()
                    .account(creditAccount.getId())
                    .thirdPartyId(writeOff.getThirdId())
                    .description("Castigo factura " + detail.getInvoiceCode())
                    .debit(BigDecimal.ZERO)
                    .credit(detail.getAmountWrittenOff())
                    .build());
        }

        validateDoubleEntry(movements);

        // Se usa el ID original del castigo y el TIPO para la asociación polimórfica.
        return AccountingEntry.builder()
                .code("AC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .date(writeOff.getWriteOffDate())
                .description("Contabilización de Castigo de Cartera " + writeOff.getCode())
                .status(AccountingEntryStatus.ACTIVE)
                .sourceDocumentId(writeOff.getOriginalWriteOffId())
                .type(SourceDocumentType.PORTFOLIO_WRITEOFF.name())
                .idEnterprise(writeOff.getEnterpriseId())
                .movements(movements)
                .build();
    }

    @Override
    @Transactional
    public void processWriteOffAnnulment(PortfolioWriteOff writeOffEventData) {
        log.info("Iniciando procesamiento de ANULACIÓN para castigo: {}", writeOffEventData.getCode());

        // Búsqueda del asiento original usando la clave compuesta (ID + TIPO).
        AccountingEntry originalEntry = accountingSearchOutputPort
                .findBySourceDocumentIdAndType(
                        writeOffEventData.getOriginalWriteOffId(),
                        SourceDocumentType.PORTFOLIO_WRITEOFF.name())
                .orElseThrow(() -> {
                    log.error(
                            "Se recibió un evento de anulación para el castigo {}, pero no se encontró su asiento contable. El mensaje podría ser descartado.",
                            writeOffEventData.getCode());
                    return new IllegalStateException(
                            "No se puede anular un castigo que no tiene un asiento contable procesado: "
                                    + writeOffEventData.getCode());
                });

        // Idempotency Check: Verifica si el asiento ya fue anulado.
        if (originalEntry.getStatus() == AccountingEntryStatus.VOIDED) {
            log.warn("El asiento contable {} para el castigo {} ya ha sido anulado. Omitiendo mensaje duplicado.",
                    originalEntry.getCode(), writeOffEventData.getCode());
            return;
        }


        // --- INICIO DE LÓGICA DE REVERSIÓN DE ESTADO DE FACTURAS ---
        log.info("Anulando castigo de cartera {}. Reversando estado de facturas...", writeOffEventData.getCode());
        for(WriteOffDetail detail : writeOffEventData.getDetails()) {
            InvoiceReplica invoice = invoiceProviderPort.findInvoiceById(detail.getInvoiceId())
                    .orElseThrow(() -> new IllegalStateException("Inconsistencia de datos: No se encontró la factura con código " + detail.getInvoiceCode() + " para anular el castigo."));

            // El valor a restaurar es el monto que originalmente se castigó
            invoice.setPendingValue(invoice.getPendingValue() + detail.getAmountWrittenOff().longValue());
            invoice.setStatus(InvoiceStatus.PENDING); // El estado vuelve a pendiente
            
            invoiceProviderPort.updateInvoice(invoice);
            log.info("Factura {} reversada a estado PENDING. Saldo restaurado a: {}", invoice.getFactCode(), invoice.getPendingValue());
        }
        // --- FIN DE LÓGICA DE REVERSIÓN DE ESTADO DE FACTURAS ---


        accountBalanceUpdatePort.reverseBalancesFromAccountingEntry(originalEntry);
        log.info("Saldos del asiento {} revertidos.", originalEntry.getCode());

        originalEntry.setStatus(AccountingEntryStatus.VOIDED);
        accountingEntryPersistencePort.save(originalEntry);
        log.info("Asiento contable {} del castigo {} marcado como anulado.", originalEntry.getCode(),
                writeOffEventData.getCode());
    }

    private void validateDoubleEntry(List<AccountingMovement> movements) {
        BigDecimal totalDebits = movements.stream().map(AccountingMovement::getDebit).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        BigDecimal totalCredits = movements.stream().map(AccountingMovement::getCredit).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        if (totalDebits.compareTo(totalCredits) != 0) {
            log.error("Error de Partida Doble. Débitos: {}, Créditos: {}", totalDebits, totalCredits);
            throw new IllegalStateException("La suma de débitos no es igual a la suma de créditos.");
        }
    }

    @Override
    public void writeOffInvoices(List<Long> invoiceIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeOffInvoices'");
    }

}
