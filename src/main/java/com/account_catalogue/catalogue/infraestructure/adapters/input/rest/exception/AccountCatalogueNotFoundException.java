package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.exception;

public class AccountCatalogueNotFoundException extends RuntimeException{
    public AccountCatalogueNotFoundException(String message){
        super(message);
    }

}
