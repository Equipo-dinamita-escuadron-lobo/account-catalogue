package com.account_catalogue.infraestructure.adapters.input.rest;

import com.account_catalogue.application.input.ITaxCreateInputPort;
import com.account_catalogue.application.input.ITaxDeleteInputPort;
import com.account_catalogue.application.input.ITaxSearchInputPort;
import com.account_catalogue.application.input.ITaxUpdateInputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxUpdateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxUpdateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxCreateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxUpdateRestMapper;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RequestMapping("/api/tax")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TaxController {

    private final ITaxCreateRestMapper taxCreateRestMapper;
    private final ITaxSearchRestMapper taxSearchRestMapper;
    private final ITaxCreateInputPort taxCreateInputPort;
    private final ITaxUpdateInputPort taxUpdateInputPort;
    private final ITaxSearchInputPort taxSearchInputPort;
    private final ITaxUpdateRestMapper taxUpdateRestMapper;
    private final  ITaxDeleteInputPort taxDeleteInputPort;



    @PostMapping("/")
    ResponseEntity<TaxCreateRes> createTax(@RequestBody TaxCreateReq taxCreateReq){
        try{
            TaxDTO taxDTO=taxCreateRestMapper.toDomain(taxCreateReq);
             Tax  tax=taxCreateInputPort.createTax(taxDTO);
            return ResponseEntity.ok(taxCreateRestMapper.toCreateResponse(tax));


        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{code}")
    ResponseEntity<TaxSearchRes> getTax(@PathVariable("code") String code){
       Tax tax=taxSearchInputPort.getTax(code);
       return ResponseEntity.ok(taxSearchRestMapper.toSearchResponse(tax));

    }
    @GetMapping("/taxes")
    ResponseEntity <List<TaxSearchRes>>  getTaxes(){
        List<Tax> taxes=taxSearchInputPort.getTaxes();
        return ResponseEntity.ok(taxSearchRestMapper.toSearchListResponse(taxes));
    }
    @PutMapping("/{code}")
    ResponseEntity<TaxUpdateRes> updateTax(@PathVariable("code")String code,@RequestBody TaxUpdateReq taxUpdateReq){
        try{
            TaxDTO taxDTO=taxUpdateRestMapper.toDomain(taxUpdateReq);
            Tax  tax=taxUpdateInputPort.update(taxDTO,code);
            return ResponseEntity.ok(taxUpdateRestMapper.toCreateResponse(tax));

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{code}")
    ResponseEntity<?> deleteByCode(@PathVariable("code")String code){
        if(taxDeleteInputPort.deleteByCode(code)){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Message");
        }
    }

}
