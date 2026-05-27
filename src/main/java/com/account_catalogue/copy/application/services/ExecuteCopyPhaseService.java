package com.account_catalogue.copy.application.services;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.commons.multitenancy.utils.TenantContext;
import com.account_catalogue.copy.application.input.IExecuteCopyPhasePort;
import com.account_catalogue.copy.application.output.*;
import com.account_catalogue.copy.domain.enums.CopyJobState;
import com.account_catalogue.copy.domain.exceptions.TopologicalSortCycleException;
import com.account_catalogue.copy.domain.models.CopyJobLog;
import com.account_catalogue.copy.infraestructure.adapters.input.rest.dto.*;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.banks.domain.enums.Currency;
import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
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
    private final IBankSourceRepositoryPort bankSource;
    private final IBankTargetRepositoryPort bankTarget;
    private final IBankAccountSourceRepositoryPort bankAccountSource;
    private final IBankAccountTargetRepositoryPort bankAccountTarget;
    private final IPaymentMethodSourceRepositoryPort paymentMethodSource;
    private final IPaymentMethodTargetRepositoryPort paymentMethodTarget;

    @Override
    public CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request) {
        // Modo RESTORE: importar datos desde backup
        if (request.getDatosImportados() != null) {
            return ejecutarImportacion(request);
        }
        // Modo BACKUP: exportar datos de la empresa origen
        if (request.getEntDestino() == null || request.getEntDestino().isBlank()) {
            return ejecutarExportacion(request);
        }

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

                // ---- Copiar Banks ----
                totalRegistros += copiarBanks(request, advertencias);

                // ---- Copiar BankAccounts (depende de Banks y Accounts) ----
                totalRegistros += copiarBankAccounts(request, advertencias);

                // ---- Copiar PaymentMethods (depende de Accounts) ----
                totalRegistros += copiarPaymentMethods(request, advertencias);

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
                            .modulo("CATALOGUE")
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

    // ----------------------------------------------------------------
    // Modo BACKUP: exportar datos de la empresa origen al ZIP
    // ----------------------------------------------------------------
    private CopyPhaseResponseDto ejecutarExportacion(CopyPhaseRequestDto request) {
        log.info("[BACKUP] ejecutarExportacion: entOrigen={}, snapshotCorte={}, tenantContext={}",
                request.getEntOrigen(), request.getSnapshotCorte(),
                com.account_catalogue.commons.multitenancy.utils.TenantContext.getTenantId());

        List<AccountCatalogueEntity> cuentas = accountSource
                .findByEntOrigenBeforeSnapshot(request.getEntOrigen(), request.getSnapshotCorte());

        List<java.util.Map<String, Object>> cuentasMaps = cuentas.stream().map(a -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("code", a.getCode());
            m.put("description", a.getDescription());
            m.put("nature", a.getNature() != null ? a.getNature().name() : null);
            m.put("financialStatus", a.getFinancialStatus() != null ? a.getFinancialStatus().name() : null);
            m.put("classification", a.getClassification() != null ? a.getClassification().name() : null);
            m.put("parentId", a.getParent() != null ? a.getParent().getId() : null);
            m.put("crossing", a.getCrossing());
            m.put("costCenter", a.getCostCenter());
            m.put("status", a.getStatus());
            m.put("amount", a.getAmount());
            return m;
        }).collect(Collectors.toList());

        List<TaxEntity> taxes = taxSource
                .findByEntOrigenBeforeSnapshot(request.getEntOrigen(), request.getSnapshotCorte());

        List<java.util.Map<String, Object>> taxesMaps = taxes.stream().map(t -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", t.getId());
            m.put("code", t.getCode());
            m.put("description", t.getDescription());
            m.put("interest", t.getInterest());
            m.put("salesTaxAccountId", t.getSalesTax() != null ? t.getSalesTax().getId() : null);
            m.put("purchaseTaxAccountId", t.getPurchaseTax() != null ? t.getPurchaseTax().getId() : null);
            m.put("status", t.getStatus());
            return m;
        }).collect(Collectors.toList());

        List<BankEntity> banks = bankSource.findByEntOrigen(request.getEntOrigen());
        List<java.util.Map<String, Object>> banksMaps = banks.stream().map(b -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", b.getId());
            m.put("code", b.getCode());
            m.put("name", b.getName());
            m.put("currencies", b.getCurrencies() != null
                    ? b.getCurrencies().stream().map(Enum::name).collect(Collectors.toList())
                    : Collections.emptyList());
            m.put("status", b.getStatus());
            return m;
        }).collect(Collectors.toList());

        List<BankAccountEntity> bankAccounts = bankAccountSource.findByEntOrigen(request.getEntOrigen());
        List<java.util.Map<String, Object>> bankAccountsMaps = bankAccounts.stream().map(ba -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", ba.getId());
            m.put("accountNumber", ba.getAccountNumber());
            m.put("bankId", ba.getBank() != null ? ba.getBank().getId() : null);
            m.put("accountType", ba.getAccountType() != null ? ba.getAccountType().name() : null);
            m.put("accountingAccountId", ba.getAccountingAccount() != null ? ba.getAccountingAccount().getId() : null);
            m.put("status", ba.getStatus());
            return m;
        }).collect(Collectors.toList());

        List<PaymentMethodEntity> paymentMethods = paymentMethodSource.findByEntOrigen(request.getEntOrigen());
        List<java.util.Map<String, Object>> paymentMethodsMaps = paymentMethods.stream().map(pm -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", pm.getId());
            m.put("name", pm.getName());
            m.put("accountingAccountId", pm.getAccountingAccount() != null ? pm.getAccountingAccount().getId() : null);
            m.put("status", pm.getStatus());
            return m;
        }).collect(Collectors.toList());

        java.util.Map<String, Object> datos = new java.util.LinkedHashMap<>();
        datos.put("accounts", cuentasMaps);
        datos.put("taxes", taxesMaps);
        datos.put("banks", banksMaps);
        datos.put("bankAccounts", bankAccountsMaps);
        datos.put("paymentMethods", paymentMethodsMaps);

        log.info("BACKUP catalogue: {} cuentas, {} taxes, {} banks, {} bankAccounts, {} paymentMethods exportadas desde empresa {}",
                cuentasMaps.size(), taxesMaps.size(), banksMaps.size(), bankAccountsMaps.size(),
                paymentMethodsMaps.size(), request.getEntOrigen());

        return CopyPhaseResponseDto.builder()
                .estado("COMPLETADO")
                .registrosProcesados(cuentasMaps.size() + taxesMaps.size() + banksMaps.size()
                        + bankAccountsMaps.size() + paymentMethodsMaps.size())
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Modo BACKUP — " + cuentasMaps.size() + " cuentas, " + taxesMaps.size() + " taxes, "
                        + banksMaps.size() + " banks, " + bankAccountsMaps.size() + " bankAccounts, "
                        + paymentMethodsMaps.size() + " paymentMethods exportadas")
                .advertencias(Collections.emptyList())
                .datosExportados(datos)
                .build();
    }

    // ----------------------------------------------------------------
    // Modo RESTORE: importar datos desde backup (datosImportados != null)
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private CopyPhaseResponseDto ejecutarImportacion(CopyPhaseRequestDto request) {
        String idProceso = request.getIdProceso().toString();

        // Idempotencia
        Optional<CopyJobLog> previo = idempotencyChecker.buscarEjecucionPrevia(idProceso, request.getFase());
        if (previo.isPresent()) {
            log.info("Fase {} proceso {} ya ejecutada (RESTORE) — retornando resultado previo", request.getFase(), idProceso);
            return construirResponseDesdeLog(previo.get());
        }

        CopyJobLog logInicio = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(CopyJobState.EN_PROCESO)
                .fechaInicio(Instant.now())
                .equivalenciasGeneradas(0)
                .build();

        equivalenceMapper.limpiar();
        List<String> advertencias = new ArrayList<>();

        java.util.Map<String, Object> datosImportados = (java.util.Map<String, Object>) request.getDatosImportados();

        String tenantOriginal = TenantContext.getTenantId();

        int totalRegistros = 0;
        try {
            totalRegistros += importarAccounts(datosImportados, request.getEntDestino(), advertencias);
            totalRegistros += importarTaxes(datosImportados, request.getEntDestino(), advertencias);
            totalRegistros += importarBanks(datosImportados, request.getEntDestino(), advertencias);
            totalRegistros += importarBankAccounts(datosImportados, request.getEntDestino(), advertencias);
            totalRegistros += importarPaymentMethods(datosImportados, request.getEntDestino(), advertencias);
        } finally {
            if (tenantOriginal != null) {
                TenantContext.setTenantId(tenantOriginal);
            } else {
                TenantContext.clear();
            }
        }

        CopyJobState estadoFinal = advertencias.isEmpty() ? CopyJobState.COMPLETADO : CopyJobState.COMPLETADO_CON_ADVERTENCIAS;

        List<CopyEquivalenciaDto> equivalencias = equivalenceMapper.toList().stream()
                .map(eq -> CopyEquivalenciaDto.builder()
                        .modulo("CATALOGUE")
                        .tabla(eq.getTabla())
                        .idViejo(eq.getIdViejo())
                        .idNuevo(eq.getIdNuevo())
                        .build())
                .collect(Collectors.toList());

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
                .mensaje("Restore completado: " + totalRegistros + " registros importados")
                .advertencias(advertencias)
                .build();
    }

    // ----------------------------------------------------------------
    // Importar accounts desde Map (RESTORE)
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private int importarAccounts(java.util.Map<String, Object> datos, String entDestino, List<String> advertencias) {
        List<java.util.Map<String, Object>> accountsList =
                (List<java.util.Map<String, Object>>) datos.get("accounts");
        if (accountsList == null || accountsList.isEmpty()) return 0;

        // Ordenar topológicamente con múltiples pasadas: primero raíces, luego hijos
        List<java.util.Map<String, Object>> pendientes = new ArrayList<>(accountsList);
        int maxPasadas = accountsList.size() + 1;
        int pasadas = 0;
        int copiadas = 0;

        while (!pendientes.isEmpty() && pasadas < maxPasadas) {
            List<java.util.Map<String, Object>> restantes = new ArrayList<>();
            for (java.util.Map<String, Object> accountMap : pendientes) {
                Long originalId = toLong(accountMap.get("id"));
                Long parentIdOriginal = toLong(accountMap.get("parentId"));

                AccountCatalogueEntity nuevoParent = null;
                if (parentIdOriginal != null) {
                    Long nuevoParentId = equivalenceMapper.resolverNuevoId("account", parentIdOriginal);
                    if (nuevoParentId == null) {
                        restantes.add(accountMap); // padre no procesado aún
                        continue;
                    }
                    nuevoParent = AccountCatalogueEntity.builder().id(nuevoParentId).build();
                }

                AccountCatalogueEntity nueva = AccountCatalogueEntity.builder()
                        .code(toString(accountMap.get("code")))
                        .description(toString(accountMap.get("description")))
                        .nature(parseEnum(accountMap.get("nature"), NatureEnum.class))
                        .financialStatus(parseEnum(accountMap.get("financialStatus"), FinancialStatusEnum.class))
                        .classification(parseEnum(accountMap.get("classification"), ClassificationEnum.class))
                        .parent(nuevoParent)
                        .idEnterprise(entDestino)
                        .tenantId(TenantContext.getTenantId())
                        .crossing(toBoolean(accountMap.get("crossing")))
                        .costCenter(toBoolean(accountMap.get("costCenter")))
                        .status(toBoolean(accountMap.get("status")))
                        .amount(toBigDecimal(accountMap.get("amount")))
                        .usageCount(0)
                        .build();

                AccountCatalogueEntity guardada = accountTarget.guardar(nueva);
                equivalenceMapper.registrar("account", originalId, guardada.getId());
                copiadas++;
            }
            pendientes = restantes;
            pasadas++;
        }

        if (!pendientes.isEmpty()) {
            advertencias.add("No se pudieron importar " + pendientes.size() + " cuentas (posible ciclo o padre ausente)");
        }
        return copiadas;
    }

    // ----------------------------------------------------------------
    // Importar taxes desde Map (RESTORE)
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private int importarTaxes(java.util.Map<String, Object> datos, String entDestino, List<String> advertencias) {
        List<java.util.Map<String, Object>> taxesList =
                (List<java.util.Map<String, Object>>) datos.get("taxes");
        if (taxesList == null || taxesList.isEmpty()) return 0;

        int copiados = 0;
        for (java.util.Map<String, Object> taxMap : taxesList) {
            Long originalId = toLong(taxMap.get("id"));

            AccountCatalogueEntity nuevoSalesTax = resolverFkAccount(
                    toLong(taxMap.get("salesTaxAccountId")), "salesTax", originalId, advertencias);
            AccountCatalogueEntity nuevoPurchaseTax = resolverFkAccount(
                    toLong(taxMap.get("purchaseTaxAccountId")), "purchaseTax", originalId, advertencias);

            TaxEntity nuevo = TaxEntity.builder()
                    .code(toString(taxMap.get("code")))
                    .description(toString(taxMap.get("description")))
                    .interest(toDouble(taxMap.get("interest")))
                    .salesTax(nuevoSalesTax)
                    .purchaseTax(nuevoPurchaseTax)
                    .idEnterprise(entDestino)
                    .tenantId(TenantContext.getTenantId())
                    .status(toBoolean(taxMap.get("status")))
                    .usageCount(0)
                    .build();

            TaxEntity guardado = taxTarget.guardar(nuevo);
            equivalenceMapper.registrar("tax", originalId, guardado.getId());
            copiados++;
        }
        return copiados;
    }

    // ----------------------------------------------------------------
    // Copiar bancos (directamente entre empresas)
    // ----------------------------------------------------------------
    private int copiarBanks(CopyPhaseRequestDto request, List<String> advertencias) {
        List<BankEntity> banksOrigen = bankSource.findByEntOrigen(request.getEntOrigen());
        if (banksOrigen.isEmpty()) return 0;

        int copiados = 0;
        for (BankEntity original : banksOrigen) {
            BankEntity nuevo = BankEntity.builder()
                    .code(original.getCode())
                    .name(original.getName())
                    .currencies(original.getCurrencies() != null
                            ? new java.util.HashSet<>(original.getCurrencies())
                            : new java.util.HashSet<>())
                    .status(original.getStatus())
                    .idEnterprise(request.getEntDestino())
                    .tenantId(request.getEntDestino())
                    .build();

            BankEntity guardado = bankTarget.guardar(nuevo);
            equivalenceMapper.registrar("bank", original.getId(), guardado.getId());
            copiados++;
        }
        return copiados;
    }

    // ----------------------------------------------------------------
    // Copiar cuentas bancarias (directamente entre empresas)
    // Ejecutar después de copiarBanks y copiarAccounts
    // ----------------------------------------------------------------
    private int copiarBankAccounts(CopyPhaseRequestDto request, List<String> advertencias) {
        List<BankAccountEntity> origen = bankAccountSource.findByEntOrigen(request.getEntOrigen());
        if (origen.isEmpty()) return 0;

        int copiados = 0;
        for (BankAccountEntity original : origen) {
            BankEntity nuevoBank = remapearFkBank(original.getBank(), original.getId(), advertencias);
            AccountCatalogueEntity nuevaCuenta = remapearFkAccount(
                    original.getAccountingAccount(), "accountingAccount", original.getId(), advertencias);

            BankAccountEntity nuevo = BankAccountEntity.builder()
                    .accountNumber(original.getAccountNumber())
                    .bank(nuevoBank)
                    .accountType(original.getAccountType())
                    .accountingAccount(nuevaCuenta)
                    .status(original.getStatus())
                    .idEnterprise(request.getEntDestino())
                    .tenantId(request.getEntDestino())
                    .usageCount(0)
                    .build();

            bankAccountTarget.guardar(nuevo);
            copiados++;
        }
        return copiados;
    }

    private BankEntity remapearFkBank(BankEntity fkRef, Long bankAccountId, List<String> advertencias) {
        if (fkRef == null) {
            advertencias.add("BankAccount " + bankAccountId + " sin banco asociado; se insertó como null.");
            return null;
        }
        Long nuevoId = equivalenceMapper.resolverNuevoId("bank", fkRef.getId());
        if (nuevoId == null) {
            advertencias.add("BankAccount " + bankAccountId + " FK bank_id=" + fkRef.getId() + " sin equivalencia; se insertó como null.");
            return null;
        }
        return BankEntity.builder().id(nuevoId).build();
    }

    // ----------------------------------------------------------------
    // Importar bancos desde Map (RESTORE)
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private int importarBanks(java.util.Map<String, Object> datos, String entDestino, List<String> advertencias) {
        List<java.util.Map<String, Object>> banksList =
                (List<java.util.Map<String, Object>>) datos.get("banks");
        if (banksList == null || banksList.isEmpty()) return 0;

        int copiados = 0;
        for (java.util.Map<String, Object> bankMap : banksList) {
            Long originalId = toLong(bankMap.get("id"));
            List<String> currencyNames = (List<String>) bankMap.get("currencies");
            java.util.Set<Currency> currencies = currencyNames != null
                    ? currencyNames.stream()
                        .map(n -> parseEnum(n, Currency.class))
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toCollection(java.util.HashSet::new))
                    : new java.util.HashSet<>();

            BankEntity nuevo = BankEntity.builder()
                    .code(toString(bankMap.get("code")))
                    .name(toString(bankMap.get("name")))
                    .currencies(currencies)
                    .status(toBoolean(bankMap.get("status")))
                    .idEnterprise(entDestino)
                    .tenantId(TenantContext.getTenantId())
                    .build();

            BankEntity guardado = bankTarget.guardar(nuevo);
            equivalenceMapper.registrar("bank", originalId, guardado.getId());
            copiados++;
        }
        return copiados;
    }

    // ----------------------------------------------------------------
    // Importar cuentas bancarias desde Map (RESTORE)
    // Ejecutar después de importarBanks e importarAccounts
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private int importarBankAccounts(java.util.Map<String, Object> datos, String entDestino, List<String> advertencias) {
        List<java.util.Map<String, Object>> bankAccountsList =
                (List<java.util.Map<String, Object>>) datos.get("bankAccounts");
        if (bankAccountsList == null || bankAccountsList.isEmpty()) return 0;

        int copiados = 0;
        for (java.util.Map<String, Object> baMap : bankAccountsList) {
            Long originalId = toLong(baMap.get("id"));

            Long bankIdOriginal = toLong(baMap.get("bankId"));
            BankEntity nuevoBank = null;
            if (bankIdOriginal != null) {
                Long nuevoId = equivalenceMapper.resolverNuevoId("bank", bankIdOriginal);
                if (nuevoId == null) {
                    advertencias.add("BankAccount " + originalId + " FK bank_id=" + bankIdOriginal
                            + " sin equivalencia; se insertó como null.");
                } else {
                    nuevoBank = BankEntity.builder().id(nuevoId).build();
                }
            }

            Long accountIdOriginal = toLong(baMap.get("accountingAccountId"));
            AccountCatalogueEntity nuevaCuenta = resolverFkAccount(
                    accountIdOriginal, "accountingAccount", originalId, advertencias);

            BankAccountEntity nuevo = BankAccountEntity.builder()
                    .accountNumber(toLong(baMap.get("accountNumber")))
                    .bank(nuevoBank)
                    .accountType(parseEnum(baMap.get("accountType"), AccountType.class))
                    .accountingAccount(nuevaCuenta)
                    .status(toBoolean(baMap.get("status")))
                    .idEnterprise(entDestino)
                    .tenantId(TenantContext.getTenantId())
                    .usageCount(0)
                    .build();

            bankAccountTarget.guardar(nuevo);
            copiados++;
        }
        return copiados;
    }

    // ----------------------------------------------------------------
    // Copiar métodos de pago (depende de Accounts)
    // ----------------------------------------------------------------
    private int copiarPaymentMethods(CopyPhaseRequestDto request, List<String> advertencias) {
        List<PaymentMethodEntity> origen = paymentMethodSource.findByEntOrigen(request.getEntOrigen());
        if (origen.isEmpty()) return 0;

        int copiados = 0;
        for (PaymentMethodEntity original : origen) {
            AccountCatalogueEntity nuevaCuenta = remapearFkAccount(
                    original.getAccountingAccount(), "accountingAccount", original.getId(), advertencias);

            PaymentMethodEntity nuevo = PaymentMethodEntity.builder()
                    .name(original.getName())
                    .accountingAccount(nuevaCuenta)
                    .status(original.getStatus())
                    .idEnterprise(request.getEntDestino())
                    .tenantId(request.getEntDestino())
                    .usageCount(0)
                    .build();

            paymentMethodTarget.guardar(nuevo);
            copiados++;
        }
        return copiados;
    }

    // ----------------------------------------------------------------
    // Importar métodos de pago desde Map (RESTORE)
    // Ejecutar después de importarAccounts
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private int importarPaymentMethods(java.util.Map<String, Object> datos, String entDestino, List<String> advertencias) {
        List<java.util.Map<String, Object>> paymentMethodsList =
                (List<java.util.Map<String, Object>>) datos.get("paymentMethods");
        if (paymentMethodsList == null || paymentMethodsList.isEmpty()) return 0;

        int copiados = 0;
        for (java.util.Map<String, Object> pmMap : paymentMethodsList) {
            Long originalId = toLong(pmMap.get("id"));

            Long accountIdOriginal = toLong(pmMap.get("accountingAccountId"));
            AccountCatalogueEntity nuevaCuenta = resolverFkAccount(
                    accountIdOriginal, "accountingAccount", originalId, advertencias);

            PaymentMethodEntity nuevo = PaymentMethodEntity.builder()
                    .name(toString(pmMap.get("name")))
                    .accountingAccount(nuevaCuenta)
                    .status(toBoolean(pmMap.get("status")))
                    .idEnterprise(entDestino)
                    .tenantId(TenantContext.getTenantId())
                    .usageCount(0)
                    .build();

            paymentMethodTarget.guardar(nuevo);
            copiados++;
        }
        return copiados;
    }

    private AccountCatalogueEntity resolverFkAccount(Long fkId, String campo, Long entidadId, List<String> advertencias) {
        if (fkId == null) return null;
        Long nuevoId = equivalenceMapper.resolverNuevoId("account", fkId);
        if (nuevoId == null) {
            advertencias.add("Tax " + entidadId + " campo " + campo + " FK account_id=" + fkId + " sin equivalencia; se insertó como null.");
            return null;
        }
        return AccountCatalogueEntity.builder().id(nuevoId).build();
    }

    // ----------------------------------------------------------------
    // Helpers de conversión de tipos desde Map
    // (Jackson deserializa números como Integer o Long según el valor)
    // ----------------------------------------------------------------
    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Long l) return l;
        if (val instanceof Integer i) return i.longValue();
        if (val instanceof Number n) return n.longValue();
        return null;
    }

    private String toString(Object val) {
        return val != null ? val.toString() : null;
    }

    private Boolean toBoolean(Object val) {
        if (val instanceof Boolean b) return b;
        return false;
    }

    private Double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Double d) return d;
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private java.math.BigDecimal toBigDecimal(Object val) {
        if (val == null) return java.math.BigDecimal.ZERO;
        if (val instanceof java.math.BigDecimal bd) return bd;
        if (val instanceof Number n) return java.math.BigDecimal.valueOf(n.doubleValue());
        return java.math.BigDecimal.ZERO;
    }

    private <E extends Enum<E>> E parseEnum(Object val, Class<E> enumClass) {
        if (val == null) return null;
        try { return Enum.valueOf(enumClass, val.toString()); }
        catch (Exception e) { return null; }
    }
}
