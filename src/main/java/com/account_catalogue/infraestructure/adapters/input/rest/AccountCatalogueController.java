package com.account_catalogue.infraestructure.adapters.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.account_catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/api/accountCatalogue")
@RestController
@AllArgsConstructor
public class AccountCatalogueController {
    private final IAccountCatalogueCreateInputPort accountCatalogueCreateInputPort;
   private  final IAccountCreateRestMapper accountCreateRestMapper;

    @PostMapping("/")
   public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(@Valid @RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq){
     
        AccountCatalogue account=accountCreateRestMapper.toDomain(accountCatalogueCreateReq);
        account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
        return   ResponseEntity.ok(accountCreateRestMapper.toCreateResponse(account));
   }
}
