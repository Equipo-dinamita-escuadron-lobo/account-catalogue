package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueExportInputPort;
import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.domain.models.AccountCatalogueTemplateData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Servicio para la exportación del catálogo de cuentas a formato Excel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCatalogueExportService implements IAccountCatalogueExportInputPort {

    private final AccountCatalogueExcelValidationService excelValidationService;

    @Override
    public Resource exportAccountCatalogueTemplate(String entId) {
        try {
            byte[] templateData = generateTemplateWithValidations(entId);
            return new ByteArrayResource(templateData);
        } catch (Exception e) {
            log.error("Error generando plantilla de catálogo de cuentas", e);
            throw new RuntimeException("Error al generar plantilla", e);
        }
    }

    @Override
    public Resource exportAccountCatalogueWithValidations(String entId) {
        try {
            // Por ahora, exportamos la plantilla con datos quemados
            // En el futuro, aquí se obtendrían los datos reales de la BD
            List<AccountCatalogueTemplateData> templateData = getHardcodedTemplateData();
            byte[] excelData = generateExcelFileWithValidations(templateData);
            return new ByteArrayResource(excelData);
        } catch (Exception e) {
            log.error("Error generando archivo de exportación de catálogo de cuentas", e);
            throw new RuntimeException("Error al generar archivo de exportación", e);
        }
    }

    private byte[] generateTemplateWithValidations(String entId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Plantilla_Catalogo_Cuentas");

            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle templateStyle = createTemplateStyle(workbook);

            // Crear encabezados
            createHeaders(sheet, headerStyle, createOptionalHeaderStyle(workbook));

            // Crear filas de ejemplo con estilos
            List<AccountCatalogueTemplateData> exampleData = getHardcodedTemplateData();
            createTemplateRows(sheet, templateStyle, exampleData);

            // Aplicar validaciones de datos
            applyValidationsToTemplate(sheet);

            // Ajustar ancho de columnas
            autoSizeColumns(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] generateExcelFileWithValidations(List<AccountCatalogueTemplateData> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Catalogo_Cuentas");

            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Crear encabezados
            createHeaders(sheet, headerStyle, createOptionalHeaderStyle(workbook));

            // Llenar datos
            fillData(sheet, data, dataStyle);

            // Aplicar validaciones de datos
            applyValidationsToDataSheet(sheet, data.size());

            // Ajustar ancho de columnas
            autoSizeColumns(sheet);

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

    private CellStyle createTemplateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
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

    private CellStyle createOptionalHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
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
            if (i >= 5) { // Columnas 5 y 6 son opcionales
                cell.setCellStyle(optionalHeaderStyle);
            } else {
                cell.setCellStyle(requiredHeaderStyle);
            }
        }

        headerRow.setHeightInPoints(40);
    }

    private void createTemplateRows(Sheet sheet, CellStyle templateStyle, List<AccountCatalogueTemplateData> exampleData) {
        int rowIndex = 1;
        for (AccountCatalogueTemplateData data : exampleData) {
            Row row = sheet.createRow(rowIndex++);
            fillTemplateRow(row, data, templateStyle);
        }
    }

    private void fillTemplateRow(Row row, AccountCatalogueTemplateData data, CellStyle style) {
        int colIndex = 0;
        createTemplateCell(row, colIndex++, data.getCode(), style);
        createTemplateCell(row, colIndex++, data.getName(), style);
        createTemplateCell(row, colIndex++, data.getNature() != null ? data.getNature().getState() : "Seleccionar...", style);
        createTemplateCell(row, colIndex++, data.getFinancialStatus() != null ? data.getFinancialStatus().getState() : "Seleccionar...", style);
        createTemplateCell(row, colIndex++, data.getClassification() != null ? data.getClassification().getState() : "Seleccionar...", style);
        createTemplateCell(row, colIndex++, data.getCruce() != null ? (data.getCruce() ? "SI" : "NO") : "", style);
        createTemplateCell(row, colIndex++, data.getCentroCosto() != null ? (data.getCentroCosto() ? "SI" : "NO") : "", style);
    }

    private void createTemplateCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
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
        row.createCell(colIndex++).setCellValue(data.getCode());
        row.createCell(colIndex++).setCellValue(data.getName());
        row.createCell(colIndex++).setCellValue(data.getNature().getState());
        row.createCell(colIndex++).setCellValue(data.getFinancialStatus().getState());
        row.createCell(colIndex++).setCellValue(data.getClassification().getState());
        row.createCell(colIndex++).setCellValue(data.getCruce() != null ? (data.getCruce() ? "SI" : "NO") : "");
        row.createCell(colIndex++).setCellValue(data.getCentroCosto() != null ? (data.getCentroCosto() ? "SI" : "NO") : "");

        // Aplicar estilo a todas las celdas
        for (int i = 0; i < colIndex; i++) {
            row.getCell(i).setCellStyle(style);
        }
    }

    private void applyValidationsToTemplate(Sheet sheet) {
        int startRow = 1; // Después del encabezado
        int endRow = 1000; // Permitir muchas filas para la plantilla

        excelValidationService.applyAccountCatalogueValidations(sheet, startRow, endRow);
    }

    private void applyValidationsToDataSheet(Sheet sheet, int dataRowCount) {
        int startRow = 1; // Después del encabezado
        int endRow = Math.max(dataRowCount + 100, 1000); // Datos existentes + filas adicionales

        excelValidationService.applyAccountCatalogueValidations(sheet, startRow, endRow);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
            // Asegurar un ancho mínimo
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
            // Limitar ancho máximo
            if (sheet.getColumnWidth(i) > 8000) {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }


    private List<AccountCatalogueTemplateData> getHardcodedTemplateData() {
        return List.of(
            AccountCatalogueTemplateData.builder()
                .code("1")
                .name("Activos")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("11")
                .name("Efectivo y equivalentes de efectivo")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.CURRENTASSETS)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1105")
                .name("Caja")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.CURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("110501")
                .name("Caja principal")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("11050101")
                .name("Banco menor")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGEXPENSES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("11050102")
                .name("Banco")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("110502")
                .name("Caja menor")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1106")
                .name("Reservas internacionales")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGEXPENSES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("12")
                .name("Inversiones e instrumentos derivados")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1205")
                .name("Propiedades, planta y equipo")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("1206")
                .name("Activos intangibles")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGEXPENSES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2")
                .name("Pasivos")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("21")
                .name("Operaciones de banca central e instituciones financieras")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2105")
                .name("Cuentas por pagar")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTASSETS)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2106")
                .name("Obligaciones financieras corrientes")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("22")
                .name("Emisión y colocación de títulos de deuda")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2205")
                .name("Obligaciones financieras no corrientes")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("2206")
                .name("Beneficios a empleados a largo plazo")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("3")
                .name("Patrimonio")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("31")
                .name("Capital social")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("32")
                .name("Utilidades acumuladas")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("33")
                .name("Utilidades NO acumuladas")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("4")
                .name("Ingresos")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("5")
                .name("Gastos")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.NONCURRENTLIABILITIES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("6")
                .name("Costos de ventas")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("7")
                .name("Costos de transformación")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("8")
                .name("Cuentas de orden deudoras")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build(),
            AccountCatalogueTemplateData.builder()
                .code("9")
                .name("Cuentas de orden acreedoras")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.STATEMENTFINANCIALPOSITION)
                .classification(ClassificationEnum.OPERATINGREVENUES)
                .build()
        );
    }
}