package com.account_catalogue.catalogue.infraestructure.adapters.input.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.account_catalogue.catalogue.application.input.IAccountCatalogueChangeStateInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueExportInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueChangeStateRes;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
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
    private final AccountCatalogueExcelFileNameGenerator fileNameGenerator;


    @PostMapping("/")
    public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(
            @RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq) {
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
        List<AccountCatalogueListRes> accountCatalogueListRes = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            try {
                AccountCatalogue accountCatalogue = accountCatalogueSearchInputPort
                        .getAccountCatalogueTree(String.valueOf(i), idEnterprise);
                if (accountCatalogue != null) {
                    accountCatalogueListRes.add(accountSearchRestMapper.toAccountCatalogueListRes(accountCatalogue));
                }
            } catch (Exception e) {
                // Si la cuenta con código 'i' no existe, simplemente continúa con el siguiente
                // No lanza excepción, solo omite la cuenta inexistente
            }
        }
        return ResponseEntity.ok(accountCatalogueListRes);
    }

    /**
     * Cambia el estado (activo/inactivo) de una cuenta del catálogo.
     * 
     * @param id el ID de la cuenta
     * @param enterpriseId el ID de la empresa
     * @param status el nuevo estado (true = activo, false = inactivo)
     * @return respuesta con el estado actualizado
     */
    @PatchMapping("/changeState/{id}/{enterpriseId}")
    public ResponseEntity<AccountCatalogueChangeStateRes> changeState(
            @PathVariable Long id,
            @PathVariable String enterpriseId,
            @RequestParam Boolean status) {
        
        // Validación manual del parámetro status
        if (status == null) {
            throw new IllegalArgumentException("El parámetro 'status' es requerido");
        }
        
        AccountCatalogue updatedAccount = accountCatalogueChangeStateInputPort.changeState(
                id, enterpriseId, status);
        
        AccountCatalogueChangeStateRes response = accountChangeStateRestMapper.toChangeStateResponse(updatedAccount);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las cuentas auxiliares (8 dígitos) activas para una empresa específica.
     * 
     * @param idEnterprise el ID de la empresa
     * @return lista de cuentas auxiliares ordenadas por código
     */
    @GetMapping("/auxiliary/{idEnterprise}")
    public ResponseEntity<AuxiliaryAccountListRes> getAuxiliaryAccounts(
            @PathVariable String idEnterprise) {
        
        List<AccountCatalogue> auxiliaryAccounts = accountCatalogueSearchInputPort.getAuxiliaryAccounts(idEnterprise);
        AuxiliaryAccountListRes response = auxiliaryAccountRestMapper.toAuxiliaryAccountListRes(auxiliaryAccounts, idEnterprise);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las cuentas auxiliares (8 dígitos) activas que tienen el campo crossing activo para una empresa específica.
     * 
     * @param idEnterprise el ID de la empresa
     * @return lista de cuentas auxiliares con crossing activo ordenadas por código
     */
    @GetMapping("/auxiliary/crossing/{idEnterprise}")
    public ResponseEntity<AuxiliaryAccountListRes> getAuxiliaryAccountsWithCrossing(
            @PathVariable String idEnterprise) {
        
        List<AccountCatalogue> auxiliaryAccountsWithCrossing = accountCatalogueSearchInputPort.getAuxiliaryAccountsWithCrossing(idEnterprise);
        AuxiliaryAccountListRes response = auxiliaryAccountRestMapper.toAuxiliaryAccountListRes(auxiliaryAccountsWithCrossing, idEnterprise);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Exporta una plantilla de catálogo de cuentas con validaciones.
     *
     * @param entId ID de la entidad
     * @return Archivo Excel con la plantilla
     */
    @GetMapping(value = "/template/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportAccountCatalogueTemplate(
            @RequestParam String entId) {

        org.springframework.core.io.Resource templateFile = accountCatalogueExportInputPort.exportAccountCatalogueTemplate(entId);
        String filename = fileNameGenerator.generateTemplateFileName();

        try {
            byte[] fileContent = templateFile.getInputStream().readAllBytes();
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(fileContent);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo de plantilla", e);
        }
    }

    /**
     * Exporta el catálogo de cuentas con validaciones a formato Excel.
     *
     * @param entId ID de la entidad
     * @param companyName Nombre de la empresa (opcional)
     * @return Archivo Excel con los datos del catálogo
     */
    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportAccountCatalogueWithValidations(
            @RequestParam String entId,
            @RequestParam(required = false) String companyName) {

        org.springframework.core.io.Resource excelFile = accountCatalogueExportInputPort.exportAccountCatalogueWithValidations(entId);
        String filename = fileNameGenerator.generateExportFileName(entId, companyName);

        try {
            byte[] fileContent = excelFile.getInputStream().readAllBytes();
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(fileContent);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo de exportación", e);
        }
    }

}
