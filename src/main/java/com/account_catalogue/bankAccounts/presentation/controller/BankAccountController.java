package com.account_catalogue.bankAccounts.presentation.controller;

import com.account_catalogue.bankAccounts.domain.model.BankAccount;
import com.account_catalogue.bankAccounts.domain.services.IBankAccountService;
import com.account_catalogue.bankAccounts.domain.mapper.BankAccountDomainMapper;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountCreateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountUpdateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.response.BankAccountRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accountCatalogue/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final IBankAccountService service;
    private final BankAccountDomainMapper mapper;

    @PostMapping("/create")
    public ResponseEntity<BankAccountRes> create(@Valid @RequestBody BankAccountCreateReq request) {
        BankAccount created = service.create(request);
        return ResponseEntity.ok(mapper.toRes(created));
    }

    @PutMapping("/update")
    public ResponseEntity<BankAccountRes> update(@Valid @RequestBody BankAccountUpdateReq request) {
        BankAccount updated = service.update(request);
        return ResponseEntity.ok(mapper.toRes(updated));
    }

    @GetMapping("/findById/{id}/{enterpriseId}")
    public ResponseEntity<BankAccountRes> getById(@PathVariable Long id, @PathVariable String enterpriseId) {
        return ResponseEntity.ok(mapper.toRes(service.findById(id, enterpriseId)));
    }

    @GetMapping("/findAll/{enterpriseId}")
    public ResponseEntity<?> list(
            @PathVariable("enterpriseId") String enterpriseId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(service.findAllByEnterprise(enterpriseId, page, size)
                .map(mapper::toRes));
    }

    @GetMapping("/findAllByStatus/{enterpriseId}")
    public ResponseEntity<?> listByStatus(
            @PathVariable("enterpriseId") String enterpriseId,
            @RequestParam Boolean status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(service.findAllByEnterpriseAndStatus(enterpriseId, status, page, size)
                .map(mapper::toRes));
    }

    @GetMapping("/findAllByBank/{enterpriseId}")
    public ResponseEntity<?> listByBank(
            @PathVariable("enterpriseId") String enterpriseId,
            @RequestParam Long bankId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(service.findAllByEnterpriseAndBank(enterpriseId, bankId, page, size)
                .map(mapper::toRes));
    }

    @PatchMapping("/changeState/{id}/{enterpriseId}")
    public ResponseEntity<BankAccountRes> changeState(
            @PathVariable Long id, 
            @PathVariable String enterpriseId,
            @RequestParam Boolean state) {
        BankAccount updated = service.changeState(id, enterpriseId, state);
        return ResponseEntity.ok(mapper.toRes(updated));
    }

    @DeleteMapping("/delete/{id}/{enterpriseId}")
    public ResponseEntity<BankAccountRes> delete(
            @PathVariable Long id, 
            @PathVariable String enterpriseId) {
        BankAccount deleted = service.delete(id, enterpriseId);
        return ResponseEntity.ok(mapper.toRes(deleted));
    }
}
