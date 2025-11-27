package com.account_catalogue.unit.commons.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.account_catalogue.commons.exceptions.BaseBusinessException;
import com.account_catalogue.commons.exceptions.ErrorCode;
import com.account_catalogue.commons.exceptions.ErrorCodeDefinition;
import com.account_catalogue.commons.exceptions.ErrorResponse;
import com.account_catalogue.commons.exceptions.GlobalExceptionHandler;
import com.account_catalogue.commons.exceptions.catalogue.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Tests Unitarios")
class GlobalExceptionHandlerUnitTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Debe manejar BaseBusinessException con código NOT_FOUND y retornar 404")
    void testHandleBusinessExceptionsWithNotFoundCode() {
        // Arrange
        BaseBusinessException exception = new AccountCatalogueNotFoundException();

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals(AccountCatalogueErrorCode.ACCOUNT_NOT_FOUND.getCode(), response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("Debe manejar BaseBusinessException con código ALREADY_EXISTS y retornar 409")
    void testHandleBusinessExceptionsWithAlreadyExistsCode() {
        // Arrange
        BaseBusinessException exception = new AccountCatalogueAlreadyExistsException("ACC001");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals(AccountCatalogueErrorCode.ACCOUNT_ALREADY_EXISTS.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar BaseBusinessException con código DUPLICATE y retornar 409")
    void testHandleBusinessExceptionsWithDuplicateCode() {
        // Arrange
        BaseBusinessException exception = new AccountCatalogueDescriptionAlreadyExistsException("Test Description");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar BaseBusinessException con código ASSOCIATED y retornar 409")
    void testHandleBusinessExceptionsWithAssociatedCode() {
        // Arrange
        BaseBusinessException exception = new AccountCatalogueAssociatedWithTaxException();

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar BaseBusinessException con código genérico y retornar 400")
    void testHandleBusinessExceptionsWithGenericCode() {
        // Arrange
        BaseBusinessException exception = new AccountCatalogueInUseException("ACC001", false);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar BaseBusinessException con código null y retornar 400")
    void testHandleBusinessExceptionsWithNullErrorCode() {
        // Arrange
        BaseBusinessException exception = mock(BaseBusinessException.class);
        ErrorCodeDefinition errorCodeDef = mock(ErrorCodeDefinition.class);
        when(exception.getErrorCode()).thenReturn(errorCodeDef);
        when(errorCodeDef.getCode()).thenReturn(null);
        when(exception.getMessage()).thenReturn("Error sin código");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con errores de campo")
    void testHandleMethodArgumentNotValidWithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("account", "code", "El código es obligatorio");
        FieldError fieldError2 = new FieldError("account", "description", "La descripción es obligatoria");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("fieldErrors"));
        
        ErrorResponse errorResponse = (ErrorResponse) body.get("error");
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Error de validación de campos", errorResponse.getMessage());
        
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertEquals(2, fieldErrors.size());
        assertEquals("El código es obligatorio", fieldErrors.get("code"));
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con campos duplicados tomando primer mensaje")
    void testHandleMethodArgumentNotValidWithDuplicateFields() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("account", "code", "Primer mensaje");
        FieldError fieldError2 = new FieldError("account", "code", "Segundo mensaje");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertEquals(1, fieldErrors.size());
        assertEquals("Primer mensaje", fieldErrors.get("code"));
    }

    @Test
    @DisplayName("Debe manejar ConstraintViolationException con violaciones")
    void testHandleConstraintViolationWithViolations() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("code");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("El código no puede estar vacío");
        
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("description");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("La descripción debe tener mínimo 3 caracteres");
        
        violations.add(violation1);
        violations.add(violation2);
        
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleConstraintViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("violations"));
        
        ErrorResponse errorResponse = (ErrorResponse) body.get("error");
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Error de validación", errorResponse.getMessage());
        
        @SuppressWarnings("unchecked")
        Map<String, String> violationsMap = (Map<String, String>) body.get("violations");
        assertEquals(2, violationsMap.size());
    }

    @Test
    @DisplayName("Debe manejar ConstraintViolationException con violaciones duplicadas")
    void testHandleConstraintViolationWithDuplicateViolations() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("code");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("Primer mensaje");
        
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("code");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("Segundo mensaje");
        
        violations.add(violation1);
        violations.add(violation2);
        
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleConstraintViolation(exception, webRequest);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        Map<String, String> violationsMap = (Map<String, String>) body.get("violations");
        assertEquals(1, violationsMap.size());
    }

    @Test
    @DisplayName("Debe manejar MaxUploadSizeExceededException y retornar 413")
    void testHandleMaxUploadSizeExceededException() {
        // Arrange
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(10485760);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMaxUploadSizeExceededException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(413, response.getBody().getStatus());
        assertEquals("Payload Too Large", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("MB"));
        assertEquals(AccountCatalogueErrorCode.FILE_SIZE_EXCEEDED.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar AccountCatalogueImportException y retornar 400")
    void testHandleAccountCatalogueImportException() {
        // Arrange
        AccountCatalogueImportException exception = new AccountCatalogueImportException(
            AccountCatalogueErrorCode.ACCOUNT_IMPORT_ERROR, "Error al procesar archivo");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccountCatalogueImportException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Import Error", response.getBody().getError());
        assertEquals("Error al procesar archivo", response.getBody().getMessage());
        assertEquals(AccountCatalogueErrorCode.ACCOUNT_IMPORT_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar FileValidationException y retornar 400")
    void testHandleFileValidationException() {
        // Arrange
        FileValidationException exception = new FileValidationException(
            AccountCatalogueErrorCode.EXCEL_VALIDATION_ERROR, "Archivo inválido");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFileValidationException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("File Validation Error", response.getBody().getError());
        assertEquals("Archivo inválido", response.getBody().getMessage());
        assertEquals(AccountCatalogueErrorCode.EXCEL_VALIDATION_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar AccountCatalogueHierarchyException y retornar 400")
    void testHandleAccountCatalogueHierarchyException() {
        // Arrange
        AccountCatalogueHierarchyException exception = new AccountCatalogueHierarchyException("Error de jerarquía");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccountCatalogueHierarchyException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Hierarchy Error", response.getBody().getError());
        assertEquals("Error de jerarquía", response.getBody().getMessage());
        assertEquals(AccountCatalogueErrorCode.ACCOUNT_HIERARCHY_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar FileSizeExceededException y retornar 413")
    void testHandleFileSizeExceededException() {
        // Arrange
        FileSizeExceededException exception = new FileSizeExceededException(10485760);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFileSizeExceededException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(413, response.getBody().getStatus());
        assertEquals("File Too Large", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("MB"));
        assertEquals(AccountCatalogueErrorCode.FILE_SIZE_EXCEEDED.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar DataIntegrityViolationException con Tax constraint y retornar 409")
    void testHandleDataIntegrityViolationExceptionWithTaxConstraint() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "could not execute statement; SQL [n/a]; constraint [fkkndntrea9snpaq594re8whhmk]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: " +
            "could not execute statement. Detail: Key (id)=(123) is still referenced from table \"tax\"."
        );

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Data Integrity Violation", response.getBody().getError());
        assertEquals(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_TAX.getMessage(), response.getBody().getMessage());
        assertEquals(AccountCatalogueErrorCode.ACCOUNT_ASSOCIATED_WITH_TAX.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar DataIntegrityViolationException genérica y retornar 409")
    void testHandleDataIntegrityViolationExceptionGeneric() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "could not execute statement; SQL [n/a]; constraint [some_other_constraint]"
        );

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Data Integrity Violation", response.getBody().getError());
        assertEquals("No se puede completar la operación debido a restricciones de integridad de datos", 
                     response.getBody().getMessage());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar DataIntegrityViolationException con mensaje null y retornar 409")
    void testHandleDataIntegrityViolationExceptionWithNullMessage() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("test", null);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Debe manejar Exception genérica y retornar 500")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Error inesperado");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ha ocurrido un error interno del servidor", response.getBody().getMessage());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), response.getBody().getCode());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("Debe verificar timestamp en ErrorResponse")
    void testErrorResponseContainsTimestamp() {
        // Arrange
        BaseBusinessException exception = new AccountCatalogueNotFoundException();

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe mapear código con IMPORT a BAD_REQUEST")
    void testMapStatusFromErrorCodeWithImport() {
        // Arrange
        AccountCatalogueImportException exception = new AccountCatalogueImportException(
            AccountCatalogueErrorCode.ACCOUNT_IMPORT_ERROR, "Import error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Debe mapear código con HIERARCHY a BAD_REQUEST")
    void testMapStatusFromErrorCodeWithHierarchy() {
        // Arrange
        AccountCatalogueHierarchyException exception = new AccountCatalogueHierarchyException("Hierarchy error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Debe mapear código con FILE a BAD_REQUEST")
    void testMapStatusFromErrorCodeWithFile() {
        // Arrange
        FileValidationException exception = new FileValidationException(
            AccountCatalogueErrorCode.EXCEL_VALIDATION_ERROR, "File error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
