package com.account_catalogue.taxes.infraestructure.adapters.input.rest;

import com.account_catalogue.taxes.application.input.ITaxChangeStateInputPort;
import com.account_catalogue.taxes.application.input.ITaxCreateInputPort;
import com.account_catalogue.taxes.application.input.ITaxDeleteInputPort;
import com.account_catalogue.taxes.application.input.ITaxSearchInputPort;
import com.account_catalogue.taxes.application.input.ITaxUpdateInputPort;
import com.account_catalogue.taxes.domain.DTO.TaxDTO;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.request.TaxUpdateReq;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response.TaxChangeStateRes;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.data.response.TaxSearchRes;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxChangeStateRestMapper;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxCreateRestMapper;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxSearchRestMapper;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxUpdateRestMapper;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/tax")
@RestController
@AllArgsConstructor
public class TaxController {

    private final ITaxCreateRestMapper taxCreateRestMapper;
    private final ITaxSearchRestMapper taxSearchRestMapper;
    private final ITaxCreateInputPort taxCreateInputPort;
    private final ITaxUpdateInputPort taxUpdateInputPort;
    private final ITaxSearchInputPort taxSearchInputPort;
    private final ITaxUpdateRestMapper taxUpdateRestMapper;
    private final ITaxDeleteInputPort taxDeleteInputPort;
    private final ITaxChangeStateInputPort taxChangeStateInputPort;
    private final ITaxChangeStateRestMapper taxChangeStateRestMapper;

    @PostMapping("/")
    ResponseEntity<?> createTax(@RequestBody @Valid TaxCreateReq taxCreateReq) {
        TaxDTO taxDTO = taxCreateRestMapper.toDomain(taxCreateReq);
        Tax tax = taxCreateInputPort.createTax(taxDTO);
        return ResponseEntity.ok(taxCreateRestMapper.toCreateResponse(tax));
    }

    @GetMapping("/{code}/{idEnterprise}")
    ResponseEntity<?> getTax(@PathVariable String code, @PathVariable String idEnterprise) {
        Tax tax = taxSearchInputPort.getTax(code, idEnterprise);
        return ResponseEntity.ok(taxSearchRestMapper.toSearchResponse(tax));
    }

    @GetMapping("/taxes/{idEnterprise}")
    ResponseEntity<List<TaxSearchRes>> getTaxes(@PathVariable String idEnterprise) {
        List<Tax> taxes = taxSearchInputPort.getTaxes(idEnterprise);
        return ResponseEntity.ok(taxSearchRestMapper.toSearchListResponse(taxes));
    }

    @GetMapping("/active/{idEnterprise}")
    ResponseEntity<List<TaxSearchRes>> getActiveTaxes(@PathVariable String idEnterprise) {
        List<Tax> activeTaxes = taxSearchInputPort.getActiveTaxes(idEnterprise);
        return ResponseEntity.ok(taxSearchRestMapper.toSearchListResponse(activeTaxes));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateTax(@PathVariable long id, @RequestBody @Valid TaxUpdateReq taxUpdateReq) {
        TaxDTO taxDTO = taxUpdateRestMapper.toDomain(taxUpdateReq);
        Tax tax = taxUpdateInputPort.update(taxDTO, id);
        return ResponseEntity.ok(taxUpdateRestMapper.toCreateResponse(tax));
    }

    @DeleteMapping("/{id}/{enterpriseId}")
    ResponseEntity<?> deleteByCode(@PathVariable long id, @PathVariable String enterpriseId) {
        if (taxDeleteInputPort.deleteByCode(id, enterpriseId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Impuesto no encontrado para la empresa especificada");
        }
    }

    @PatchMapping("/changeState/{id}/{enterpriseId}")
    ResponseEntity<TaxChangeStateRes> changeState(
            @PathVariable Long id,
            @PathVariable String enterpriseId,
            @RequestParam Boolean status) {

        Tax updatedTax = taxChangeStateInputPort.changeState(id, enterpriseId, status);
        TaxChangeStateRes response = taxChangeStateRestMapper.toChangeStateResponse(updatedTax);
        return ResponseEntity.ok(response);
    }

}
