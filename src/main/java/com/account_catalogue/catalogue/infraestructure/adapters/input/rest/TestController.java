package com.account_catalogue.catalogue.infraestructure.adapters.input.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/accountCatalogue/test")
public class TestController {

    @GetMapping("/testAdmin")
    @PreAuthorize("hasRole('admin_client')")
    @Operation(summary = "Prueba para administrador", description = "Endpoint de prueba accesible únicamente para usuarios con el rol de administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceso permitido para administrador", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    public String test() {
        return "Test for Admin";
    }

    @GetMapping("/testUser")
    @PreAuthorize("hasRole('user_client') or hasRole('admin_client')")
    @Operation(summary = "Prueba para usuario o administrador", description = "Endpoint de prueba accesible para usuarios con rol de usuario o administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceso permitido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    public String testUser() {
        return "Test for User";
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Prueba básica para verificar la disponibilidad del servidor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servidor responde correctamente", content = @Content(mediaType = "text/plain"))
    })
    public String ping() {
        return "pong";
    }

}
