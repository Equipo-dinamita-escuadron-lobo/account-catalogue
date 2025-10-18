package com.account_catalogue.catalogue.application.services;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio centralizado para validaciones de Excel específicas del catálogo de cuentas.
 */
@Slf4j
@Service
public class AccountCatalogueExcelValidationService {

    // ========== MÉTODOS DE OBTENCIÓN DE DATOS ==========

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
    public void applyAccountCatalogueValidations(Sheet sheet, int startRow, int endRow) {
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
    public void applyCodeValidation(Sheet sheet, int columnIndex, int startRow, int endRow) {
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
            log.error("Error aplicando validación de código en columna {}", columnIndex, e);
        }
    }

    /**
     * Aplica validación de lista desplegable a una columna específica.
     */
    public void applyDropdownValidation(Sheet sheet, int columnIndex, int startRow, int endRow,
            List<String> options, String errorMessage) {
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
            log.error("Error aplicando validación en columna {}", columnIndex, e);
        }
    }

    /**
     * Aplica validación condicional para Cruce.
     * Solo permitida cuando el código tiene exactamente 8 dígitos.
     */
    public void applyCruceValidation(Sheet sheet, int columnIndex, int startRow, int endRow) {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            for (int row = startRow; row <= endRow; row++) {
                CellRangeAddressList addressList = new CellRangeAddressList(row, row, columnIndex, columnIndex);

                // Fórmula condicional: solo permite SI/NO si el código tiene 8 dígitos
                String formula = "OR(LEN(TEXT(A" + (row + 1) + ", \"0\")) <> 8, B" + (row + 1) + " = \"SI\", B" + (row + 1) + " = \"NO\", ISBLANK(B" + (row + 1) + "))";

                DataValidationConstraint constraint = validationHelper.createCustomConstraint(formula);
                DataValidation validation = validationHelper.createValidation(constraint, addressList);

                validation.setShowErrorBox(true);
                validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                validation.createErrorBox("Error de Validación",
                        "Cruce solo es válido para cuentas con código de 8 dígitos");

                validation.setShowPromptBox(true);
                validation.createPromptBox("Cruce",
                        "Solo disponible para códigos de 8 dígitos. Use SI o NO");

                sheet.addValidationData(validation);
            }

        } catch (Exception e) {
            log.error("Error aplicando validación de cruce en columna {}", columnIndex, e);
        }
    }

    /**
     * Aplica validación condicional para Centro de Costo.
     * Solo permitida cuando el código tiene 8 dígitos Y Estado Financiero es "Estado de Resultados".
     */
    public void applyCentroCostoValidation(Sheet sheet, int columnIndex, int startRow, int endRow) {
        try {
            XSSFSheet xssfSheet = (XSSFSheet) sheet;
            XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(xssfSheet);

            for (int row = startRow; row <= endRow; row++) {
                CellRangeAddressList addressList = new CellRangeAddressList(row, row, columnIndex, columnIndex);

                // Fórmula condicional compleja
                String formula = "OR(AND(LEN(TEXT(A" + (row + 1) + ", \"0\")) = 8, D" + (row + 1) + " = \"Estado de Resultados\"), " +
                               "OR(C" + (row + 1) + " = \"SI\", C" + (row + 1) + " = \"NO\", ISBLANK(C" + (row + 1) + ")))";

                DataValidationConstraint constraint = validationHelper.createCustomConstraint(formula);
                DataValidation validation = validationHelper.createValidation(constraint, addressList);

                validation.setShowErrorBox(true);
                validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                validation.createErrorBox("Error de Validación",
                        "Centro de Costo requiere código de 8 dígitos y Estado de Resultados");

                validation.setShowPromptBox(true);
                validation.createPromptBox("Centro de Costo",
                        "Requiere código de 8 dígitos y Estado Financiero = 'Estado de Resultados'");

                sheet.addValidationData(validation);
            }

        } catch (Exception e) {
            log.error("Error aplicando validación de centro de costo en columna {}", columnIndex, e);
        }
    }
}