package com.account_catalogue.catalogue.domain.utils;

import java.text.Normalizer;

/**
 * Utilidad para normalizar cadenas de texto en el proceso de importación de catálogo de cuentas.
 * Proporciona métodos para limpiar y formatear textos de manera consistente.
 */
public final class StringNormalizer {

    private StringNormalizer() {
        throw new UnsupportedOperationException("StringNormalizer es una clase de utilidad y no debe ser instanciada");
    }

    /**
     * Normaliza un texto para comparaciones case-insensitive.
     * Elimina acentos, espacios extra y convierte a minúsculas para comparación.
     * Usado en: detección de duplicados por descripción.
     *
     * @param input el texto a normalizar para comparación
     * @return el texto normalizado para comparación, o null si el input es null
     */
    public static String normalizeForComparison(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String normalized = removeAccents(input.trim().toLowerCase());
        // Eliminar espacios múltiples
        return normalized.replaceAll("\\s+", " ");
    }

    /**
     * Normaliza una descripción de cuenta para almacenamiento.
     * Elimina espacios múltiples y aplica trim.
     * Usado en: conversión de datos para persistencia.
     *
     * @param input la descripción a normalizar
     * @return la descripción normalizada, o null si el input es null
     */
    public static String normalizeDescription(String input) {
        if (input == null) {
            return null;
        }
        
        // Trim y eliminar espacios múltiples
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Normaliza un código de cuenta para almacenamiento.
     * Aplica trim simple para códigos numéricos.
     * Usado en: conversión de datos para persistencia.
     *
     * @param input el código a normalizar
     * @return el código normalizado, o null si el input es null
     */
    public static String normalizeCode(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim();
    }

    /**
     * Elimina tildes y acentos de una cadena de texto.
     *
     * @param input el texto del cual eliminar acentos
     * @return el texto sin acentos
     */
    private static String removeAccents(String input) {
        if (input == null) {
            return null;
        }
        
        // Normaliza a forma NFD (descompone caracteres con acentos)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        
        // Elimina los caracteres diacríticos (tildes, acentos, etc.)
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Normaliza el nombre de un encabezado de Excel eliminando texto entre paréntesis y saltos de línea.
     * Útil para procesar encabezados con indicativos de requerimiento.
     * Usado en: parseo de archivos Excel para mapeo de columnas.
     * 
     * Ejemplo: "Código\n(Requerido)" -> "Código"
     * Ejemplo: "Centro de Costo\n(Opcional)" -> "Centro de Costo"
     *
     * @param headerName nombre del encabezado original
     * @return nombre normalizado sin indicativos de requerimiento ni saltos de línea
     */
    public static String normalizeHeaderName(String headerName) {
        if (headerName == null) {
            return null;
        }
        
        // Eliminar saltos de línea y caracteres de retorno de carro
        String normalized = headerName.replaceAll("[\n\r]+", " ");
        
        // Eliminar texto entre paréntesis (indicativos de requerimiento)
        normalized = normalized.replaceAll("\\s*\\([^)]*\\)\\s*", "");
        
        // Limpiar espacios múltiples y trim
        normalized = normalized.replaceAll("\\s+", " ").trim();
        
        return normalized;
    }
}
