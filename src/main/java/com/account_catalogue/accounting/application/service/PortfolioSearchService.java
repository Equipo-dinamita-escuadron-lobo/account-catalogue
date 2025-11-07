package com.account_catalogue.accounting.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IPortfolioSearchInputPort;
import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.infraestructure.input.data.response.ClientPortfolioSummaryResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.InvoiceDetailResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.ReceiptSummaryResponse;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.ReceiptEntity;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IReceiptDetailRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PortfolioSearchService implements IPortfolioSearchInputPort {
    private final IAccountingSearchOutputPort accountingSearchOutputPort;
    private final IReceiptDetailRepository receiptDetailRepository;

    @Override
    public List<ClientPortfolioSummaryResponse> getClientPortfolioSummary(List<Long> clientIds) {
        if (clientIds == null || clientIds.isEmpty())
            return List.of();

        // 1. Obtener TODAS las facturas pendientes para los clientes solicitados
        List<InvoiceReplica> allInvoices = accountingSearchOutputPort.findPendingInvoicesByClientIds(clientIds);

        // 2. Agrupar las facturas por el ID del cliente (thirdId)
        Map<Long, List<InvoiceReplica>> invoicesByClient = allInvoices.stream()
                .collect(Collectors.groupingBy(InvoiceReplica::getThirdId));

        // 3. Procesar el mapa para calcular los resúmenes de cada cliente
        return invoicesByClient.entrySet().stream()
                .map(entry -> {
                    Long clientId = entry.getKey();
                    List<InvoiceReplica> clientInvoices = entry.getValue();
                    LocalDate today = LocalDate.now();

                    // Calculamos el valor total de la deuda
                    BigDecimal totalDebt = clientInvoices.stream()
                            .map(inv -> BigDecimal.valueOf(inv.getPendingValue()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Calculamos la deuda vencida
                    BigDecimal overdueAmount = clientInvoices.stream()
                            .filter(inv -> inv.getExpirationDate().isBefore(today))
                            .map(inv -> BigDecimal.valueOf(inv.getPendingValue()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Calculamos la deuda por vencer
                    BigDecimal dueToAmount = totalDebt.subtract(overdueAmount);

                    

                    // 4. Construir y devolver el DTO de respuesta
                    return ClientPortfolioSummaryResponse.builder()
                            .clientId(clientId)
                            .totalDebt(totalDebt)
                            .overdueAmount(overdueAmount)
                            .dueToAmount(dueToAmount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceReplica> findPendingInvoicesByClientId(Long thirdIds) {
        return accountingSearchOutputPort.findPendingInvoicesByClientId(thirdIds);
    }

    @Override
    public List<ReceiptSummaryResponse> findReceiptsByInvoiceId(Long invoiceId) {
        return receiptDetailRepository.findByOriginalInvoiceId(invoiceId)
                .stream()
                .map(detailEntity -> {
                    ReceiptEntity receipt = detailEntity.getReceipt();
                    return ReceiptSummaryResponse.builder()
                            .id(receipt.getId())
                            .receiptCode(receipt.getReceiptCode())
                            .issueDate(receipt.getIssueDate())
                            .amountPaid(detailEntity.getAmountPaid())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<InvoiceDetailResponse> getInvoiceDetailsByClientId(Long clientId) {
    // 1. Reutilizamos el método existente para obtener los modelos de dominio.
    List<InvoiceReplica> invoices = this.findPendingInvoicesByClientId(clientId);
    
    // 2. Obtenemos la fecha actual una sola vez para eficiencia.
    LocalDate today = LocalDate.now();

    // 3. Mapeamos la lista de modelos de dominio (InvoiceReplica) a la lista de DTOs (InvoiceDetailResponse)
    return invoices.stream()
            .map(invoice -> {
                int daysInArrears = 0;
                
                // 4. Calculamos los días en mora (la misma lógica que estaba en el controlador)
                if (invoice.getExpirationDate() != null && invoice.getExpirationDate().isBefore(today)) {
                    daysInArrears = (int) ChronoUnit.DAYS.between(invoice.getExpirationDate(), today);
                }

                // 5. Construimos el DTO de respuesta
                return InvoiceDetailResponse.builder()
                        .id(invoice.getId())
                        .factCode(invoice.getFactCode())
                        .clientId(invoice.getThirdId())
                        .creationDate(invoice.getCreationDate())
                        .expirationDate(invoice.getExpirationDate())
                        .totalValue(invoice.getTotalValue())
                        .totalPay(invoice.getTotalPay())
                        .pendingValue(invoice.getPendingValue())
                        .status(invoice.getStatus().toString())
                        .daysInArrears(daysInArrears) // Asignamos el valor calculado
                        .build();
            })
            .collect(Collectors.toList());
}

}
