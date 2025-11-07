package com.account_catalogue.accounting.infraestructure.input;

import java.util.List;


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

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/accountCatalogue/portfolio")
@AllArgsConstructor
public class PortfolioController {
    private final IPortfolioSearchInputPort portfolioSearchInputPort;

    @GetMapping("/clients-summary")
    public ResponseEntity<List<ClientPortfolioSummaryResponse>> getClientsSummary(@RequestParam List<Long> clientIds) {
        return ResponseEntity.ok(portfolioSearchInputPort.getClientPortfolioSummary(clientIds));
    }

     @GetMapping("/invoices/by-client/{clientId}")
    public ResponseEntity<List<InvoiceDetailResponse>> getInvoicesByClient(@PathVariable Long clientId) {
        List<InvoiceDetailResponse> invoices = portfolioSearchInputPort.getInvoiceDetailsByClientId(clientId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/receipts/by-invoice/{invoiceId}")
    public ResponseEntity<List<ReceiptSummaryResponse>> getReceiptsByInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(portfolioSearchInputPort.findReceiptsByInvoiceId(invoiceId));
    }
}
