package com.account_catalogue.taxes.infraestructure.adapters.input.rest;

import com.account_catalogue.commons.exceptions.taxes.InvalidAccountDigitsException;
import com.account_catalogue.commons.exceptions.taxes.InvalidDepositAccountException;
import com.account_catalogue.commons.exceptions.taxes.InvalidRefundAccountException;
import com.account_catalogue.commons.exceptions.taxes.TaxAlreadyExistsException;
import com.account_catalogue.commons.exceptions.taxes.TaxNotFoundException;
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
    ResponseEntity<?> createTax(@RequestBody TaxCreateReq taxCreateReq) {
        try {
            TaxDTO taxDTO = taxCreateRestMapper.toDomain(taxCreateReq);
            Tax tax = taxCreateInputPort.createTax(taxDTO);
            return ResponseEntity.ok(taxCreateRestMapper.toCreateResponse(tax));

        } catch (IllegalArgumentException | TaxAlreadyExistsException | InvalidDepositAccountException | InvalidRefundAccountException | InvalidAccountDigitsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{code}/{idEnterprise}")
    ResponseEntity<?> getTax(@PathVariable("code") String code, @PathVariable String idEnterprise) {
        try {
            Tax tax = taxSearchInputPort.getTax(code, idEnterprise);
            return ResponseEntity.ok(taxSearchRestMapper.toSearchResponse(tax));
        } catch (TaxNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @GetMapping("/taxes/{idEnterprise}")
    ResponseEntity<List<TaxSearchRes>> getTaxes(@PathVariable("idEnterprise") String idEnterprise) {
        try {
            List<Tax> taxes = taxSearchInputPort.getTaxes(idEnterprise);
            return ResponseEntity.ok(taxSearchRestMapper.toSearchListResponse(taxes));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateTax(@PathVariable("id") long id, @RequestBody TaxUpdateReq taxUpdateReq) {
        try {
            TaxDTO taxDTO = taxUpdateRestMapper.toDomain(taxUpdateReq);
            Tax tax = taxUpdateInputPort.update(taxDTO, id);
            return ResponseEntity.ok(taxUpdateRestMapper.toCreateResponse(tax));
        } catch (IllegalArgumentException | TaxAlreadyExistsException | InvalidDepositAccountException | InvalidRefundAccountException | InvalidAccountDigitsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/{enterpriseId}")
    ResponseEntity<?> deleteByCode(@PathVariable("id") long id, @PathVariable("enterpriseId") String enterpriseId) {
        try {
            if (taxDeleteInputPort.deleteByCode(id, enterpriseId)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Impuesto no encontrado para la empresa especificada");
            }
        } catch (TaxNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ha ocurrido un error al eliminar el impuesto.");
        }
    }

    /**
     * Cambia el estado (activo/inactivo) de un impuesto.
     * 
     * @param id el ID del impuesto
     * @param enterpriseId el ID de la empresa
     * @param status el nuevo estado (true = activo, false = inactivo)
     * @return respuesta con el estado actualizado
     */
    @PatchMapping("/changeState/{id}/{enterpriseId}")
    ResponseEntity<TaxChangeStateRes> changeState(
            @PathVariable("id") Long id,
            @PathVariable("enterpriseId") String enterpriseId,
            @RequestParam("status") Boolean status) {
        
        // Validación manual del parámetro status
        if (status == null) {
            throw new IllegalArgumentException("El parámetro 'status' es requerido");
        }
        
        Tax updatedTax = taxChangeStateInputPort.changeState(id, enterpriseId, status);
        TaxChangeStateRes response = taxChangeStateRestMapper.toChangeStateResponse(updatedTax);
        return ResponseEntity.ok(response);
    }

}
