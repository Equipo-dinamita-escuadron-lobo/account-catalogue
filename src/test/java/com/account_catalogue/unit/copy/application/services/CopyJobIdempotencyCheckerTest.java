package com.account_catalogue.unit.copy.application.services;

import com.account_catalogue.copy.application.output.ICopyJobLogRepositoryPort;
import com.account_catalogue.copy.application.services.CopyJobIdempotencyChecker;
import com.account_catalogue.copy.domain.enums.CopyJobState;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests de idempotencia: re-invocación con mismo idProceso retorna estado previo.
 */
@ExtendWith(MockitoExtension.class)
class CopyJobIdempotencyCheckerTest {

    @Mock
    private ICopyJobLogRepositoryPort repositoryPort;

    private CopyJobIdempotencyChecker checker;

    @BeforeEach
    void setUp() {
        checker = new CopyJobIdempotencyChecker(repositoryPort);
    }

    @Test
    @DisplayName("primera ejecución — no existe registro previo — retorna empty")
    void primeraEjecucion_retornaEmpty() {
        String idProceso = UUID.randomUUID().toString();
        when(repositoryPort.buscarPorIdProcesoYFase(idProceso, 1)).thenReturn(Optional.empty());

        Optional<CopyJobLog> resultado = checker.buscarEjecucionPrevia(idProceso, 1);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("re-invocación con mismo idProceso y fase — retorna log previo COMPLETADO")
    void reinvocacion_retornaLogPrevioCompletado() {
        String idProceso = UUID.randomUUID().toString();
        CopyJobLog logPrevio = CopyJobLog.builder()
                .idProceso(UUID.fromString(idProceso))
                .fase(1)
                .estado(CopyJobState.COMPLETADO)
                .equivalenciasGeneradas(5)
                .fechaInicio(Instant.now().minusSeconds(60))
                .fechaFin(Instant.now())
                .build();

        when(repositoryPort.buscarPorIdProcesoYFase(idProceso, 1)).thenReturn(Optional.of(logPrevio));

        Optional<CopyJobLog> resultado = checker.buscarEjecucionPrevia(idProceso, 1);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isEqualTo(CopyJobState.COMPLETADO);
        assertThat(resultado.get().getEquivalenciasGeneradas()).isEqualTo(5);
    }

    @Test
    @DisplayName("fase diferente — no es reutilizable — retorna empty")
    void faseDiferente_retornaEmpty() {
        String idProceso = UUID.randomUUID().toString();
        when(repositoryPort.buscarPorIdProcesoYFase(idProceso, 2)).thenReturn(Optional.empty());

        Optional<CopyJobLog> resultado = checker.buscarEjecucionPrevia(idProceso, 2);

        assertThat(resultado).isEmpty();
    }
}
