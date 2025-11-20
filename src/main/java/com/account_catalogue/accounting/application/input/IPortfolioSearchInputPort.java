package com.account_catalogue.accounting.application.input;

import java.time.LocalDate;
import java.util.List;

import com.account_catalogue.accounting.domain.models.InvoiceReplica;
import com.account_catalogue.accounting.infraestructure.input.data.response.ClientPortfolioSummaryResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.ReceiptSummaryResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.InvoiceDetailResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.PortfolioAgingAccountResponse;

public interface IPortfolioSearchInputPort {
    List<ClientPortfolioSummaryResponse> getClientPortfolioSummary(List<Long> clientIds);
    List<InvoiceReplica> findPendingInvoicesByClientId(Long thirdIds);
    List<ReceiptSummaryResponse> findReceiptsByInvoiceId(Long invoiceId);
    List<InvoiceDetailResponse> getInvoiceDetailsByClientId(Long clientId);
    List<PortfolioAgingAccountResponse> getPortfolioAgingReport(Long clientId, LocalDate cutoffDate, String enterpriseId);
}
