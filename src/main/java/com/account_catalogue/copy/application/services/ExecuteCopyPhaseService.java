package com.account_catalogue.copy.application.services;

import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.commons.multitenancy.utils.TenantContext;
import com.account_catalogue.copy.application.input.IExecuteCopyPhasePort;
import com.account_catalogue.copy.application.output.*;
import com.account_catalogue.copy.domain.enums.CopyJobState;
import com.account_catalogue.copy.domain.exceptions.TopologicalSortCycleException;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.*;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que orquesta la ejecución de una fase de copia.
 * Implementa: idempotencia, topological sort, tenant override y copia Account + Tax.
 * REQ-PARTICIPANT-02, REQ-PARTICIPANT-03, REQ-PARTICIPANT-04, REQ-PARTICIPANT-06.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExecuteCopyPhaseService implements IExecuteCopyPhasePort {

    private static final String MODULO = "account-catalogue";

    private final ICopyJobLogRepositoryPort logRepo;
    private final IAccountSourceRepositoryPort accountSource;
    private final IAccountTargetRepositoryPort accountTarget;
    private final ITaxSourceRepositoryPort taxSource;
    private final ITaxTargetRepositoryPort taxTarget;
    private final ITopologicalSortPort topoSort;
    private final CopyJobIdempotencyChecker idempotencyChecker;
    private final EquivalenceMapper equivalenceMapper;

    @Override
    public CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request) {
        // Validación básica: entOrigen != entDestino
        if (request.getEntOrigen().equals(request.getEntDestino())) {
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_NO_REINTENTABLE")
                    .mensaje("entOrigen y entDestino no pueden ser iguales")
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();
        }

        String idProceso = request.getIdProceso().toString();

        // Idempotencia: si ya fue ejecutada esta fase, retornar resultado previo
        Optional<CopyJobLog> previo = idempotencyChecker.buscarEjecucionPrevia(idProceso, request.getFase());
        if (previo.isPresent()) {
            log.info("Fase {} del proceso {} ya fue ejecutada — retornando resultado previo", request.getFase(), idProceso);
            return construirResponseDesdeLog(previo.get());
        }

        // Registrar inicio
        CopyJobLog logInicio = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(CopyJobState.EN_PROCESO)
                .fechaInicio(Instant.now())
                .equivalenciasGeneradas(0)
                .build();
        logRepo.guardar(logInicio);

        // Limpiar el mapper para esta ejecución
        equivalenceMapper.limpiar();

        List<String> advertencias = new ArrayList<>();

        try {
            // ---- Tenant override: forzar entDestino durante toda la ejecución ----
            String tenantOriginal = TenantContext.getTenantId();
            TenantContext.setTenantId(request.getEntDestino());

            int totalRegistros = 0;

            try {
                // ---- Copiar Accounts ----
                totalRegistros += copiarAccounts(request, advertencias);

                // ---- Copiar Taxes ----
                totalRegistros += copiarTaxes(request, advertencias);

            } finally {
                // Restaurar tenant original (o limpiar si no había)
                if (tenantOriginal != null) {
                    TenantContext.setTenantId(tenantOriginal);
                } else {
                    TenantContext.clear();
                }
            }

            // Determinar estado final
            CopyJobState estadoFinal = advertencias.isEmpty()
                    ? CopyJobState.COMPLETADO
                    : CopyJobState.COMPLETADO_CON_ADVERTENCIAS;

            List<CopyEquivalenciaDto> equivalencias = equivalenceMapper.toList().stream()
                    .map(eq -> CopyEquivalenciaDto.builder()
                            .tabla(eq.getTabla())
                            .idViejo(eq.getIdViejo())
                            .idNuevo(eq.getIdNuevo())
                            .build())
                    .collect(Collectors.toList());

            // Guardar log final
            CopyJobLog logFin = CopyJobLog.builder()
                    .idProceso(request.getIdProceso())
                    .fase(request.getFase())
                    .modulo(MODULO)
                    .estado(estadoFinal)
                    .fechaInicio(logInicio.getFechaInicio())
                    .fechaFin(Instant.now())
                    .equivalenciasGeneradas(equivalencias.size())
                    .build();
            logRepo.guardar(logFin);

            return CopyPhaseResponseDto.builder()
                    .estado(estadoFinal.name())
                    .registrosProcesados(totalRegistros)
                    .equivalenciasGeneradas(equivalencias)
                    .mensaje("Copia completada exitosamente")
                    .advertencias(advertencias)
                    .build();

        } catch (TopologicalSortCycleException e) {
            log.error("Ciclo detectado en jerarquía de cuentas para proceso {}: {}", idProceso, e.getMessage());
            registrarFallo(request, e.getMessage());
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_NO_REINTENTABLE")
                    .mensaje("ciclo detectado en jerarquía de cuentas")
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Error inesperado durante copia del proceso {}: {}", idProceso, e.getMessage(), e);
            registrarFallo(request, e.getMessage());
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_REINTENTABLE")
                    .mensaje("Error interno: " + e.getMessage())
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();
        }
    }

    // ----------------------------------------------------------------
    // Copiar cuentas con topological sort
    // ----------------------------------------------------------------
    private int copiarAccounts(CopyPhaseRequestDto request, List<String> advertencias) {
        List<AccountCatalogueEntity> cuentasOrigen = accountSource
                .findByEntOrigenBeforeSnapshot(request.getEntOrigen(), request.getSnapshotCorte());

        if (cuentasOrigen.isEmpty()) {
            return 0;
        }

        // Ordenar topológicamente (lanza TopologicalSortCycleException si hay ciclo)
        List<AccountCatalogueEntity> cuentasOrdenadas = topoSort.ordenar(cuentasOrigen);

        // Mapa rápido de IDs presentes en el conjunto origen
        Set<Long> idsOrigen = cuentasOrdenadas.stream()
                .map(AccountCatalogueEntity::getId)
                .collect(Collectors.toSet());

        int copiadas = 0;
        for (AccountCatalogueEntity original : cuentasOrdenadas) {
            // Construir entidad nueva sin id (IDENTITY lo genera)
            // Remapear parent_id usando equivalencias ya registradas
            AccountCatalogueEntity nuevaParent = resolverNuevoParent(original, idsOrigen, advertencias);

            AccountCatalogueEntity nueva = AccountCatalogueEntity.builder()
                    .code(original.getCode())
                    .description(original.getDescription())
                    .nature(original.getNature())
                    .financialStatus(original.getFinancialStatus())
                    .classification(original.getClassification())
                    .parent(nuevaParent)
                    .idEnterprise(request.getEntDestino())
                    .tenantId(request.getEntDestino())
                    .crossing(original.getCrossing())
                    .costCenter(original.getCostCenter())
                    .status(original.getStatus())
                    .amount(original.getAmount())
                    .usageCount(0)
                    .build();

            AccountCatalogueEntity guardada = accountTarget.guardar(nueva);
            equivalenceMapper.registrar("account", original.getId(), guardada.getId());
            copiadas++;
        }

        return copiadas;
    }

    // ----------------------------------------------------------------
    // Copiar impuestos con remapeo de FKs
    // ----------------------------------------------------------------
    private int copiarTaxes(CopyPhaseRequestDto request, List<String> advertencias) {
        List<TaxEntity> taxesOrigen = taxSource
                .findByEntOrigenBeforeSnapshot(request.getEntOrigen(), request.getSnapshotCorte());

        if (taxesOrigen.isEmpty()) {
            return 0;
        }

        int copiados = 0;
        for (TaxEntity original : taxesOrigen) {
            // Remapear salesTax y purchaseTax usando equivalencias de account
            AccountCatalogueEntity nuevoSalesTax = remapearFkAccount(
                    original.getSalesTax(), "salesTax", original.getId(), advertencias);
            AccountCatalogueEntity nuevoPurchaseTax = remapearFkAccount(
                    original.getPurchaseTax(), "purchaseTax", original.getId(), advertencias);

            TaxEntity nuevo = TaxEntity.builder()
                    .code(original.getCode())
                    .description(original.getDescription())
                    .interest(original.getInterest())
                    .salesTax(nuevoSalesTax)
                    .purchaseTax(nuevoPurchaseTax)
                    .idEnterprise(request.getEntDestino())
                    .tenantId(request.getEntDestino())
                    .status(original.getStatus())
                    .usageCount(0)
                    .build();

            TaxEntity guardado = taxTarget.guardar(nuevo);
            equivalenceMapper.registrar("tax", original.getId(), guardado.getId());
            copiados++;
        }

        return copiados;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private AccountCatalogueEntity resolverNuevoParent(
            AccountCatalogueEntity original,
            Set<Long> idsOrigen,
            List<String> advertencias) {

        if (original.getParent() == null) {
            return null;
        }
        Long parentIdViejo = original.getParent().getId();
        if (!idsOrigen.contains(parentIdViejo)) {
            // Padre ausente del conjunto — tratar como raíz y registrar advertencia
            advertencias.add("Cuenta " + original.getId() + " tenía parent_id=" + parentIdViejo
                    + " que no existe en entOrigen; se insertó como raíz.");
            return null;
        }
        Long nuevoParentId = equivalenceMapper.resolverNuevoId("account", parentIdViejo);
        if (nuevoParentId == null) {
            advertencias.add("Cuenta " + original.getId() + " tenía parent_id=" + parentIdViejo
                    + " sin equivalencia disponible; se insertó como raíz.");
            return null;
        }
        return AccountCatalogueEntity.builder().id(nuevoParentId).build();
    }

    private AccountCatalogueEntity remapearFkAccount(
            AccountCatalogueEntity fkRef,
            String nombreCampo,
            Long taxId,
            List<String> advertencias) {

        if (fkRef == null) {
            return null;
        }
        Long nuevoId = equivalenceMapper.resolverNuevoId("account", fkRef.getId());
        if (nuevoId == null) {
            advertencias.add("Tax " + taxId + " campo " + nombreCampo
                    + " FK account_id=" + fkRef.getId() + " sin equivalencia; se insertó como null.");
            return null;
        }
        return AccountCatalogueEntity.builder().id(nuevoId).build();
    }

    private CopyPhaseResponseDto construirResponseDesdeLog(CopyJobLog log) {
        return CopyPhaseResponseDto.builder()
                .estado(log.getEstado().name())
                .registrosProcesados(log.getEquivalenciasGeneradas() != null ? log.getEquivalenciasGeneradas() : 0)
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Resultado de ejecución previa (idempotencia)")
                .advertencias(Collections.emptyList())
                .build();
    }

    private void registrarFallo(CopyPhaseRequestDto request, String mensaje) {
        try {
            CopyJobLog logFallo = CopyJobLog.builder()
                    .idProceso(request.getIdProceso())
                    .fase(request.getFase())
                    .modulo(MODULO)
                    .estado(CopyJobState.FALLIDO)
                    .fechaInicio(Instant.now())
                    .fechaFin(Instant.now())
                    .equivalenciasGeneradas(0)
                    .errorMessage(mensaje)
                    .build();
            logRepo.guardar(logFallo);
        } catch (Exception e) {
            log.error("Error al registrar fallo de copia: {}", e.getMessage());
        }
    }
}
