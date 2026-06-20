package com.account_catalogue.unit.copy.infraestructure.adapters.input.rest;

import com.account_catalogue.copy.application.input.*;
import com.account_catalogue.copy.domain.exceptions.CopyJobNotFoundException;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.controller.CopyController;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del CopyController.
 * Siguiendo el patrón existente de controladores en el proyecto.
 */
@ExtendWith(MockitoExtension.class)
class CopyControllerUnitTest {

    @Mock private IExecuteCopyPhasePort executeCopyPhasePort;
    @Mock private IGetCopyJobStatusPort getCopyJobStatusPort;
    @Mock private ICancelCopyPort cancelCopyPort;
    @Mock private ICleanupCopyPort cleanupCopyPort;

    @InjectMocks
    private CopyController controller;

    // ----------------------------------------------------------------
    // POST /phase — happy path → 200
    // ----------------------------------------------------------------
    @Test
    @DisplayName("POST /phase COMPLETADO → HTTP 200")
    void executePhase_completado_retorna200() {
        CopyPhaseRequestDto req = buildRequest();
        CopyPhaseResponseDto resp = CopyPhaseResponseDto.builder()
                .estado("COMPLETADO").registrosProcesados(3)
                .equivalenciasGeneradas(Collections.emptyList())
                .advertencias(Collections.emptyList())
                .build();
        when(executeCopyPhasePort.ejecutar(req)).thenReturn(resp);

        ResponseEntity<CopyPhaseResponseDto> result = controller.executePhase(req);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEstado()).isEqualTo("COMPLETADO");
    }

    // ----------------------------------------------------------------
    // POST /phase — ERROR_NO_REINTENTABLE → 422
    // ----------------------------------------------------------------
    @Test
    @DisplayName("POST /phase ERROR_NO_REINTENTABLE → HTTP 422")
    void executePhase_errorNoReintentable_retorna422() {
        CopyPhaseRequestDto req = buildRequest();
        CopyPhaseResponseDto resp = CopyPhaseResponseDto.builder()
                .estado("ERROR_NO_REINTENTABLE").mensaje("error semántico")
                .equivalenciasGeneradas(Collections.emptyList())
                .advertencias(Collections.emptyList())
                .build();
        when(executeCopyPhasePort.ejecutar(req)).thenReturn(resp);

        ResponseEntity<CopyPhaseResponseDto> result = controller.executePhase(req);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // ----------------------------------------------------------------
    // POST /phase — ERROR_REINTENTABLE → 500
    // ----------------------------------------------------------------
    @Test
    @DisplayName("POST /phase ERROR_REINTENTABLE → HTTP 500")
    void executePhase_errorReintentable_retorna500() {
        CopyPhaseRequestDto req = buildRequest();
        CopyPhaseResponseDto resp = CopyPhaseResponseDto.builder()
                .estado("ERROR_REINTENTABLE").mensaje("error interno")
                .equivalenciasGeneradas(Collections.emptyList())
                .advertencias(Collections.emptyList())
                .build();
        when(executeCopyPhasePort.ejecutar(req)).thenReturn(resp);

        ResponseEntity<CopyPhaseResponseDto> result = controller.executePhase(req);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ----------------------------------------------------------------
    // GET /status — happy path → 200
    // ----------------------------------------------------------------
    @Test
    @DisplayName("GET /{idProceso}/status → HTTP 200 con estado")
    void getStatus_retorna200() {
        String idProceso = UUID.randomUUID().toString();
        CopyStatusResponseDto resp = CopyStatusResponseDto.builder()
                .fase(1).estado("COMPLETADO").registrosProcesados(5).build();
        when(getCopyJobStatusPort.obtenerEstado(idProceso)).thenReturn(resp);

        ResponseEntity<CopyStatusResponseDto> result = controller.getStatus(idProceso);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getEstado()).isEqualTo("COMPLETADO");
    }

    // ----------------------------------------------------------------
    // GET /status — no encontrado → 404
    // ----------------------------------------------------------------
    @Test
    @DisplayName("handler CopyJobNotFoundException → HTTP 404")
    void handleNotFound_retorna404() {
        ResponseEntity<String> result = controller.handleNotFound(
                new CopyJobNotFoundException("no-existe"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).contains("no-existe");
    }

    // ----------------------------------------------------------------
    // POST /cancel — happy path → 200
    // ----------------------------------------------------------------
    @Test
    @DisplayName("POST /{idProceso}/cancel → HTTP 200 con estado CANCELADO")
    void cancel_retorna200() {
        String idProceso = UUID.randomUUID().toString();
        CopyCancelResponseDto resp = CopyCancelResponseDto.builder().estado("CANCELADO").build();
        when(cancelCopyPort.cancelar(idProceso)).thenReturn(resp);

        ResponseEntity<CopyCancelResponseDto> result = controller.cancel(idProceso);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getEstado()).isEqualTo("CANCELADO");
    }

    // ----------------------------------------------------------------
    // DELETE /cleanup — happy path → 204
    // ----------------------------------------------------------------
    @Test
    @DisplayName("DELETE /{idProceso}/cleanup → HTTP 204")
    void cleanup_retorna204() {
        String idProceso = UUID.randomUUID().toString();
        doNothing().when(cleanupCopyPort).limpiar(idProceso);

        ResponseEntity<Void> result = controller.cleanup(idProceso);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(cleanupCopyPort).limpiar(idProceso);
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------
    private CopyPhaseRequestDto buildRequest() {
        return CopyPhaseRequestDto.builder()
                .idProceso(UUID.randomUUID())
                .fase(1)
                .entOrigen("emp-a")
                .entDestino("emp-b")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();
    }
}
