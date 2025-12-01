package com.account_catalogue.commons.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueErrorCode;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueHierarchyException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueImportException;
import com.account_catalogue.commons.exceptions.catalogue.FileSizeExceededException;
import com.account_catalogue.commons.exceptions.catalogue.FileValidationException;

import com.account_catalogue.catalogue.domain.utils.ImportConstants;

import org.springframework.data.mapping.PropertyReferenceException;

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
     * Maneja excepciones cuando falta un parámetro de solicitud requerido.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {

        String message = String.format("El parámetro '%s' es requerido", ex.getParameterName());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando un argumento de método no puede ser convertido al tipo esperado.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String paramName = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "desconocido";

        String message = String.format("El valor '%s' no es válido para el parámetro '%s'. Se esperaba un valor de tipo %s",
                value, paramName, typeName);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de tamaño de archivo excedido por Spring Boot.
     * Esta excepción es lanzada por Spring antes de que llegue al controlador.
     * Delega a FileSizeExceededException para reutilizar la lógica de formateo.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {

        // Crear una instancia de FileSizeExceededException para reutilizar su mensaje formateado
        long maxSize = ImportConstants.MAX_FILE_SIZE;
        FileSizeExceededException fileSizeException = new FileSizeExceededException(maxSize);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error("Payload Too Large")
                .message(fileSizeException.getMessage())
                .code(fileSizeException.getErrorCode().getCode())
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
     * Maneja excepciones de argumentos ilegales (parámetros de paginación inválidos).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        String message = ex.getMessage();
        
        // Mensajes específicos para errores de paginación
        if (message != null && message.contains("Page index must not be less than zero")) {
            message = "El índice de página no puede ser menor a cero";
        } else if (message != null && message.contains("Page size must not be less than one")) {
            message = "El tamaño de página debe ser mayor a cero";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de referencia de propiedad inválida (campos de ordenamiento inexistentes).
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReferenceException(
            PropertyReferenceException ex, WebRequest request) {

        String propertyName = ex.getPropertyName();
        String message = String.format("El campo de ordenamiento '%s' no es válido", propertyName);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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