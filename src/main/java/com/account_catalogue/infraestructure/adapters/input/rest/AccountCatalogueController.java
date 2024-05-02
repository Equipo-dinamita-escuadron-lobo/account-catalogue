package com.account_catalogue.infraestructure.adapters.input.rest;

import com.account_catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.application.input.IAccountCatalogueUpdateInputPort;
import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;

import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueUpdateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.ItemAccountCatalogueSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.exception.AccountCatalogueNotFoundException;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.util.AdjustEnumAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.account_catalogue.application.input.IAccountCatalogueCreateInputPort;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.AccountCatalogueCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.AccountCatalogueCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/accountCatalogue")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AccountCatalogueController {
    private final IAccountCatalogueCreateInputPort accountCatalogueCreateInputPort;
   private  final IAccountCreateRestMapper accountCreateRestMapper;
   private final IAccountSearchRestMapper accountSearchRestMapper;
   private  final IAccountCatalogueSearchInputPort accountCatalogueSearchInputPort;
    private final IItemAccountSearchRestMapper itemAccountSearchRestMapper;
    private final IAccountUpdateRestMapper accountUpdateRestMapper;
    private final IAccountCatalogueDeleteInputPort accountCatalogueDeleteInputPort;
    private final IAccountCatalogueUpdateInputPort accountCatalogueUpdateInputPort;



   /* @PostMapping("/")
   public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(@Valid @RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq){
     
        AccountCatalogue account=accountCreateRestMapper.toDomain(accountCatalogueCreateReq);
        account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
        return   ResponseEntity.ok(accountCreateRestMapper.toCreateResponse(account));
   }*/
    @PostMapping("/")
    public ResponseEntity<AccountCatalogueCreateRes> createAccountCatalogue(@RequestBody AccountCatalogueCreateReq accountCatalogueCreateReq){
       AccountCatalogue account=null;
        try{


            account=accountCreateRestMapper.toDomainClase( accountCatalogueCreateReq);

            account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
            //Grupo
            if(accountCatalogueCreateReq.getGrupo().getCode()!=null){

                account=accountCreateRestMapper.toDomainGrupo(accountCatalogueCreateReq.getGrupo());
                account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
               //Cuenta
                if(accountCatalogueCreateReq.getGrupo().getCuenta().getCode()!=null){

                    account =accountCreateRestMapper.toDomainCuenta(accountCatalogueCreateReq.getGrupo().getCuenta());
                    account =accountCatalogueCreateInputPort.createAccountCatalogue(account );
                    //SubCuenta
                    if(accountCatalogueCreateReq.getGrupo().getCuenta().getSubcuenta().getCode()!=null){

                        account=accountCreateRestMapper.toDomainSubCuenta(accountCatalogueCreateReq.getGrupo().getCuenta().getSubcuenta());
                        account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
                            //Auxiliar1
                        if(accountCatalogueCreateReq.getGrupo().getCuenta().getSubcuenta().getAuxiliar().getCode()!=null){

                            account=accountCreateRestMapper.toDomainAuxiliar1(accountCatalogueCreateReq.getGrupo().getCuenta().getSubcuenta().getAuxiliar());
                            account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
                           /*
                            //Auxiliar2
                                if(accountCatalogueCreateReq.getGrupo().getCuenta().getSubcuenta().getAuxiliar().getAuxiliar2().getCode()!=null){
                                    account=accountCreateRestMapper.toDomainAUxiliar2(accountCatalogueCreateReq.getGrupo().getCuenta().getSubcuenta().getAuxiliar().getAuxiliar2());
                                    account=accountCatalogueCreateInputPort.createAccountCatalogue(account);
                                }*/
                        }

                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }


        return   ResponseEntity.ok(accountCreateRestMapper.toCreateResponse( account));
    }


   @PutMapping("/{id}")
   public ResponseEntity<AccountCatalogueUpdateRes> updateAccountCatalogue(@PathVariable("id") int id, @Valid @RequestBody AccountCatalogueUpdateReq accountCatalogueUpdateReq){
        AccountCatalogue updateAccountCatalogue=accountUpdateRestMapper.toDomain(accountCatalogueUpdateReq);
        updateAccountCatalogue=accountCatalogueUpdateInputPort.updateAccountCatalogue(id,updateAccountCatalogue);
        return ResponseEntity.ok(accountUpdateRestMapper.toUpdateResponse(updateAccountCatalogue));
   }
   @GetMapping("/accounts/{code}")
    public ResponseEntity<List<AccountCatalogueSearchRes>> getAllAccountCatalogue(@PathVariable("code")String code){
        List<AccountCatalogueInfoDTO> accounts=accountCatalogueSearchInputPort.getAllAccountCatalogue(code);
        return ResponseEntity.ok(accountSearchRestMapper.toListAccountResponse(accounts));
   }

    @GetMapping("/accountByCode/{code}")
    public ResponseEntity<ItemAccountCatalogueSearchRes> getAccountCatalogue(@PathVariable("code")String code){
        AccountCatalogue accountCatalogue=accountCatalogueSearchInputPort.getAccountCatalogueByCode(code);
        return ResponseEntity.ok(itemAccountSearchRestMapper.toItemAccountCatalogueSearch(accountCatalogue));
    }
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteByCode(@PathVariable("code")String code){
        try{
            accountCatalogueDeleteInputPort.deleteByCode(code);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch(AccountCatalogueNotFoundException ex){
            Map<String,String> errorRespnse=new HashMap<>();
            errorRespnse.put("Error Message",ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRespnse);
        }
    }
}
