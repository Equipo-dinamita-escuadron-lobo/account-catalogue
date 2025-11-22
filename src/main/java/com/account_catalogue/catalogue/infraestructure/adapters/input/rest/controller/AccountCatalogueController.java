package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.account_catalogue.catalogue.domain.models.ExportJobStatus;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request.AccountCatalogueExportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request.AccountCatalogueCreateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request.AccountCatalogueImportRequest;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.request.AccountCatalogueUpdateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AccountCatalogueChangeStateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AccountCatalogueCreateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AccountCatalogueListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AccountCatalogueUpdateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.AuxiliaryAccountListRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.dto.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountChangeStateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IAuxiliaryAccountRestMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import com.account_catalogue.catalogue.infraestructure.utils.AccountCatalogueExcelFileNameGenerator;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * @brief Controlador REST para gestión completa del catálogo de cuentas
 *
 * Expone endpoints para operaciones CRUD, importación/exportación Excel,
 * búsqueda jerárquica y gestión de estados de cuentas contables.
 */
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


    /**
     * @brief Crea nueva cuenta contable en el catálogo
     * @param accountCatalogueCreateReq datos de la cuenta a crear
     * @return respuesta con datos de la cuenta creada
     */
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

    /**
     * @brief Actualiza cuenta contable existente
     * @param id identificador único de la cuenta
     * @param accountCatalogueUpdateReq datos actualizados de la cuenta
     * @return respuesta con datos de la cuenta actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountCatalogueUpdateRes> updateAccountCatalogue(@PathVariable("id") int id,
            @Valid @RequestBody AccountCatalogueUpdateReq accountCatalogueUpdateReq) {
        // Convertir el request a dominio (incluye idEnterprise)
        AccountCatalogue updateAccountCatalogue = accountUpdateRestMapper.toDomain(accountCatalogueUpdateReq);
        
        // Actualizar la cuenta
        updateAccountCatalogue = accountCatalogueUpdateInputPort.updateAccountCatalogue(id, updateAccountCatalogue);
        return ResponseEntity.ok(accountUpdateRestMapper.toUpdateResponse(updateAccountCatalogue));
    }

    /**
     * @brief Obtiene cuenta contable por código y empresa
     * @param code código de la cuenta a buscar
     * @param idEnterprise ID de la empresa
     * @return respuesta con datos de la cuenta encontrada
     */
    @GetMapping("/accountByCode/{code}/{idEnterprise}")
    public ResponseEntity<ItemAccountCatalogueSearchRes> getAccountCatalogue(@PathVariable String code,
            @PathVariable String idEnterprise) {
        AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort.getAccountCatalogueByCode(code, idEnterprise);
        return ResponseEntity.ok(itemAccountSearchRestMapper.toItemAccountCatalogueSearch(accountCatalogue));
    }

    /**
     * @brief Elimina cuenta contable por ID y empresa
     * @param id identificador único de la cuenta
     * @param idEnterprise ID de la empresa
     * @return respuesta sin contenido (204)
     */
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

    @GetMapping("/search/{idEnterprise}")
    public ResponseEntity<List<ItemAccountCatalogueSearchRes>> searchAccountCatalogues(
            @PathVariable String idEnterprise,
            @RequestParam(required = false) String search) {

        List<AccountCatalogue> accounts = (search != null && !search.trim().isEmpty())
                ? accountCatalogueSearchInputPort.getAccountsByCodeOrDescription(idEnterprise, search)
                : accountCatalogueSearchInputPort.getAllAccountsByEnterprise(idEnterprise);

        List<ItemAccountCatalogueSearchRes> response = accounts.stream()
                .map(itemAccountSearchRestMapper::toItemAccountCatalogueSearch)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
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

    
    /**
     * @brief Descarga plantilla Excel para importación de cuentas
     * @param entId identificador de la empresa
     * @return archivo Excel con estructura de plantilla
     */
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

    /**
     * @brief Inicia exportación asíncrona de catálogo de cuentas
     * @param entId identificador de la empresa
     * @param companyName nombre de la empresa (opcional, para incluir en el nombre del archivo)
     * @param status estado de las cuentas a incluir (opcional: true=activos, false=inactivos, null=todos)
     * @return respuesta con jobId para rastrear el estado de la exportación
     */
    @GetMapping("/export/excel")
    public ResponseEntity<Map<String, String>> exportAccountCatalogueAsync(
            @RequestParam String entId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Boolean status) {


        AccountCatalogueExportRequest request = AccountCatalogueExportRequest.builder()
                .entId(entId)
                .companyName(companyName)
                .status(status)
                .build();

        String jobId = accountCatalogueExportInputPort.exportAccountCatalogueAsync(request);


        return ResponseEntity.accepted()
                .body(Map.of(
                        "jobId", jobId,
                        "message", "Exportación iniciada exitosamente",
                        "statusEndpoint", "/api/accountCatalogue/export/status/" + jobId
                ));
    }

    /**
     * @brief Consulta el estado de una exportación asíncrona
     * @param jobId identificador del trabajo de exportación
     * @return estado actual de la exportación con archivo si está completado
     */
    @GetMapping("/export/status/{jobId}")
    public ResponseEntity<?> getExportStatus(@PathVariable String jobId) {

        return accountCatalogueExportInputPort.getExportStatus(jobId)
                .map(status -> {
                    return ResponseEntity.ok(status);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @brief Descarga el archivo exportado de catálogo de cuentas
     * @param jobId identificador del trabajo de exportación
     * @return archivo Excel exportado
     */
    @GetMapping("/export/download/{jobId}")
    public ResponseEntity<Resource> downloadExportedFile(@PathVariable String jobId) {

        Optional<ExportJobStatus> jobStatus = accountCatalogueExportInputPort.getExportStatus(jobId);

        if (jobStatus.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ExportJobStatus status = jobStatus.get();

        if (status.getStatus() != ImportStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (status.getFileData() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Resource resource = new ByteArrayResource(status.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + status.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    
    /**
     * @brief Inicia importación asíncrona de cuentas contables desde archivo Excel
     * @param entId identificador de la empresa
     * @param file archivo Excel con datos de cuentas
     * @return respuesta con jobId para rastrear el estado de la importación
     */
    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, String>> importFromExcel(
            @RequestParam String entId,
            @RequestParam MultipartFile file) {


        // Construir request
        AccountCatalogueImportRequest request = AccountCatalogueImportRequest.from(entId, file);

        // Iniciar importación asíncrona
        String jobId = accountCatalogueImportInputPort.importAccountCatalogueAsync(request);


        // Retornar jobId para que el cliente pueda consultar el estado
        return ResponseEntity.accepted()
                .body(Map.of(
                        "jobId", jobId,
                        "message", "Importación iniciada exitosamente",
                        "statusEndpoint", "/api/accountCatalogue/import/status/" + jobId
                ));
    }

    /**
     * @brief Consulta el estado de una importación asíncrona
     * @param jobId identificador del trabajo de importación
     * @return estado actual de la importación
     */
    @GetMapping("/import/status/{jobId}")
    public ResponseEntity<?> getImportStatus(@PathVariable String jobId) {

        return accountCatalogueImportInputPort.getImportStatus(jobId)
                .map(status -> {
                    return ResponseEntity.ok(status);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
