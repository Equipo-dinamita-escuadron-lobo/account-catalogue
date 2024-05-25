package com.account_catalogue.infraestructure.adapters.input.rest;

import com.account_catalogue.application.input.IAccountTaxCreateInputPort;
import com.account_catalogue.domain.models.AccountTax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountTaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountTaxCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccounTaxCreateRestMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/accountTax")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AccountTaxController {

  private final IAccounTaxCreateRestMapper accounTaxCreateRestMapper;
  private final IAccountTaxCreateInputPort accountTaxCreateInputPort;

  @PostMapping("/")
  ResponseEntity<AccountTaxCreateRes> createAccountTax(@RequestBody AccountTaxCreateReq accountTaxCreateReq){
  try{
   AccountTax accountTax=accountTaxCreateInputPort.createAccounTax(accountTaxCreateReq.getCodeAccount(), accountTaxCreateReq.getCodeTax());
    return  ResponseEntity.ok(accounTaxCreateRestMapper.toCreateResponse(accountTax));
  }catch (Exception e){
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
  }
}
