package com.account_catalogue.accounting.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.account_catalogue.accounting.application.input.IPortfolioSearchInputPort;
import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.infraestructure.input.data.response.ClientPortfolioSummaryResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.ReceiptSummaryResponse;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.InvoiceReplicaEntity;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.ReceiptEntity;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper.IInvoicePersistenceMapper;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IInvoiceRepository;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.repository.IReceiptDetailRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PortfolioSearchService implements IPortfolioSearchInputPort {
    private final IInvoiceRepository invoiceRepository;
    private final IInvoicePersistenceMapper invoiceMapper;
    private final IReceiptDetailRepository receiptDetailRepository;

    @Override
    public List<ClientPortfolioSummaryResponse> getClientPortfolioSummary(List<Long> clientIds) {
        if (clientIds == null || clientIds.isEmpty())
            return List.of();

        // 1. Obtener TODAS las facturas pendientes para los clientes solicitados
        List<InvoiceReplicaEntity> allInvoices = invoiceRepository.findByPendingValueGreaterThanAndThirdIdIn(0L,
                clientIds);

        // 2. Agrupar las facturas por el ID del cliente (thirdId)
        Map<Long, List<InvoiceReplicaEntity>> invoicesByClient = allInvoices.stream()
                .collect(Collectors.groupingBy(InvoiceReplicaEntity::getThirdId));

        // 3. Procesar el mapa para calcular los resúmenes de cada cliente
        return invoicesByClient.entrySet().stream()
                .map(entry -> {
                    Long clientId = entry.getKey();
                    List<InvoiceReplicaEntity> clientInvoices = entry.getValue();
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
        return invoiceMapper.toInvoiceReplicaList(
                invoiceRepository.findByThirdIdAndPendingValueGreaterThan(thirdIds, 0L));
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

}
