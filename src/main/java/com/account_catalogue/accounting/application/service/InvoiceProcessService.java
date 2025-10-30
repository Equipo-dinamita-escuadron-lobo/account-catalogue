package com.account_catalogue.accounting.application.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IInvoiceProcessInputPort;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.InvoiceSyncDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceProcessService implements IInvoiceProcessInputPort {
    
    // Inyectamos el servicio que ya sabe cómo actualizar saldos jerárquicamente.
    private final AccountBalanceUpdateService accountBalanceUpdateService;

    @Override
    public void processInvoiceCreation(InvoiceSyncDto invoiceDto) {
        log.info("Iniciando actualización de saldos para la nueva factura {}", invoiceDto.getFactCode());

        if (invoiceDto.getAccountingAccount() == null || invoiceDto.getPendingValue() == null) {
            log.error("La factura {} no tiene una cuenta contable o un valor pendiente para procesar.", invoiceDto.getFactCode());
            // Considera lanzar una excepción si esto no debería ocurrir.
            return;
        }

        Long accountId = invoiceDto.getAccountingAccount();
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
}
