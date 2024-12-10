package com.account_catalogue.infraestructure.adapters.input.rest;

import com.account_catalogue.application.input.ITaxCreateInputPort;
import com.account_catalogue.application.input.ITaxDeleteInputPort;
import com.account_catalogue.application.input.ITaxSearchInputPort;
import com.account_catalogue.application.input.ITaxUpdateInputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxCreateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxUpdateRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/tax")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TaxController {

    private final ITaxCreateRestMapper taxCreateRestMapper;
    private final ITaxSearchRestMapper taxSearchRestMapper;
    private final ITaxCreateInputPort taxCreateInputPort;
    private final ITaxUpdateInputPort taxUpdateInputPort;
    private final ITaxSearchInputPort taxSearchInputPort;
    private final ITaxUpdateRestMapper taxUpdateRestMapper;
    private final ITaxDeleteInputPort taxDeleteInputPort;

    @PostMapping("/")
    @Operation(summary = "Crear un nuevo impuesto", description = "Este endpoint permite crear un nuevo impuesto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Impuesto creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaxCreateRes.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    ResponseEntity<?> createTax(@RequestBody TaxCreateReq taxCreateReq) {
        try {
            TaxDTO taxDTO = taxCreateRestMapper.toDomain(taxCreateReq);
            Tax tax = taxCreateInputPort.createTax(taxDTO);
            return ResponseEntity.ok(taxCreateRestMapper.toCreateResponse(tax));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{code}/{idEnterprise}")
    @Operation(summary = "Obtener información de un impuesto", description = "Recupera los detalles de un impuesto específico basado en el código y el ID de la empresa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Impuesto encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaxSearchRes.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    ResponseEntity<TaxSearchRes> getTax(@PathVariable("code") String code, @PathVariable String idEnterprise) {
        try {
            Tax tax = taxSearchInputPort.getTax(code, idEnterprise);
            return ResponseEntity.ok(taxSearchRestMapper.toSearchResponse(tax));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/taxes/{idEnterprise}")
    @Operation(summary = "Obtener lista de impuestos", description = "Recupera una lista de impuestos asociados a una empresa específica basada en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de impuestos recuperada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaxSearchRes.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
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
    @Operation(summary = "Actualizar información de un impuesto", description = "Actualiza los detalles de un impuesto específico basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Impuesto actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaxCreateRes.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    ResponseEntity<?> updateTax(@PathVariable("id") long id, @RequestBody TaxUpdateReq taxUpdateReq) {
        try {
            TaxDTO taxDTO = taxUpdateRestMapper.toDomain(taxUpdateReq);
            Tax tax = taxUpdateInputPort.update(taxDTO, id);
            return ResponseEntity.ok(taxUpdateRestMapper.toCreateResponse(tax));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un impuesto", description = "Elimina un impuesto específico basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Impuesto eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Impuesto no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    ResponseEntity<?> deleteByCode(@PathVariable("id") long id) {
        try {
            if (taxDeleteInputPort.deleteByCode(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id No Encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the tax.");
        }
    }

}
