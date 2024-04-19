package com.chartaccounts.infraestructure.adapters.input.rest;

import com.chartaccounts.application.input.IAccountCatalogueCreateInputPort;
import com.chartaccounts.domain.models.AccountCatalogue;
import com.chartaccounts.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.chartaccounts.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.chartaccounts.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
