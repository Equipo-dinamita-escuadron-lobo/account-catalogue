package com.account_catalogue.catalogue.domain.utils;

/**
 * Constantes centralizadas para funcionalidades de importación de catálogo de cuentas.
 * Centraliza todos los valores constantes utilizados en el proceso de importación
 * para facilitar mantenimiento y reutilización.
 */
public final class ImportConstants {

    private ImportConstants() {
        throw new UnsupportedOperationException("ImportConstants es una clase de utilidad y no debe ser instanciada");
    }

    // ===== CONFIGURACIÓN DE ARCHIVOS =====
    
    /**
     * Extensiones de archivo soportadas para importación.
     */
    public static final String[] SUPPORTED_EXTENSIONS = {".xlsx", ".xls"};

    /**
     * Tamaño máximo de archivo en bytes (5 MB).
     */
    public static final long MAX_FILE_SIZE = 5242880L; // 5 * 1024 * 1024

    // ===== ENCABEZADOS DE EXCEL =====
    
    /**
     * Encabezados requeridos para importación de catálogo de cuentas.
     * Todos son obligatorios excepto los dos últimos (Cruce y Centro de Costo).
     */
    public static final String[] REQUIRED_HEADERS = {
        "Código",
        "Nombre",
        "Naturaleza",
        "Estado Financiero",
        "Clasificación"
    };
    
    /**
     * Encabezados opcionales para importación de catálogo de cuentas.
     * Estos campos pueden estar presentes o ausentes, y pueden estar vacíos.
     */
    public static final String[] OPTIONAL_HEADERS = {
        "Cruce",
        "Centro de Costo"
    };
    
    // ===== NOMBRES DE COLUMNAS =====
    
    public static final String CODE_COLUMN = "Código";
    public static final String NAME_COLUMN = "Nombre";
    public static final String NATURE_COLUMN = "Naturaleza";
    public static final String FINANCIAL_STATUS_COLUMN = "Estado Financiero";
    public static final String CLASSIFICATION_COLUMN = "Clasificación";
    public static final String CROSSING_COLUMN = "Cruce";
    public static final String COST_CENTER_COLUMN = "Centro de Costo";

    // ===== VALORES BOOLEANOS ACEPTADOS =====
    
    /**
     * Valores que representan TRUE en los campos booleanos.
     */
    public static final String[] BOOLEAN_TRUE_VALUES = {"SI", "SÍ", "S", "TRUE", "1"};
    
    /**
     * Valores que representan FALSE en los campos booleanos.
     */
    public static final String[] BOOLEAN_FALSE_VALUES = {"NO", "N", "FALSE", "0"};

    // ===== CÓDIGOS DE ERROR =====
    
    /**
     * Códigos de error estandarizados para importación.
     */
    public static final class ErrorCodes {
        public static final String REQUIRED_FIELD_MISSING = "REQUIRED_FIELD_MISSING";
        public static final String INVALID_CODE_FORMAT = "INVALID_CODE_FORMAT";
        public static final String INVALID_CODE_LENGTH = "INVALID_CODE_LENGTH";
        public static final String INVALID_ENUM_VALUE = "INVALID_ENUM_VALUE";
        public static final String INVALID_BOOLEAN_VALUE = "INVALID_BOOLEAN_VALUE";
        public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
        public static final String DUPLICATE_CODE = "DUPLICATE_CODE";
        public static final String DUPLICATE_DESCRIPTION = "DUPLICATE_DESCRIPTION";
        public static final String ORPHAN_ACCOUNT = "ORPHAN_ACCOUNT";
        public static final String PARENT_NOT_FOUND = "PARENT_NOT_FOUND";
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
        
        private ErrorCodes() {}
    }

    // ===== MENSAJES DE ERROR COMUNES =====
    
    /**
     * Mensajes de error estándar para importación.
     */
    public static final class ErrorMessages {
        public static final String SYSTEM_ERROR = "Error del sistema durante la importación";
        public static final String EMPTY_FILE = "El archivo no contiene datos para importar";
        public static final String INVALID_HEADERS = "El archivo no contiene los encabezados requeridos";
        
        private ErrorMessages() {}
    }

    // ===== VALORES POR DEFECTO =====
    
    /**
     * Valores por defecto para configuraciones.
     */
    public static final class Defaults {
        public static final int COLUMN_START_INDEX = 1;
        public static final int BATCH_SIZE = 500;
        public static final boolean SKIP_DUPLICATES = true;
        public static final boolean CONTINUE_ON_ERROR = false;
        
        private Defaults() {}
    }
}

