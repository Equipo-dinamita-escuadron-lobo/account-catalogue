package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.domain.enums.ImportStatus;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueTemplateData;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueExportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief Procesador asíncrono para exportación de catálogo de cuentas
 *
 * Maneja el flujo completo de exportación en un hilo separado, actualizando
 * el estado del trabajo en tiempo real a través del JobTracker.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueAsyncExportProcessor {

    private final AccountCatalogueExportJobTracker jobTracker;
    private final IAccountCatalogueSearchInputPort accountCatalogueSearchInputPort;
    private final AccountCatalogueExcelValidationService excelValidationService;

    private static final int EXPORT_PAGE_SIZE = 1000;

    /**
     * @brief Procesa la exportación de forma asíncrona
     * @param entId identificador de la entidad
     * @param status filtro por estado (true=activos, false=inactivos, null=todos)
     * @param jobId identificador del trabajo
     */
    @Async
    public void processExportAsync(String entId, Boolean status, String jobId) {
        log.info("JobId {}: Iniciando exportación asíncrona de catálogo de cuentas para entidad: {}", jobId, entId);

        long startTime = System.currentTimeMillis();
        Map<String, Long> phaseTimes = new LinkedHashMap<>();

        try {
            jobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
            jobTracker.updateProgress(jobId, 10);

            // ==================== FASE 1: OBTENCIÓN DE DATOS ====================
            long phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 1 - Obtención de datos", jobId);

            List<AccountCatalogue> accountCatalogues = getAllAccountCataloguesWithPagination(entId, status);

            long phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("1. Obtención Datos", phaseDuration);
            log.info("JobId {}: FASE 1 completada en {} ms. Cuentas obtenidas: {}", 
                    jobId, phaseDuration, accountCatalogues.size());

            if (accountCatalogues.isEmpty()) {
                handleNoData(jobId, status);
                return;
            }

            jobTracker.updateTotalRecords(jobId, accountCatalogues.size());
            jobTracker.updateProgress(jobId, 50);

            // ==================== FASE 2: GENERACIÓN EXCEL ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 2 - Generación de archivo Excel", jobId);

            List<AccountCatalogueTemplateData> templateData = convertToTemplateData(accountCatalogues);
            byte[] excelData = generateExcelFile(templateData);

            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("2. Generación Excel", phaseDuration);
            log.info("JobId {}: FASE 2 completada en {} ms. Archivo generado: {} bytes", 
                    jobId, phaseDuration, excelData.length);

            jobTracker.updateProgress(jobId, 90);

            // ==================== FASE 3: ALMACENAMIENTO ====================
            phaseStart = System.currentTimeMillis();
            log.info("JobId {}: FASE 3 - Almacenamiento del archivo", jobId);

            jobTracker.setFileData(jobId, excelData);

            phaseDuration = System.currentTimeMillis() - phaseStart;
            phaseTimes.put("3. Almacenamiento", phaseDuration);
            log.info("JobId {}: FASE 3 completada en {} ms", jobId, phaseDuration);

            // ==================== FINALIZACIÓN ====================
            long totalDuration = System.currentTimeMillis() - startTime;

            jobTracker.updateProgress(jobId, 100);
            jobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);

            printPhaseTimesTable(jobId, totalDuration, phaseTimes, accountCatalogues.size());

            log.info("JobId {}: Exportación completada exitosamente en {} ms", jobId, totalDuration);

        } catch (AccountCatalogueExportException e) {
            handleError(jobId, e.getMessage());
        } catch (Exception e) {
            log.error("JobId {}: Error crítico durante la exportación", jobId, e);
            handleError(jobId, "Error del sistema: " + e.getMessage());
        }
    }

    /**
     * @brief Obtiene todas las cuentas con paginación
     * @param entId identificador de la entidad
     * @param status filtro por estado
     * @return lista de cuentas
     */
    private List<AccountCatalogue> getAllAccountCataloguesWithPagination(String entId, Boolean status) {
        List<AccountCatalogue> allAccounts = new ArrayList<>();
        int currentPage = 0;
        Page<AccountCatalogue> page;

        do {
            Pageable pageable = PageRequest.of(currentPage, EXPORT_PAGE_SIZE);
            // Usar método para exportación con JOIN FETCH del parent
            page = accountCatalogueSearchInputPort.getAllAccountCataloguesForExport(entId, status, pageable);

            if (page != null && page.hasContent()) {
                allAccounts.addAll(page.getContent());
            }

            currentPage++;

        } while (page != null && page.hasNext());

        return allAccounts;
    }

    /**
     * @brief Convierte cuentas a datos de plantilla
     * @param accounts lista de cuentas
     * @return lista de datos de plantilla
     */
    private List<AccountCatalogueTemplateData> convertToTemplateData(List<AccountCatalogue> accounts) {
        return accounts.stream()
                .map(this::convertAccountToTemplateData)
                .toList();
    }

    /**
     * @brief Convierte una cuenta a datos de plantilla
     * @param account cuenta a convertir
     * @return datos de plantilla
     */
    private AccountCatalogueTemplateData convertAccountToTemplateData(AccountCatalogue account) {
        return AccountCatalogueTemplateData.builder()
                .code(account.getCode())
                .name(account.getDescription())
                .nature(account.getNature())
                .financialStatus(account.getFinancialStatus())
                .classification(account.getClassification())
                .cruce(account.getCrossing())
                .centroCosto(account.getCostCenter())
                .build();
    }

    /**
     * @brief Genera el archivo Excel con los datos
     * @param data datos de plantilla
     * @return bytes del archivo Excel
     */
    private byte[] generateExcelFile(List<AccountCatalogueTemplateData> data) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Catalogo_Cuentas");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle optionalHeaderStyle = createOptionalHeaderStyle(workbook);

            // Establecer anchos fijos de columnas ANTES de llenar datos (más eficiente)
            setFixedColumnWidths(sheet);
            
            createHeaders(sheet, headerStyle, optionalHeaderStyle);
            fillData(sheet, data, dataStyle);
            applyValidationsToDataSheet(sheet, data.size());

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createOptionalHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createHeaders(Sheet sheet, CellStyle requiredHeaderStyle, CellStyle optionalHeaderStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Código\n(Requerido)",
                "Nombre\n(Requerido)",
                "Naturaleza\n(Requerido)",
                "Estado Financiero\n(Requerido)",
                "Clasificación\n(Requerido)",
                "Cruce\n(Opcional)",
                "Centro de Costo\n(Opcional)"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(i >= 5 ? optionalHeaderStyle : requiredHeaderStyle);
        }

        headerRow.setHeightInPoints(40);
    }

    private void fillData(Sheet sheet, List<AccountCatalogueTemplateData> data, CellStyle dataStyle) {
        int rowIndex = 1;
        for (AccountCatalogueTemplateData item : data) {
            Row row = sheet.createRow(rowIndex++);
            fillDataRow(row, item, dataStyle);
        }
    }

    private void fillDataRow(Row row, AccountCatalogueTemplateData data, CellStyle style) {
        int colIndex = 0;
        
        // Optimización: crear celda y establecer estilo en una sola operación
        Cell cell;
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(data.getCode());
        cell.setCellStyle(style);
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(data.getName());
        cell.setCellStyle(style);
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(data.getNature().getState());
        cell.setCellStyle(style);
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(data.getFinancialStatus().getState());
        cell.setCellStyle(style);
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(data.getClassification().getState());
        cell.setCellStyle(style);
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(formatBooleanValue(data.getCruce()));
        cell.setCellStyle(style);
        
        cell = row.createCell(colIndex++);
        cell.setCellValue(formatBooleanValue(data.getCentroCosto()));
        cell.setCellStyle(style);
    }

    private String formatBooleanValue(Boolean value) {
        if (value == null) {
            return "";
        }
        return value ? "SI" : "NO";
    }

    /**
     * @brief Aplica validaciones de datos a la hoja de exportación
     * @param sheet hoja de Excel
     * @param dataRowCount número de filas con datos
     * @details Para exportaciones grandes (>3000 registros), NO aplica validaciones porque:
     * - Son archivos de solo lectura (no se van a editar miles de registros)
     * - Las validaciones condicionales son EXTREMADAMENTE costosas (N*2 validaciones individuales)
     * - Las validaciones son útiles solo para PLANTILLAS de importación o archivos pequeños editables
     * Para archivos medianos (<3000), aplica validaciones completas con buffer pequeño.
     */
    private void applyValidationsToDataSheet(Sheet sheet, int dataRowCount) throws Exception {
        // Para exportaciones grandes (>3000), omitir validaciones (son datos de solo lectura)
        if (dataRowCount > 3000) {
            log.info("Exportación grande ({} registros): Omitiendo validaciones para mejorar rendimiento", dataRowCount);
            return;
        }
        
        // Para archivos medianos, aplicar validaciones con buffer pequeño
        int startRow = 1;
        int buffer = dataRowCount > 1000 ? 50 : 100;
        int endRow = dataRowCount + buffer;
        log.debug("Aplicando validaciones a {} registros", dataRowCount);
        excelValidationService.applyAccountCatalogueValidations(sheet, startRow, endRow);
    }

    /**
     * @brief Establece anchos fijos para las columnas del Excel
     * @param sheet hoja de Excel
     * @details Usa anchos predefinidos basados en el contenido típico de cada columna.
     * Ancho en unidades de Excel (1 unidad = 1/256 de ancho de caracter)
     */
    private void setFixedColumnWidths(Sheet sheet) {
        // Código: típicamente 8-15 caracteres
        sheet.setColumnWidth(0, 4000);  // ~15 caracteres
        
        // Nombre/Descripción: puede ser largo
        sheet.setColumnWidth(1, 10000); // ~38 caracteres
        
        // Naturaleza: "DEBITO" o "CREDITO" (7-8 caracteres)
        sheet.setColumnWidth(2, 3500);  // ~13 caracteres
        
        // Estado Financiero: "ACTIVO", "PASIVO", etc (6-12 caracteres)
        sheet.setColumnWidth(3, 4500);  // ~17 caracteres
        
        // Clasificación: "AGRUPACION", "MOVIMIENTO", etc (10-15 caracteres)
        sheet.setColumnWidth(4, 4500);  // ~17 caracteres
        
        // Cruce: "SI" o "NO" (2 caracteres)
        sheet.setColumnWidth(5, 2500);  // ~9 caracteres
        
        // Centro de Costo: "SI" o "NO" (2 caracteres)
        sheet.setColumnWidth(6, 2500);  // ~13 caracteres
    }

    private void handleNoData(String jobId, Boolean status) {
        log.warn("JobId {}: No hay datos para exportar con el filtro especificado", jobId);
        jobTracker.setErrorMessage(jobId, AccountCatalogueExportException.forNoData(status).getMessage());
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.updateProgress(jobId, 100);
    }

    private void handleError(String jobId, String errorMessage) {
        log.error("JobId {}: Error en exportación: {}", jobId, errorMessage);
        jobTracker.setErrorMessage(jobId, errorMessage);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.updateProgress(jobId, 100);
    }

    private void printPhaseTimesTable(String jobId, long totalDuration, Map<String, Long> phaseTimes, int totalRecords) {
        double totalSeconds = totalDuration / 1000.0;
        double performance = totalRecords * 1000.0 / totalDuration;

        log.info("╔══════════════════════════════════════════════════════════════════════════════╗");
        log.info("║  RESUMEN DE TIEMPOS - JobId: {}                                        ║", jobId.substring(0, 8));
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        log.info("║  Fase                          │ Tiempo (ms) │ Tiempo (s) │      %          ║");
        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");

        for (Map.Entry<String, Long> entry : phaseTimes.entrySet()) {
            String fase = entry.getKey();
            long tiempo = entry.getValue();
            double segundos = tiempo / 1000.0;
            double porcentaje = (tiempo * 100.0) / totalDuration;

            String linea = String.format("║  %-29s │ %,11d │ %10.2f │ %6.1f%%         ║",
                    fase, tiempo, segundos, porcentaje);
            log.info(linea);
        }

        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        String lineaTotal = String.format("║  TOTAL                         │ %,11d │ %10.2f │  100.0%%         ║",
                totalDuration, totalSeconds);
        log.info(lineaTotal);

        log.info("╠══════════════════════════════════════════════════════════════════════════════╣");
        String lineaRegistros = String.format("║  Total Registros: %,8d                                                     ║",
                totalRecords);
        log.info(lineaRegistros);

        String lineaRendimiento = String.format("║  Rendimiento: %8.2f registros/seg                                        ║",
                performance);
        log.info(lineaRendimiento);
        log.info("╚══════════════════════════════════════════════════════════════════════════════╝");
    }
}

