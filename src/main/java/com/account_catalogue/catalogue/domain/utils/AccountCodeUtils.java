package com.account_catalogue.catalogue.domain.utils;

import com.account_catalogue.catalogue.domain.models.AccountCatalogueExcelData;

import java.util.Comparator;
import java.util.List;

/**
 * Utilidades para manejo de códigos de cuenta contable y jerarquía.
 */
public final class AccountCodeUtils {

    private AccountCodeUtils() {
        throw new UnsupportedOperationException("AccountCodeUtils es una clase de utilidad y no debe ser instanciada");
    }

    /**
     * Extrae el código del padre a partir de un código de cuenta.
     * Ejemplos:
     * - 1105 → 110
     * - 110501 → 11050
     * - 11050101 → 1105010
     * - 11 → 1
     * - 1 → null (cuenta raíz)
     * 
     * @param code código de la cuenta
     * @return código del padre o null si es cuenta raíz
     */
    public static String extractParentCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        String trimmedCode = code.trim();
        int length = trimmedCode.length();
        
        // Si tiene solo 1 dígito, es raíz y no tiene padre
        if (length == 1) {
            return null;
        }
        
        // Determinar longitud del padre según la longitud actual
        int parentLength;
        if (length == 2) {
            parentLength = 1;
        } else if (length == 4) {
            parentLength = 2;
        } else if (length == 6) {
            parentLength = 4;
        } else if (length == 8) {
            parentLength = 6;
        } else {
            // Para códigos de longitud no estándar, retornar null
            return null;
        }
        
        return trimmedCode.substring(0, parentLength);
    }

    /**
     * Obtiene el nivel jerárquico basado en la longitud del código.
     * Nivel 1: 1 dígito (ej: 1, 2, 3)
     * Nivel 2: 2 dígitos (ej: 11, 12, 21)
     * Nivel 3: 4 dígitos (ej: 1105, 1205)
     * Nivel 4: 6 dígitos (ej: 110501, 120501)
     * Nivel 5: 8 dígitos (ej: 11050101, 12050101) - Cuentas auxiliares
     * 
     * @param code código de la cuenta
     * @return nivel jerárquico (1-5) o 0 si el código es inválido
     */
    public static int getHierarchyLevel(String code) {
        if (code == null || code.trim().isEmpty()) {
            return 0;
        }
        
        int length = code.trim().length();
        return switch (length) {
            case 1 -> 1;
            case 2 -> 2;
            case 4 -> 3;
            case 6 -> 4;
            case 8 -> 5;
            default -> 0;
        };
    }

    /**
     * Valida si un código tiene una longitud permitida.
     * Solo se permiten: 1, 2, 4, 6 u 8 dígitos.
     * 
     * @param code código a validar
     * @return true si la longitud es válida
     */
    public static boolean isValidCodeLength(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        int length = code.trim().length();
        return length == 1 || length == 2 || length == 4 || length == 6 || length == 8;
    }

    /**
     * Ordena una lista de cuentas por jerarquía (orden natural del código).
     * Esto garantiza que los padres siempre se procesen antes que los hijos.
     * Ejemplo: 1, 11, 110, 1105, 110501, 11050101, 12, 1205, ...
     * 
     * @param accounts lista de cuentas a ordenar
     * @return lista ordenada por jerarquía
     */
    public static List<AccountCatalogueExcelData> sortByHierarchy(List<AccountCatalogueExcelData> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return accounts;
        }
        
        // Ordenar por código numéricamente (como entero) para mantener orden jerárquico correcto
        accounts.sort(Comparator.comparingLong(account -> {
            try {
                return Long.parseLong(account.getCode().trim());
            } catch (NumberFormatException e) {
                // Si no es numérico, usar 0 para que vaya al inicio
                return 0L;
            }
        }));
        
        return accounts;
    }

    /**
     * Verifica si una cuenta es cuenta raíz (sin padre).
     * 
     * @param code código de la cuenta
     * @return true si es cuenta raíz (1 dígito)
     */
    public static boolean isRootAccount(String code) {
        return code != null && code.trim().length() == 1;
    }

    /**
     * Verifica si una cuenta es auxiliar (hoja del árbol, 8 dígitos).
     * 
     * @param code código de la cuenta
     * @return true si es cuenta auxiliar
     */
    public static boolean isAuxiliaryAccount(String code) {
        return code != null && code.trim().length() == 8;
    }
}

