package com.account_catalogue.commons.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueErrorCode;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueHierarchyException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueImportException;
import com.account_catalogue.commons.exceptions.catalogue.FileSizeExceededException;
import com.account_catalogue.commons.exceptions.catalogue.FileValidationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Proporciona respuestas consistentes y descriptivas para diferentes tipos de errores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de negocio y resuelve el estado HTTP según el código de error.
     */
    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(
            BaseBusinessException ex, WebRequest request) {

        String errorCode = ex.getErrorCode().getCode();
        HttpStatus status = mapStatusFromErrorCode(errorCode);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .code(errorCode)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Maneja errores de validación de payload (Bean Validation en @RequestBody con @Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (msg1, msg2) -> msg1
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Error de validación de campos")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(Map.of(
                "error", errorResponse,
                "fieldErrors", fieldErrors
        ), status);
    }

    /**
     * Maneja errores de validación a nivel de parámetros (e.g., @RequestParam, @PathVariable).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (msg1, msg2) -> msg1
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Error de validación")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(Map.of(
                "error", errorResponse,
                "violations", violations
        ), status);
    }

    /**
     * Maneja excepciones de tamaño de archivo excedido por Spring Boot.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error("Payload Too Large")
                .message("El archivo excede el tamaño máximo permitido")
                .code(AccountCatalogueErrorCode.FILE_SIZE_EXCEEDED.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Maneja excepciones específicas de importación de catálogo de cuentas.
     */
    @ExceptionHandler(AccountCatalogueImportException.class)
    public ResponseEntity<ErrorResponse> handleAccountCatalogueImportException(
            AccountCatalogueImportException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Import Error")
                .message(ex.getMessage())
                .code(ex.getErrorCode().getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de archivos.
     */
    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ErrorResponse> handleFileValidationException(
            FileValidationException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("File Validation Error")
                .message(ex.getMessage())
                .code(ex.getErrorCode().getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de jerarquía de cuentas.
     */
    @ExceptionHandler(AccountCatalogueHierarchyException.class)
    public ResponseEntity<ErrorResponse> handleAccountCatalogueHierarchyException(
            AccountCatalogueHierarchyException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Hierarchy Error")
                .message(ex.getMessage())
                .code(ex.getErrorCode().getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de tamaño de archivo excedido (custom).
     */
    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileSizeExceededException(
            FileSizeExceededException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error("File Too Large")
                .message(ex.getMessage())
                .code(ex.getErrorCode().getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    private HttpStatus mapStatusFromErrorCode(String code) {
        if (code == null) {
            return HttpStatus.BAD_REQUEST;
        }
        String upper = code.toUpperCase();
        if (upper.endsWith("_NOT_FOUND") || upper.contains("NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        }
        if (upper.endsWith("_ALREADY_EXISTS") || upper.contains("DUPLICATE") || upper.contains("ASSOCIATED")) {
            return HttpStatus.CONFLICT;
        }
        if (upper.contains("IMPORT") || upper.contains("HIERARCHY") || upper.contains("FILE")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Maneja excepciones de violación de integridad de datos (claves foráneas).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {

        String errorMessage = ex.getMessage();
        
        // Verificar si es una violación de clave foránea relacionada con Tax
        if (errorMessage != null && errorMessage.contains("fkkndntrea9snpaq594re8whhmk") 
            && errorMessage.contains("table \"tax\"")) {
            
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.CONFLICT.value())
                    .error("Data Integrity Violation")
                    .message(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_TAX.getMessage())
                    .code(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_TAX.getCode())
                    .path(request.getDescription(false).replace("uri=", ""))
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        
        // Para otras violaciones de integridad, devolver error genérico
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Data Integrity Violation")
                .message("No se puede completar la operación debido a restricciones de integridad de datos")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones generales no específicas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ha ocurrido un error interno del servidor")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}