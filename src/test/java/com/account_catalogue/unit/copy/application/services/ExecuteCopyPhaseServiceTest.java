package com.account_catalogue.unit.copy.application.services;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.copy.application.output.*;
import com.account_catalogue.copy.application.services.*;
import com.account_catalogue.copy.domain.enums.CopyJobState;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.*;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests del servicio orquestador de copia — ExecuteCopyPhaseService.
 * TDD RED → GREEN → REFACTOR.
 */
@ExtendWith(MockitoExtension.class)
class ExecuteCopyPhaseServiceTest {

    @Mock private ICopyJobLogRepositoryPort logRepo;
    @Mock private IAccountSourceRepositoryPort accountSource;
    @Mock private IAccountTargetRepositoryPort accountTarget;
    @Mock private ITaxSourceRepositoryPort taxSource;
    @Mock private ITaxTargetRepositoryPort taxTarget;
    @Mock private ITopologicalSortPort topoSort;

    private ExecuteCopyPhaseService service;

    @BeforeEach
    void setUp() {
        CopyJobIdempotencyChecker idempotencyChecker = new CopyJobIdempotencyChecker(logRepo);
        EquivalenceMapper eqMapper = new EquivalenceMapper();
        service = new ExecuteCopyPhaseService(
                logRepo, accountSource, accountTarget,
                taxSource, taxTarget, topoSort,
                idempotencyChecker, eqMapper);
    }

    // ----------------------------------------------------------------
    // Scenario: entOrigen == entDestino → 422
    // ----------------------------------------------------------------
    @Test
    @DisplayName("entOrigen == entDestino → estado ERROR_NO_REINTENTABLE con mensaje")
    void entOrigenIgualEntDestino_retornaErrorNoReintentable() {
        CopyPhaseRequestDto req = buildRequest("emp-a", "emp-a");

        CopyPhaseResponseDto resp = service.ejecutar(req);

        assertThat(resp.getEstado()).isEqualTo("ERROR_NO_REINTENTABLE");
        assertThat(resp.getMensaje()).contains("entOrigen y entDestino no pueden ser iguales");
    }

    // ----------------------------------------------------------------
    // Scenario: idempotencia — fase ya ejecutada retorna resultado previo
    // ----------------------------------------------------------------
    @Test
    @DisplayName("re-invocación con misma fase — retorna log previo sin re-ejecutar")
    void reinvocacion_retornaLogPrevio() {
        CopyPhaseRequestDto req = buildRequest("emp-a", "emp-b");
        CopyJobLog logPrevio = CopyJobLog.builder()
                .idProceso(req.getIdProceso())
                .fase(1)
                .estado(CopyJobState.COMPLETADO)
                .equivalenciasGeneradas(3)
                .build();
        when(logRepo.buscarPorIdProcesoYFase(req.getIdProceso().toString(), 1))
                .thenReturn(Optional.of(logPrevio));

        CopyPhaseResponseDto resp = service.ejecutar(req);

        assertThat(resp.getEstado()).isEqualTo("COMPLETADO");
        // No se debe haber consultado cuentas origen
        verify(accountSource, never()).findByEntOrigenBeforeSnapshot(any(), any());
    }

    // ----------------------------------------------------------------
    // Scenario: copia happy path — 5 cuentas raíz, 0 taxes
    // ----------------------------------------------------------------
    @Test
    @DisplayName("5 cuentas raíz sin taxes — COMPLETADO con 5 equivalencias de account")
    void cincoCuentasRaiz_copiaExitosa() {
        CopyPhaseRequestDto req = buildRequest("emp-a", "emp-b");
        when(logRepo.buscarPorIdProcesoYFase(any(), anyInt())).thenReturn(Optional.empty());

        List<AccountCatalogueEntity> cuentas = buildCuentas(5);
        when(accountSource.findByEntOrigenBeforeSnapshot(eq("emp-a"), any())).thenReturn(cuentas);
        when(topoSort.ordenar(cuentas)).thenReturn(cuentas);
        // Simular save: retorna entidad con nuevo id (viejo id + 100)
        when(accountTarget.guardar(any(AccountCatalogueEntity.class))).thenAnswer(inv -> {
            AccountCatalogueEntity e = inv.getArgument(0);
            AccountCatalogueEntity saved = AccountCatalogueEntity.builder()
                    .id(e.getId() != null ? e.getId() + 100 : 100L)
                    .code(e.getCode())
                    .idEnterprise("emp-b")
                    .tenantId("emp-b")
                    .build();
            return saved;
        });
        when(taxSource.findByEntOrigenBeforeSnapshot(eq("emp-a"), any())).thenReturn(Collections.emptyList());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        CopyPhaseResponseDto resp = service.ejecutar(req);

        assertThat(resp.getEstado()).isEqualTo("COMPLETADO");
        assertThat(resp.getRegistrosProcesados()).isEqualTo(5);
        assertThat(resp.getEquivalenciasGeneradas()).hasSize(5);
        assertThat(resp.getEquivalenciasGeneradas())
                .allSatisfy(eq -> assertThat(eq.getTabla()).isEqualTo("account"));
    }

