package com.account_catalogue.catalogue.infraestructure.adapters.input.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueChangeStateInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueExportInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueImportInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueImportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueChangeStateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueImportResponse;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AuxiliaryAccountListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountChangeStateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAuxiliaryAccountRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.util.AccountCatalogueExcelFileNameGenerator;

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
    private final IAccountCatalogueChangeStateInputPort accountCatalogueChangeStateInputPort;
    private final IAccountChangeStateRestMapper accountChangeStateRestMapper;
    private final IAuxiliaryAccountRestMapper auxiliaryAccountRestMapper;
    private final IAccountCatalogueExportInputPort accountCatalogueExportInputPort;
    private final IAccountCatalogueImportInputPort accountCatalogueImportInputPort;
    private final AccountCatalogueExcelFileNameGenerator fileNameGenerator;


    @PostMapping("/")
    public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(
            @Valid @RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq) {
        AccountCatalogue padre = null;
        if (accountCatalogueCreateReq.getParent() != null) {
            padre = accountCatalogueSearchInputPort.getAccountCatalogueById(accountCatalogueCreateReq.getParent(), accountCatalogueCreateReq.getIdEnterprise());
        }
        AccountCatalogue account = accountCreateRestMapper.toDomain(accountCatalogueCreateReq, padre);
        account = accountCatalogueCreateInputPort.createAccountCatalogue(account);
        return ResponseEntity.ok(accountCreateRestMapper.toCreateResponse(account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountCatalogueUpdateRes> updateAccountCatalogue(@PathVariable("id") int id,
            @Valid @RequestBody AccountCatalogueUpdateReq accountCatalogueUpdateReq) {
        // Convertir el request a dominio (incluye idEnterprise)
        AccountCatalogue updateAccountCatalogue = accountUpdateRestMapper.toDomain(accountCatalogueUpdateReq);
        
        // Actualizar la cuenta
        updateAccountCatalogue = accountCatalogueUpdateInputPort.updateAccountCatalogue(id, updateAccountCatalogue);
        return ResponseEntity.ok(accountUpdateRestMapper.toUpdateResponse(updateAccountCatalogue));
    }

    @GetMapping("/accountByCode/{code}/{idEnterprise}")
    public ResponseEntity<ItemAccountCatalogueSearchRes> getAccountCatalogue(@PathVariable String code,
            @PathVariable String idEnterprise) {
        AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort.getAccountCatalogueByCode(code, idEnterprise);
        return ResponseEntity.ok(itemAccountSearchRestMapper.toItemAccountCatalogueSearch(accountCatalogue));
    }

    @DeleteMapping("/{id}/{idEnterprise}")
    public ResponseEntity<Void> deleteByCode(@PathVariable Long id, @PathVariable String idEnterprise) {
        accountCatalogueDeleteInputPort.deleteById(id, idEnterprise);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/tree/{code}/{idEnterprise}")
    public ResponseEntity<AccountCatalogueListRes> getAccountCatalogueTree(@PathVariable String code,
            @PathVariable String idEnterprise) {
        AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort.getAccountCatalogueTree(code, idEnterprise);
        return ResponseEntity.ok(accountSearchRestMapper.toAccountCatalogueListRes(accountCatalogue));
    }

    @GetMapping("/trees/{idEnterprise}")
    public ResponseEntity<List<AccountCatalogueListRes>> getAccountCatalogueTrees(
            @PathVariable String idEnterprise) {
        List<AccountCatalogue> accountCatalogueTrees = accountCatalogueSearchInputPort.getAccountCatalogueTrees(idEnterprise);
        List<AccountCatalogueListRes> response = accountCatalogueTrees.stream()
                .map(accountSearchRestMapper::toAccountCatalogueListRes)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/changeState/{id}/{enterpriseId}")
    public ResponseEntity<AccountCatalogueChangeStateRes> changeState(
            @PathVariable Long id,
            @PathVariable String enterpriseId,
            @RequestParam Boolean status) {
        
        AccountCatalogue updatedAccount = accountCatalogueChangeStateInputPort.changeState(
                id, enterpriseId, status);
        
        AccountCatalogueChangeStateRes response = accountChangeStateRestMapper.toChangeStateResponse(updatedAccount);
        return ResponseEntity.ok(response);
    }

   
    @GetMapping("/auxiliary/{idEnterprise}")
    public ResponseEntity<AuxiliaryAccountListRes> getAuxiliaryAccounts(
            @PathVariable String idEnterprise) {
        
        List<AccountCatalogue> auxiliaryAccounts = accountCatalogueSearchInputPort.getAuxiliaryAccounts(idEnterprise);
        AuxiliaryAccountListRes response = auxiliaryAccountRestMapper.toAuxiliaryAccountListRes(auxiliaryAccounts, idEnterprise);
        
        return ResponseEntity.ok(response);
    }

   
    @GetMapping("/auxiliary/crossing/{idEnterprise}")
    public ResponseEntity<AuxiliaryAccountListRes> getAuxiliaryAccountsWithCrossing(
            @PathVariable String idEnterprise) {
        
        List<AccountCatalogue> auxiliaryAccountsWithCrossing = accountCatalogueSearchInputPort.getAuxiliaryAccountsWithCrossing(idEnterprise);
        AuxiliaryAccountListRes response = auxiliaryAccountRestMapper.toAuxiliaryAccountListRes(auxiliaryAccountsWithCrossing, idEnterprise);
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/template/excel")
    public ResponseEntity<Resource> exportAccountCatalogueTemplate(
            @RequestParam String entId) {

        Resource templateFile = accountCatalogueExportInputPort.exportAccountCatalogueTemplate(entId);
        String filename = fileNameGenerator.generateTemplateFileName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(templateFile);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportAccountCatalogueWithValidations(
            @RequestParam String entId,
            @RequestParam(required = false) String companyName) {

        Resource excelFile = accountCatalogueExportInputPort.exportAccountCatalogueWithValidations(entId);
        String filename = fileNameGenerator.generateExportFileName(entId, companyName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }

    /**
     * Endpoint para importar catálogo de cuentas desde archivo Excel.
     * 
     * @param entId identificador de la empresa
     * @param file archivo Excel con las cuentas a importar
     * @return respuesta con estadísticas y errores de la importación
     */
    @PostMapping("/import/excel")
    public ResponseEntity<AccountCatalogueImportResponse> importFromExcel(
            @RequestParam String entId,
            @RequestParam("file") MultipartFile file) {

        // Construir request
        AccountCatalogueImportRequest request = AccountCatalogueImportRequest.from(entId, file);

        // Ejecutar importación
        AccountCatalogueImportResponse response = accountCatalogueImportInputPort
                .importAccountCatalogueFromExcel(request);

        // Mapear estado a código HTTP apropiado
        HttpStatus httpStatus = mapImportStatusToHttpStatus(response.getStatus());

        return ResponseEntity.status(httpStatus).body(response);
    }

    /**
     * Mapea el estado de importación a código HTTP apropiado.
     */
    private HttpStatus mapImportStatusToHttpStatus(ImportStatus status) {
        return switch (status) {
            case COMPLETED -> HttpStatus.OK;
            case COMPLETED_WITH_ERRORS -> HttpStatus.ACCEPTED;
            case FAILED -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
