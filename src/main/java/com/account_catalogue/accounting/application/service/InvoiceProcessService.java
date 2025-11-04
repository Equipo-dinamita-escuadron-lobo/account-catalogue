package com.account_catalogue.accounting.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IInvoiceProcessInputPort;
import com.account_catalogue.accounting.application.output.IInvoiceProviderPort;
import com.account_catalogue.accounting.domain.enums.InvoiceStatus;
import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.InvoiceSyncDto;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.accounting.domain.exception.InvoiceNotFoundException;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceProcessService implements IInvoiceProcessInputPort {
    
    // Inyectamos el servicio que ya sabe cómo actualizar saldos jerárquicamente.
    private final AccountBalanceUpdateService accountBalanceUpdateService;
    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    private final IInvoiceProviderPort invoiceProviderPort;
   

    @Override
    @Transactional
    public void processInvoiceCreation(InvoiceSyncDto invoiceDto) {
        log.info("Iniciando actualización de saldos para la nueva factura {}", invoiceDto.getFactCode());

        if (invoiceDto.getAccountingAccount() == null || invoiceDto.getPendingValue() == null) {
            log.error("La factura {} no tiene una cuenta contable o un valor pendiente para procesar.", invoiceDto.getFactCode());
            // Considera lanzar una excepción si esto no debería ocurrir.
            return;
        }

        // Buscar cuenta contable por codigo
        AccountCatalogue accountCatalogue = accountCatalogueSearchOutputPort.getAccountCatalogueByCode(invoiceDto.getAccountingAccount().toString(), invoiceDto.getEntId());
        if (accountCatalogue == null) {
            log.error("No se encontró la cuenta contable con ID: {} para la factura {}. Se omite la actualización de saldo.", invoiceDto.getAccountingAccount(), invoiceDto.getFactCode());
            // Considera lanzar una excepción si esto es un estado irrecuperable
            return;
        }


        Long accountId = accountCatalogue.getId();
        String enterpriseId = invoiceDto.getEntId();
        
        // Convertimos el valor pendiente (Long) a BigDecimal.
        BigDecimal amount = new BigDecimal(invoiceDto.getPendingValue());

        // Lógica Contable: Una nueva factura de venta (cuenta por cobrar) es un activo.
        // El aumento de un activo se registra como un DÉBITO.
        // Por lo tanto, pasamos el monto pendiente en el parámetro de débito.
        BigDecimal debitAmount = amount;
        BigDecimal creditAmount = BigDecimal.ZERO;

        try {
            accountBalanceUpdateService.updateSingleAccountHierarchy(accountId, debitAmount, creditAmount, enterpriseId);
            log.info("Saldos actualizados correctamente para la jerarquía de la cuenta {} debido a la factura {}", accountId, invoiceDto.getFactCode());
        } catch (Exception e) {
            log.error("Error crítico al actualizar el saldo para la cuenta {} de la factura {}. Error: {}", accountId, invoiceDto.getFactCode(), e.getMessage(), e);
            // Lanzamos una excepción para que la transacción se revierta y el mensaje pueda ir a una DLQ.
            throw new IllegalStateException("Fallo al actualizar el saldo de la cuenta para la factura " + invoiceDto.getFactCode(), e);
        }
    }

    @Override
    public void updateDueDate(Long invoiceId, LocalDate newDueDate) {
        InvoiceReplica invoice = invoiceProviderPort.findInvoiceById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("No se encontró la factura con ID: " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) 
            throw new IllegalStateException("No se puede cambiar la fecha de vencimiento de una factura ya pagada.");

        invoice.setExpirationDate(newDueDate);

        invoice.validateDates();

        invoiceProviderPort.updateInvoice(invoice);
    }

    @Override
    public List<InvoiceReplica> findPendingInvoicesByClientId(Long clientId) {
       return invoiceProviderPort.findPendingInvoicesByClientId(clientId);
    }

    @Override
    public List<InvoiceReplica> findInvoicesByEnterpriseId(String enterpriseId) {
        return invoiceProviderPort.findInvoicesByEnterpriseId(enterpriseId);
    }

    @Override
    public InvoiceReplica findInvoiceById(Long invoiceId) {
        return invoiceProviderPort.findInvoiceById(invoiceId).
        orElseThrow(() -> new RuntimeException("No se encontró la factura con ID: " + invoiceId));
    }
}