    // ----------------------------------------------------------------
    // Scenario: ciclo en jerarquía → ERROR_NO_REINTENTABLE
    // ----------------------------------------------------------------
    @Test
    @DisplayName("ciclo en jerarquía — estado ERROR_NO_REINTENTABLE con mensaje ciclo")
    void cicloJerarquia_retornaErrorNoReintentable() {
        CopyPhaseRequestDto req = buildRequest("emp-a", "emp-b");
        when(logRepo.buscarPorIdProcesoYFase(any(), anyInt())).thenReturn(Optional.empty());

        List<AccountCatalogueEntity> cuentas = buildCuentas(2);
        when(accountSource.findByEntOrigenBeforeSnapshot(any(), any())).thenReturn(cuentas);
        when(topoSort.ordenar(cuentas)).thenThrow(
                new com.account_catalogue.copy.domain.exceptions.TopologicalSortCycleException());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        CopyPhaseResponseDto resp = service.ejecutar(req);

        assertThat(resp.getEstado()).isEqualTo("ERROR_NO_REINTENTABLE");
        assertThat(resp.getMensaje()).contains("ciclo detectado");
    }

    // ----------------------------------------------------------------
    // Scenario: tax con FK account válida remapeada correctamente
    // ----------------------------------------------------------------
    @Test
    @DisplayName("tax con salesTax FK — se remapea usando equivalencias de account")
    void taxConFkAccount_remapeado() {
        CopyPhaseRequestDto req = buildRequest("emp-a", "emp-b");
        when(logRepo.buscarPorIdProcesoYFase(any(), anyInt())).thenReturn(Optional.empty());

        // Una cuenta raíz con id=1
        AccountCatalogueEntity cuenta = AccountCatalogueEntity.builder()
                .id(1L).code("COD-1").idEnterprise("emp-a").build();
        when(accountSource.findByEntOrigenBeforeSnapshot(any(), any())).thenReturn(List.of(cuenta));
        when(topoSort.ordenar(any())).thenAnswer(inv -> inv.getArgument(0));
        // Guardar cuenta → nuevo id=101
        when(accountTarget.guardar(any())).thenReturn(
                AccountCatalogueEntity.builder().id(101L).code("COD-1").idEnterprise("emp-b").tenantId("emp-b").build()
        );

        // Un Tax con salesTax apuntando a cuenta id=1
        AccountCatalogueEntity salesTaxRef = AccountCatalogueEntity.builder().id(1L).build();
        TaxEntity tax = TaxEntity.builder()
                .id(10L).code("IVA").idEnterprise("emp-a").interest(19.0)
                .salesTax(salesTaxRef).build();
        when(taxSource.findByEntOrigenBeforeSnapshot(any(), any())).thenReturn(List.of(tax));
        when(taxTarget.guardar(any())).thenAnswer(inv -> {
            TaxEntity t = inv.getArgument(0);
            // Verificar que salesTax apunta al nuevo id 101
            assertThat(t.getSalesTax()).isNotNull();
            assertThat(t.getSalesTax().getId()).isEqualTo(101L);
            return TaxEntity.builder().id(110L).code(t.getCode()).idEnterprise("emp-b").tenantId("emp-b").interest(t.getInterest()).build();
        });
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        CopyPhaseResponseDto resp = service.ejecutar(req);

        assertThat(resp.getEstado()).isEqualTo("COMPLETADO");
        // account + tax = 2 equivalencias
        assertThat(resp.getEquivalenciasGeneradas()).hasSize(2);
        verify(taxTarget).guardar(any());
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private CopyPhaseRequestDto buildRequest(String entOrigen, String entDestino) {
        return CopyPhaseRequestDto.builder()
                .idProceso(UUID.randomUUID())
                .fase(1)
                .entOrigen(entOrigen)
                .entDestino(entDestino)
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();
    }

    private List<AccountCatalogueEntity> buildCuentas(int n) {
        List<AccountCatalogueEntity> lista = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            lista.add(AccountCatalogueEntity.builder()
                    .id((long) i).code("COD-" + i).idEnterprise("emp-a").build());
        }
        return lista;
    }
}
