package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.commons.exceptions.catalogue.ExcelValidationException;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountCatalogueExcelValidationService {

    // ========== MÉTODOS PARA GENERACIÓN DE EXCEL ==========

    /**
     * Obtiene todas las opciones de naturaleza.
     */
    public List<String> getNatureOptions() {
        return List.of(
            NatureEnum.DEBIT.getState(),
            NatureEnum.CREDIT.getState()
        );
    }

    /**
     * Obtiene todas las opciones de estado financiero.
     */
    public List<String> getFinancialStatusOptions() {
        return List.of(
            FinancialStatusEnum.STATEMENTFINANCIALPOSITION.getState(),
            FinancialStatusEnum.INCOMESTATEMENT.getState()
        );
    }

    /**
     * Obtiene todas las opciones de clasificación.
     */
    public List<String> getClassificationOptions() {
        return List.of(
            ClassificationEnum.CURRENTASSETS.getState(),
            ClassificationEnum.NONCURRENTASSETS.getState(),
            ClassificationEnum.CURRENTLIABILITIES.getState(),
            ClassificationEnum.NONCURRENTLIABILITIES.getState(),
            ClassificationEnum.EQUITY.getState(),
            ClassificationEnum.OPERATINGREVENUES.getState(),
            ClassificationEnum.NONOPERATINGINCOME.getState(),
            ClassificationEnum.OPERATINGEXPENSES.getState()
        );
    }

    // ========== MÉTODOS DE APLICACIÓN DE VALIDACIONES ==========

    /**
     * Aplica todas las validaciones de datos a una hoja de Excel para catálogo de cuentas.
     *
     * @param sheet hoja de Excel
     * @param startRow fila inicial
     * @param endRow fila final
     */
    public void applyAccountCatalogueValidations(Sheet sheet, int startRow, int endRow) throws ExcelValidationException {
        // Columna 0: Código (validación numérica personalizada)
        applyCodeValidation(sheet, 0, startRow, endRow);

        // Columna 2: Naturaleza
        applyDropdownValidation(sheet, 2, startRow, endRow, getNatureOptions(),
                "Seleccione Debito o Credito");

        // Columna 3: Estado Financiero
        applyDropdownValidation(sheet, 3, startRow, endRow, getFinancialStatusOptions(),
                "Seleccione Estado de Situacion Financiero o Estado de Resultados");

        // Columna 4: Clasificación
        applyDropdownValidation(sheet, 4, startRow, endRow, getClassificationOptions(),
                "Seleccione una clasificación válida");

        // Columna 5: Cruce (checkbox condicional)
        applyCruceValidation(sheet, 5, startRow, endRow);

        // Columna 6: Centro de Costo (checkbox condicional)
        applyCentroCostoValidation(sheet, 6, startRow, endRow);
    }

    /**
     * Aplica validación personalizada para el código de cuenta.
     * Debe ser positivo y tener exactamente 1, 2, 4, 6 u 8 dígitos.
     */
    public void applyCodeValidation(Sheet sheet, int columnIndex, int startRow, int endRow) throws ExcelValidationException {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);

            // Validación personalizada: número entero positivo con exactamente 1, 2, 4, 6 u 8 dígitos
            String formula = "AND(ISNUMBER(A" + (startRow + 1) + "), A" + (startRow + 1) + " >= 1, " +
                           "OR(LEN(TEXT(A" + (startRow + 1) + ", \"0\")) = 1, " +
                           "LEN(TEXT(A" + (startRow + 1) + ", \"0\")) = 2, " +
                           "LEN(TEXT(A" + (startRow + 1) + ", \"0\")) = 4, " +
                           "LEN(TEXT(A" + (startRow + 1) + ", \"0\")) = 6, " +
                           "LEN(TEXT(A" + (startRow + 1) + ", \"0\")) = 8))";

            DataValidationConstraint constraint = validationHelper.createCustomConstraint(formula);

            DataValidation validation = validationHelper.createValidation(constraint, addressList);

            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Error de Validación",
                    "El código debe ser un número positivo con exactamente 1, 2, 4, 6 u 8 dígitos");

            validation.setShowPromptBox(true);
            validation.createPromptBox("Código de Cuenta",
                    "Ingrese un código numérico positivo (1, 2, 4, 6 u 8 dígitos)");

            sheet.addValidationData(validation);

        } catch (Exception e) {
            throw new ExcelValidationException.ExcelCodeValidationException(columnIndex, e);
        }
    }

    /**
     * Aplica validación de lista desplegable a una columna específica.
     */
    public void applyDropdownValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            List<String> options, String errorMessage) throws ExcelValidationException {
        if (options == null || options.isEmpty()) {
            return;
        }

        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);

            String[] optionsArray = options.toArray(new String[0]);
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(optionsArray);

            DataValidation validation = validationHelper.createValidation(constraint, addressList);

            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Error de Validación", errorMessage);

            validation.setShowPromptBox(true);
            validation.createPromptBox("Selección", "Seleccione una opción de la lista");

            sheet.addValidationData(validation);

        } catch (Exception e) {
            throw new ExcelValidationException.ExcelDropdownValidationException(columnIndex, e);
        }
    }

    /**
     * Aplica validación condicional para Cruce.
     * Solo permitida cuando el código tiene exactamente 8 dígitos.
     * Usa lista desplegable SI/NO con tooltip personalizado.
     */
    public void applyCruceValidation(Sheet sheet, int columnIndex, int startRow, int endRow) throws ExcelValidationException {
        applyConditionalDropdownValidation(sheet, columnIndex, startRow, endRow,
                "LEN(TEXT(A{row}, \"0\")) = 8", 
                List.of("SI", "NO"),
                "Seleccione SI/NO. Valor permitido en cuenta auxiliar.",
                "¿Permitir asociar Cruce?");
    }

    /**
     * Aplica validación condicional para Centro de Costo.
     * Solo permitida cuando el código tiene 8 dígitos Y Estado Financiero es "Estado de Resultados".
     * Usa lista desplegable SI/NO con tooltip personalizado.
     */
    public void applyCentroCostoValidation(Sheet sheet, int columnIndex, int startRow, int endRow) throws ExcelValidationException {
        applyConditionalDropdownValidation(sheet, columnIndex, startRow, endRow,
                "AND(LEN(TEXT(A{row}, \"0\")) = 8, D{row} = \"" + FinancialStatusEnum.INCOMESTATEMENT.getState() + "\")",
                List.of("SI", "NO"),
                "Seleccione SI/NO. Valor permitido en cuenta auxiliar y Estado de Resultados.",
                "¿Permitir asociar Centro de Costo?");
    }

    /**
     * Aplica validación condicional de lista desplegable.
     * Muestra lista desplegable SI/NO solo cuando se cumple la condición.
     */
    private void applyConditionalDropdownValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            String conditionFormula, List<String> options, String errorMessage, String promptMessage) throws ExcelValidationException {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFWorkbook workbook = xssfSheet.getWorkbook();

            // Crear hoja oculta para validaciones si no existe
            XSSFSheet validationSheet = workbook.getSheet("Validations");
            if (validationSheet == null) {
                validationSheet = workbook.createSheet("Validations");
                workbook.setSheetHidden(workbook.getSheetIndex(validationSheet), true);

                // Agregar opciones SI/NO en la hoja oculta
                validationSheet.createRow(0).createCell(0).setCellValue("SI");
                validationSheet.createRow(1).createCell(0).setCellValue("NO");

                // Crear rango nombrado para las opciones
                Name optionsName = workbook.createName();
                optionsName.setNameName("ValidationOptions");
                optionsName.setRefersToFormula("Validations!$A$1:$A$2");
            }

            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            for (int row = startRow; row <= endRow; row++) {
                CellRangeAddressList addressList = new CellRangeAddressList(row, row, columnIndex, columnIndex);

                // Crear fórmula condicional para el rango de la lista
                String actualCondition = conditionFormula.replace("{row}", String.valueOf(row + 1));
                String formula = "IF(" + actualCondition + ", ValidationOptions, OFFSET(ValidationOptions,0,0,0,1))";

                DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);
                DataValidation validation = validationHelper.createValidation(constraint, addressList);

                validation.setShowErrorBox(true);
                validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                validation.createErrorBox("Error de Validación", errorMessage);

                validation.setShowPromptBox(true);
                validation.createPromptBox("Selección", promptMessage);

                sheet.addValidationData(validation);
            }

        } catch (Exception e) {
            throw new ExcelValidationException.ExcelConditionalValidationException(columnIndex, e);
        }
    }
}