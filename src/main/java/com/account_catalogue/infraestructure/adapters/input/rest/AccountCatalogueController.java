package com.account_catalogue.infraestructure.adapters.input.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.account_catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueListRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.exception.AccountCatalogueNotFoundException;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/api/accountCatalogue")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
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
    public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(@RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq){
        try{
            AccountCatalogue padre = accountCatalogueSearchInputPort.getAccountCatalogueByCode(accountCatalogueCreateReq.getParent(),accountCatalogueCreateReq.getIdEnterprise());
            AccountCatalogue account=accountCreateRestMapper.toDomain(accountCatalogueCreateReq,padre);
            account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
            return   ResponseEntity.ok(accountCreateRestMapper.toCreateResponse(account));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }   
    }


   @PutMapping("/{id}")
   public ResponseEntity<AccountCatalogueUpdateRes> updateAccountCatalogue(@PathVariable("id") int id, @Valid @RequestBody AccountCatalogueUpdateReq accountCatalogueUpdateReq){
        AccountCatalogue updateAccountCatalogue=accountUpdateRestMapper.toDomain(accountCatalogueUpdateReq);
        updateAccountCatalogue=accountCatalogueUpdateInputPort.updateAccountCatalogue(id,updateAccountCatalogue);
        return ResponseEntity.ok(accountUpdateRestMapper.toUpdateResponse(updateAccountCatalogue));
   }

    @GetMapping("/accountByCode/{code}/{idEnterprise}")
    public ResponseEntity<ItemAccountCatalogueSearchRes> getAccountCatalogue(@PathVariable("code")String code, @PathVariable("idEnterprise")String idEnterprise){
        AccountCatalogue accountCatalogue=accountCatalogueSearchInputPort.getAccountCatalogueByCode(code,idEnterprise);
        return ResponseEntity.ok(itemAccountSearchRestMapper.toItemAccountCatalogueSearch(accountCatalogue));
    }
   
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteByCode(@PathVariable("id")Long id){
        try{
            accountCatalogueDeleteInputPort.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch(AccountCatalogueNotFoundException ex){
            Map<String,String> errorRespnse=new HashMap<>();
            errorRespnse.put("Error Message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRespnse);
        }
    }

    @GetMapping("/tree/{code}/{idEnterprise}")
    public ResponseEntity<AccountCatalogueListRes> getAccountCatalogueTree(@PathVariable("code")String code, @PathVariable("idEnterprise")String idEnterprise){
        AccountCatalogue accountCatalogue=accountCatalogueSearchInputPort.getAccountCatalogueTree(code,idEnterprise);
        return ResponseEntity.ok(accountSearchRestMapper.toAccountCatalogueListRes(accountCatalogue));
    }

    @GetMapping("/trees/{idEnterprise}")
    public ResponseEntity<List<AccountCatalogueListRes>> getAccountCatalogueTrees(@PathVariable("idEnterprise")String idEnterprise){
        List<AccountCatalogueListRes> accountCatalogueListRes= new ArrayList<>();
        for(int i=1;i<=9;i++){
            AccountCatalogue accountCatalogue=accountCatalogueSearchInputPort.getAccountCatalogueTree(String.valueOf(i),idEnterprise);
            if (accountCatalogue!=null){
                accountCatalogueListRes.add(accountSearchRestMapper.toAccountCatalogueListRes(accountCatalogue));
            }
        }
        return ResponseEntity.ok(accountCatalogueListRes);
        
    }


}
