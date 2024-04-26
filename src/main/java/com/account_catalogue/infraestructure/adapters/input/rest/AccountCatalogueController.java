package com.account_catalogue.infraestructure.adapters.input.rest;

import com.account_catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.account_catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

@RequestMapping("/api/accountCatalogue")
@RestController
@AllArgsConstructor
public class AccountCatalogueController {
    private final IAccountCatalogueCreateInputPort accountCatalogueCreateInputPort;
   private  final IAccountCreateRestMapper accountCreateRestMapper;
   private final IAccountSearchRestMapper accountSearchRestMapper;
   private  final IAccountCatalogueSearchInputPort accountCatalogueSearchInputPort;
    private final IItemAccountSearchRestMapper itemAccountSearchRestMapper;
    @PostMapping("/")
   public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(@Valid @RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq){
     
        AccountCatalogue account=accountCreateRestMapper.toDomain(accountCatalogueCreateReq);
        account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
        return   ResponseEntity.ok(accountCreateRestMapper.toCreateResponse(account));
   }
   @GetMapping("/{code}")
    public ResponseEntity<List<AccountCatalogueSearchRes>> getAllAccountCatalogue(@PathVariable("code")String code){
        List<AccountCatalogueInfoDTO> accounts=accountCatalogueSearchInputPort.getAllAccountCatalogue(code);
        return ResponseEntity.ok(accountSearchRestMapper.toListAccountResponse(accounts));
   }

    @GetMapping("/code/{code}")
    public ResponseEntity<ItemAccountCatalogueSearchRes> getAccountCatalogue(@PathVariable("code")String code){
        AccountCatalogue accountCatalogue=accountCatalogueSearchInputPort.getAccountCatalogueByCode(code);
        return ResponseEntity.ok(itemAccountSearchRestMapper.toItemAccountCatalogueSearch(accountCatalogue));
    }
}
