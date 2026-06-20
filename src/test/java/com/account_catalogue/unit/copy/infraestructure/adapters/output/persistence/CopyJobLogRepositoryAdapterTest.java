package com.account_catalogue.unit.copy.infraestructure.adapters.output.persistence;

import com.account_catalogue.copy.domain.enums.CopyJobState;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa.CopyJobLogJpaRepository;
import com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa.CopyJobLogRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.account_catalogue.copy.infraestructure.adapters.output.persistence.jpa.CopyJobLogEntity;

/**
 * Tests unitarios del adaptador de repositorio de CopyJobLog.
 */
@ExtendWith(MockitoExtension.class)
class CopyJobLogRepositoryAdapterTest {

    @Mock
    private CopyJobLogJpaRepository jpaRepository;

    private CopyJobLogRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CopyJobLogRepositoryAdapter(jpaRepository);
    }

    @Test
    @DisplayName("guardar — persiste y retorna modelo de dominio")
    void guardar_persisteYRetornaDominio() {
        UUID idProceso = UUID.randomUUID();
        CopyJobLog log = CopyJobLog.builder()
                .idProceso(idProceso).fase(1).modulo("account-catalogue")
                .estado(CopyJobState.COMPLETADO).fechaInicio(Instant.now())
                .equivalenciasGeneradas(5).build();

        CopyJobLogEntity savedEntity = CopyJobLogEntity.builder()
                .id(1L).idProceso(idProceso.toString()).fase(1).modulo("account-catalogue")
                .estado(CopyJobState.COMPLETADO).equivalenciasGeneradas(5).build();
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        CopyJobLog resultado = adapter.guardar(log);

        assertThat(resultado.getIdProceso()).isEqualTo(idProceso);
        assertThat(resultado.getEstado()).isEqualTo(CopyJobState.COMPLETADO);
        assertThat(resultado.getEquivalenciasGeneradas()).isEqualTo(5);
    }

    @Test
    @DisplayName("buscarPorIdProcesoYFase — retorna empty cuando no existe")
    void buscarPorIdProcesoYFase_noExiste_retornaEmpty() {
        when(jpaRepository.findByIdProcesoAndFase(any(), anyInt())).thenReturn(Optional.empty());

        Optional<CopyJobLog> resultado = adapter.buscarPorIdProcesoYFase("no-existe", 1);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorIdProcesoYFase — retorna log cuando existe")
    void buscarPorIdProcesoYFase_existe_retornaLog() {
        UUID idProceso = UUID.randomUUID();
        CopyJobLogEntity entity = CopyJobLogEntity.builder()
                .id(1L).idProceso(idProceso.toString()).fase(1).modulo("account-catalogue")
                .estado(CopyJobState.EN_PROCESO).equivalenciasGeneradas(0).build();
        when(jpaRepository.findByIdProcesoAndFase(idProceso.toString(), 1)).thenReturn(Optional.of(entity));

        Optional<CopyJobLog> resultado = adapter.buscarPorIdProcesoYFase(idProceso.toString(), 1);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getIdProceso()).isEqualTo(idProceso);
        assertThat(resultado.get().getEstado()).isEqualTo(CopyJobState.EN_PROCESO);
    }

    @Test
    @DisplayName("eliminarPorIdProceso — delega al repositorio JPA")
    void eliminarPorIdProceso_delegaAlJpaRepo() {
        String idProceso = UUID.randomUUID().toString();
        doNothing().when(jpaRepository).deleteByIdProceso(idProceso);

        adapter.eliminarPorIdProceso(idProceso);

        verify(jpaRepository).deleteByIdProceso(idProceso);
    }
}
