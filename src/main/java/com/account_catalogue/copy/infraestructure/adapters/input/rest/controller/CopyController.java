package com.account_catalogue.copy.infraestructure.adapters.input.rest.controller;

import com.account_catalogue.copy.application.input.*;
import com.account_catalogue.copy.domain.exceptions.CopyJobNotFoundException;
import com.account_catalogue.copy.domain.exceptions.InvalidCopyRequestException;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST del bounded context copy en account-catalogue.
 * Expone los 4 endpoints del contrato uniforme bajo /api/accountCatalogue/copy.
 * REQ-CONTRACT-01, ADR-18.
 */
@RestController
@RequestMapping("/api/accountCatalogue/copy")
@RequiredArgsConstructor
@Slf4j
public class CopyController {

    private final IExecuteCopyPhasePort executeCopyPhasePort;
    private final IGetCopyJobStatusPort getCopyJobStatusPort;
    private final ICancelCopyPort cancelCopyPort;
    private final ICleanupCopyPort cleanupCopyPort;

    /**
     * POST /api/accountCatalogue/copy/phase
     * Ejecuta una fase del proceso de copia.
     */
    @PostMapping("/phase")
    public ResponseEntity<CopyPhaseResponseDto> executePhase(
            @Valid @RequestBody CopyPhaseRequestDto request) {
        log.info("Ejecutando fase {} para proceso {}", request.getFase(), request.getIdProceso());
        CopyPhaseResponseDto response = executeCopyPhasePort.ejecutar(request);

        // Mapear estado a código HTTP
        HttpStatus status = resolverHttpStatus(response.getEstado());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/accountCatalogue/copy/{idProceso}/status
     * Consulta el estado de un proceso de copia.
     */
    @GetMapping("/{idProceso}/status")
    public ResponseEntity<CopyStatusResponseDto> getStatus(
            @PathVariable String idProceso) {
        CopyStatusResponseDto response = getCopyJobStatusPort.obtenerEstado(idProceso);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/accountCatalogue/copy/{idProceso}/cancel
     * Cancela un proceso de copia en curso.
     */
    @PostMapping("/{idProceso}/cancel")
    public ResponseEntity<CopyCancelResponseDto> cancel(
            @PathVariable String idProceso) {
        CopyCancelResponseDto response = cancelCopyPort.cancelar(idProceso);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/accountCatalogue/copy/{idProceso}/cleanup
     * Elimina registros temporales de un proceso terminado.
     */
    @DeleteMapping("/{idProceso}/cleanup")
    public ResponseEntity<Void> cleanup(
            @PathVariable String idProceso) {
        cleanupCopyPort.limpiar(idProceso);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // Manejo de excepciones
    // ----------------------------------------------------------------

    @ExceptionHandler(CopyJobNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CopyJobNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidCopyRequestException.class)
    public ResponseEntity<String> handleInvalidRequest(InvalidCopyRequestException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private HttpStatus resolverHttpStatus(String estado) {
        if (estado == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        return switch (estado) {
            case "COMPLETADO", "COMPLETADO_CON_ADVERTENCIAS" -> HttpStatus.OK;
            case "ERROR_NO_REINTENTABLE" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "ERROR_REINTENTABLE" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.OK;
        };
    }
}
