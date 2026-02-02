package com.account_catalogue.accounting.infraestructure.input.data.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    private final boolean success;
    private final String message;
    private final String code; // Código interno para que el frontend identifique el caso
    private final T data;
    private final Integer status; // Código de estado HTTP (ej. 404, 500)
    private final String path;

    // Constructor para respuestas de ÉXITO
    private ApiResponse(boolean success, String message, String code, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
        this.status = null;
        this.path = null;
    }

    // Constructor para respuestas de ERROR
    private ApiResponse(boolean success, String message, String code, Integer status, String path) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = null;
        this.status = status;
        this.path = path;
    }

    // --- MÉTODOS ESTÁTICOS PARA ÉXITO ---

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operación exitosa.", "OK", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, "OK", data);
    }

    public static <T> ApiResponse<T> successEmpty(String message, String code) {
        return new ApiResponse<>(true, message, code, null);
    }

    // --- MÉTODOS ESTÁTICOS PARA ERROR ---

    public static <T> ApiResponse<T> error(String message, String code, HttpStatus status, String path) {
        return new ApiResponse<>(false, message, code, status.value(), path);
    }
}
