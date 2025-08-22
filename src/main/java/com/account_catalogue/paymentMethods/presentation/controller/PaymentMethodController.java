package com.account_catalogue.paymentMethods.presentation.controller;

import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.domain.services.IPaymentMethodService;
import com.account_catalogue.paymentMethods.domain.mapper.PaymentMethodDomainMapper;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodCreateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.response.PaymentMethodRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accountCatalogue/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final IPaymentMethodService service;
    private final PaymentMethodDomainMapper mapper;

    @PostMapping("/create")
    public ResponseEntity<PaymentMethodRes> create(@Valid @RequestBody PaymentMethodCreateReq request) {
        PaymentMethod created = service.create(request);
        return ResponseEntity.ok(mapper.toRes(created));
    }

    @PutMapping("/update")
    public ResponseEntity<PaymentMethodRes> update(@Valid @RequestBody PaymentMethodUpdateReq request) {
        PaymentMethod updated = service.update(request);
        return ResponseEntity.ok(mapper.toRes(updated));
    }

    @GetMapping("/findById/{id}/{enterpriseId}")
    public ResponseEntity<PaymentMethodRes> getById(@PathVariable Long id, @PathVariable String enterpriseId) {
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

    @PatchMapping("/changeState/{id}/{enterpriseId}")
    public ResponseEntity<PaymentMethodRes> changeState(
            @PathVariable Long id, 
            @PathVariable String enterpriseId,
            @RequestParam Boolean state) {
        PaymentMethod updated = service.changeState(id, enterpriseId, state);
        return ResponseEntity.ok(mapper.toRes(updated));
    }
}
