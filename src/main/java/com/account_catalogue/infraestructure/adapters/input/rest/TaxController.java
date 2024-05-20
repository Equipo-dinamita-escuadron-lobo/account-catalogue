package com.account_catalogue.infraestructure.adapters.input.rest;

import com.account_catalogue.application.input.ITaxCreateInputPort;
import com.account_catalogue.application.input.ITaxSearchInputPort;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.input.rest.data.request.TaxCreateReq;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxCreateRes;
import com.account_catalogue.infraestructure.adapters.input.rest.data.response.TaxSearchRes;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxCreateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.ITaxSearchRestMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RequestMapping("/api/tax")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TaxController {

    private final ITaxCreateRestMapper taxCreateRestMapper;
    private final ITaxSearchRestMapper taxSearchRestMapper;
    private final ITaxCreateInputPort taxCreateInputPort;
    private final ITaxSearchInputPort taxSearchInputPort;


    @PostMapping("/")
    ResponseEntity<TaxCreateRes> createTax(@RequestBody TaxCreateReq taxCreateReq){
        try{
            Tax tax=taxCreateRestMapper.toDomain(taxCreateReq);
            tax=taxCreateInputPort.createTax(tax);
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
}
