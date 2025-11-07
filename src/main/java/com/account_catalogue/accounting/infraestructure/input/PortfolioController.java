package com.account_catalogue.accounting.infraestructure.input;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.account_catalogue.accounting.application.input.IPortfolioSearchInputPort;
import com.account_catalogue.accounting.infraestructure.input.data.response.ClientPortfolioSummaryResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.InvoiceDetailResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.ReceiptSummaryResponse;
import com.account_catalogue.accounting.infraestructure.input.mapper.IAccountingRestMapper;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/accountCatalogue/portfolio")
@AllArgsConstructor
public class PortfolioController {
    private final IPortfolioSearchInputPort portfolioSearchInputPort;
    private final IAccountingRestMapper restMapper;

    @GetMapping("/clients-summary")
    public ResponseEntity<List<ClientPortfolioSummaryResponse>> getClientsSummary(@RequestParam List<Long> clientIds) {
        return ResponseEntity.ok(portfolioSearchInputPort.getClientPortfolioSummary(clientIds));
    }

    @GetMapping("/invoices/by-client/{clientId}")
    public ResponseEntity<List<InvoiceDetailResponse>> getInvoicesByClient(@PathVariable Long clientId) {
        List<InvoiceDetailResponse> invoices = portfolioSearchInputPort.findPendingInvoicesByClientId(clientId)
                .stream()
                .map(invoice -> {
                    // Mapeo manual para incluir el cálculo de días en mora
                    InvoiceDetailResponse response = restMapper.toInvoiceDetailResponse(invoice);

                    LocalDate today = LocalDate.now();
                    int daysInArrears = 0;

                    // Si la fecha de vencimiento es anterior a hoy, calculamos los días
                    if (invoice.getExpirationDate() != null && invoice.getExpirationDate().isBefore(today)) {
                        daysInArrears = (int) ChronoUnit.DAYS.between(invoice.getExpirationDate(), today);
                    }

                    response.setDaysInArrears(daysInArrears);
                    return response;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/receipts/by-invoice/{invoiceId}")
    public ResponseEntity<List<ReceiptSummaryResponse>> getReceiptsByInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(portfolioSearchInputPort.findReceiptsByInvoiceId(invoiceId));
    }
}
