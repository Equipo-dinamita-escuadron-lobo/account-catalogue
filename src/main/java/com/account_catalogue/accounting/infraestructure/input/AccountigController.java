package com.account_catalogue.accounting.infraestructure.input;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.account_catalogue.accounting.application.input.IAccountingSearchInputPort;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.infraestructure.input.data.response.AccountingEntryResponse;
import com.account_catalogue.accounting.infraestructure.input.data.response.AccountingMovementResponse;
import com.account_catalogue.accounting.infraestructure.input.mapper.IAccountingRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accountCatalogue/accounting")
@RequiredArgsConstructor
public class AccountigController {
    private final IAccountingSearchInputPort accountingSearchInputPort;
    private final IAccountingRestMapper accountingRestMapper;


    @GetMapping("/entries/{id}")
    public ResponseEntity<AccountingEntryResponse> getEntryById(@PathVariable Long id) {
        AccountingEntry entry = accountingSearchInputPort.findAccountingEntryById(id);
        return ResponseEntity.ok(accountingRestMapper.toEntryResponse(entry));
    }

    @GetMapping("/entries/by-receipt/{receiptId}")
    public ResponseEntity<AccountingEntryResponse> getEntryByReceiptId(@PathVariable Long receiptId) {
        AccountingEntry entry = accountingSearchInputPort.findAccountingEntryByReceiptId(receiptId);
        return ResponseEntity.ok(accountingRestMapper.toEntryResponse(entry));
    }

     @GetMapping("/entries/by-source/{sourceDocumentId}/{type}")
    public ResponseEntity<AccountingEntryResponse> getEntryBySourceDocumentIdAndType(@PathVariable Long sourceDocumentId, @PathVariable String type) {
        AccountingEntry entry = accountingSearchInputPort.findAccountingEntryBySourceDocumentIdAndType(sourceDocumentId, type);
        return ResponseEntity.ok(accountingRestMapper.toEntryResponse(entry));
    }

    @GetMapping("/movements/by-account/{accountId}")
    public ResponseEntity<List<AccountingMovementResponse>> getMovementsByAccount(@PathVariable Long accountId) {
        List<AccountingMovement> movements = accountingSearchInputPort.findMovementsByAccountId(accountId);
        List<AccountingMovementResponse> response = movements.stream()
                .map(accountingRestMapper::toMovementResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movements/by-third-party/{thirdPartyId}")
    public ResponseEntity<List<AccountingMovementResponse>> getMovementsByThirdParty(@PathVariable Long thirdPartyId) {
        List<AccountingMovement> movements = accountingSearchInputPort.findMovementsByThirdPartyId(thirdPartyId);
        List<AccountingMovementResponse> response = movements.stream()
                .map(accountingRestMapper::toMovementResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }    

    

   

}
