package com.account_catalogue.catalogue.infraestructure.adapters.input.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import com.account_catalogue.commons.exceptions.AccountCatalogueNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/api/accountCatalogue")
@RestController
@AllArgsConstructor
// @PreAuthorize("hasRole('admin_client')")
public class AccountCatalogueController {
    private final IAccountCatalogueCreateInputPort accountCatalogueCreateInputPort;
    private final IAccountCreateRestMapper accountCreateRestMapper;
    private final IAccountSearchRestMapper accountSearchRestMapper;
    private final IAccountCatalogueSearchInputPort accountCatalogueSearchInputPort;
    private final IItemAccountSearchRestMapper itemAccountSearchRestMapper;
    private final IAccountUpdateRestMapper accountUpdateRestMapper;
    private final IAccountCatalogueDeleteInputPort accountCatalogueDeleteInputPort;
    private final IAccountCatalogueUpdateInputPort accountCatalogueUpdateInputPort;


    @PostMapping("/")
    @Operation(summary = "Crear un catálogo de cuentas", description = "Crea un nuevo catálogo de cuentas con un padre específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de cuentas creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountCatalogueCreateRes.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(
            @RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq) {
        try {
            AccountCatalogue padre = accountCatalogueSearchInputPort
                    .getAccountCatalogueById(accountCatalogueCreateReq.getParent());
            AccountCatalogue account = accountCreateRestMapper.toDomain(accountCatalogueCreateReq, padre);
            account = accountCatalogueCreateInputPort.createAccountCatalogue(account);
            return ResponseEntity.ok(accountCreateRestMapper.toCreateResponse(account));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un catálogo de cuentas", description = "Actualiza los detalles de un catálogo de cuentas existente basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de cuentas actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountCatalogueUpdateRes.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<AccountCatalogueUpdateRes> updateAccountCatalogue(@PathVariable("id") int id,
            @Valid @RequestBody AccountCatalogueUpdateReq accountCatalogueUpdateReq) {
        AccountCatalogue updateAccountCatalogue = accountUpdateRestMapper.toDomain(accountCatalogueUpdateReq);
        updateAccountCatalogue = accountCatalogueUpdateInputPort.updateAccountCatalogue(id, updateAccountCatalogue);
        return ResponseEntity.ok(accountUpdateRestMapper.toUpdateResponse(updateAccountCatalogue));
    }

    @GetMapping("/accountByCode/{code}/{idEnterprise}")
    @Operation(summary = "Obtener un catálogo de cuentas por código", description = "Recupera un catálogo de cuentas basado en el código y el ID de la empresa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de cuentas encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemAccountCatalogueSearchRes.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ItemAccountCatalogueSearchRes> getAccountCatalogue(@PathVariable("code") String code,
            @PathVariable("idEnterprise") String idEnterprise) {
        AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort.getAccountCatalogueByCode(code,
                idEnterprise);
        return ResponseEntity.ok(itemAccountSearchRestMapper.toItemAccountCatalogueSearch(accountCatalogue));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un catálogo de cuentas", description = "Elimina un catálogo de cuentas basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Catálogo de cuentas eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Catálogo de cuentas no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> deleteByCode(@PathVariable("id") Long id) {
        try {
            accountCatalogueDeleteInputPort.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (AccountCatalogueNotFoundException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error Message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/tree/{code}/{idEnterprise}")
    @Operation(summary = "Obtener árbol de catálogo de cuentas", description = "Recupera el árbol completo de un catálogo de cuentas basado en el código y el ID de la empresa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Árbol de catálogo de cuentas recuperado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountCatalogueListRes.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<AccountCatalogueListRes> getAccountCatalogueTree(@PathVariable("code") String code,
            @PathVariable("idEnterprise") String idEnterprise) {
        AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort.getAccountCatalogueTree(code, idEnterprise);
        return ResponseEntity.ok(accountSearchRestMapper.toAccountCatalogueListRes(accountCatalogue));
    }

    @GetMapping("/trees/{idEnterprise}")
    @Operation(summary = "Obtener árboles de catálogos de cuentas", description = "Recupera todos los árboles de catálogos de cuentas para una empresa específica basada en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Árboles de catálogos de cuentas recuperados exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountCatalogueListRes.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<AccountCatalogueListRes>> getAccountCatalogueTrees(
            @PathVariable("idEnterprise") String idEnterprise) {
        List<AccountCatalogueListRes> accountCatalogueListRes = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort
                    .getAccountCatalogueTree(String.valueOf(i), idEnterprise);
            if (accountCatalogue != null) {
                accountCatalogueListRes.add(accountSearchRestMapper.toAccountCatalogueListRes(accountCatalogue));
            }
        }
        return ResponseEntity.ok(accountCatalogueListRes);
    }

}
