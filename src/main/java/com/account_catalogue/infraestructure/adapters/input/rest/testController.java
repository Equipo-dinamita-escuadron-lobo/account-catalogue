package com.account_catalogue.infraestructure.adapters.input.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/accountCatalogue/test")
@RestController
@CrossOrigin(origins = "*")
public class testController {

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
    
}
