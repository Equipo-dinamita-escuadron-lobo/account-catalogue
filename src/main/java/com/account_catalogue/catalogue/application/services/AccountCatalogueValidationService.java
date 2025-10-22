package com.account_catalogue.catalogue.application.services;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueAlreadyExistsException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueAssociatedWithTaxException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueDescriptionAlreadyExistsException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueNotFoundException;
import com.account_catalogue.commons.exceptions.catalogue.InvalidAccountCodeException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountCatalogueValidationService {
    
    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    
    /**
     * Valida el código de cuenta según las reglas de negocio.
     * El código debe tener exactamente 1, 2, 4, 6 u 8 dígitos.
     * 
     * @param code el código a validar
     * @throws InvalidAccountCodeException si el código no cumple con las reglas
     */
    public void validateAccountCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidAccountCodeException("El código de cuenta no puede estar vacío");
        }
        
        // Verificar que solo contenga dígitos
        if (!Pattern.matches("^\\d+$", code.trim())) {
            throw new InvalidAccountCodeException("El código de cuenta debe contener solo dígitos");
        }
        
        int length = code.trim().length();
        if (length != 1 && length != 2 && length != 4 && length != 6 && length != 8) {
            throw new InvalidAccountCodeException(
                "El código de cuenta debe tener exactamente 1, 2, 4, 6 u 8 dígitos. Longitud actual: " + length
            );
        }
    }
    
    /**
     * Valida que la cuenta no exista ya en el sistema para la misma empresa.
     * 
     * @param code el código de la cuenta
     * @param idEnterprise el ID de la empresa
     * @throws AccountCatalogueAlreadyExistsException si la cuenta ya existe
     */
    public void validateAccountDoesNotExist(String code, String idEnterprise) {
        AccountCatalogue existingAccount = accountCatalogueSearchOutputPort.getAccountCatalogueByCode(code, idEnterprise);
        if (existingAccount != null) {
            throw new AccountCatalogueAlreadyExistsException(
                "Ya existe una cuenta con el código '" + code + "' para la empresa '" + idEnterprise + "'"
            );
        }
    }
    
    /**
     * Valida que la cuenta no exista ya en el sistema para la misma empresa, excluyendo una cuenta específica.
     * Útil para validaciones de actualización.
     * 
     * @param code el código de la cuenta
     * @param idEnterprise el ID de la empresa
     * @param excludeId el ID de la cuenta a excluir de la validación
     * @throws AccountCatalogueAlreadyExistsException si la cuenta ya existe
     */
    public void validateAccountDoesNotExistExcluding(String code, String idEnterprise, Long excludeId) {
        if (code == null || code.trim().isEmpty()) {
            return; // La validación de código vacío se maneja en validateAccountCode
        }
        
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa no puede estar vacío para validación de duplicados");
        }
        
        if (excludeId == null) {
            throw new IllegalArgumentException("El ID a excluir no puede ser null para validación de duplicados");
        }
        
        AccountCatalogue existingAccount = accountCatalogueSearchOutputPort.getAccountCatalogueByCode(code.trim(), idEnterprise);
        if (existingAccount != null && !existingAccount.getId().equals(excludeId)) {
            throw new AccountCatalogueAlreadyExistsException(
                "Ya existe otra cuenta con el código '" + code.trim() + "' para la empresa '" + idEnterprise + 
                "'. Cuenta existente: ID=" + existingAccount.getId() + ", Código=" + existingAccount.getCode() + 
                ". Cuenta a actualizar: ID=" + excludeId
            );
        }
    }
    
    /**
     * Valida que la cuenta existe en el sistema.
     * 
     * @param code el código de la cuenta
     * @param idEnterprise el ID de la empresa
     * @return la cuenta encontrada
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    public AccountCatalogue validateAccountExists(String code, String idEnterprise) {
        AccountCatalogue account = accountCatalogueSearchOutputPort.getAccountCatalogueByCode(code, idEnterprise);
        if (account == null) {
            throw new AccountCatalogueNotFoundException(
                "No se encontró una cuenta con el código '" + code + "'"
            );
        }
        return account;
    }
    
    /**
     * Valida que la cuenta existe en el sistema por ID y empresa.
     * 
     * @param id el ID de la cuenta
     * @param idEnterprise el ID de la empresa
     * @return la cuenta encontrada
     * @throws AccountCatalogueNotFoundException si la cuenta no existe
     */
    public AccountCatalogue validateAccountExistsByIdAndEnterprise(Long id, String idEnterprise) {
        AccountCatalogue account = accountCatalogueSearchOutputPort.getAccountCatalogueByIdAndIdEnterprise(id, idEnterprise);
        if (account == null) {
            throw new AccountCatalogueNotFoundException(
                "No se encontró una cuenta con el ID '" + id + "'"
            );
        }
        return account;
    }
        
    /**
     * Valida que la cuenta no esté asociada a impuestos.
     * 
     * @param account la cuenta a validar
     * @throws AccountCatalogueAssociatedWithTaxException si la cuenta está asociada a impuestos
     */
    public void validateAccountNotAssociatedWithTaxes(AccountCatalogue account) {
        boolean hasSalesTaxes = account.getSalesTaxes() != null && !account.getSalesTaxes().isEmpty();
        boolean hasPurchaseTaxes = account.getPurchaseTaxes() != null && !account.getPurchaseTaxes().isEmpty();

        if (hasSalesTaxes || hasPurchaseTaxes) {
            int totalTaxes = 0;
            if (hasSalesTaxes) totalTaxes += account.getSalesTaxes().size();
            if (hasPurchaseTaxes) totalTaxes += account.getPurchaseTaxes().size();
            
            throw new AccountCatalogueAssociatedWithTaxException(
                "La cuenta '" + account.getCode() + "' está asociada a " + totalTaxes + 
                " impuesto(s). No se puede modificar o eliminar una cuenta asociada a impuestos."
            );
        }
    }
    
    /**
     * Valida recursivamente que ni la cuenta ni ninguna de sus cuentas hijas estén asociadas a impuestos.
     * Esta validación se usa antes de la eliminación para asegurar que toda la jerarquía puede ser eliminada.
     * 
     * @param account la cuenta padre a validar junto con todas sus cuentas hijas
     * @throws AccountCatalogueAssociatedWithTaxException si la cuenta o alguna de sus hijas está asociada a impuestos
     */
    public void validateAccountAndChildrenNotAssociatedWithTaxes(AccountCatalogue account) {
        // Validar la cuenta principal
        validateAccountNotAssociatedWithTaxes(account);
        
        // Validar recursivamente todas las cuentas hijas
        if (account.getChildren() != null && !account.getChildren().isEmpty()) {
            for (AccountCatalogue child : account.getChildren()) {
                validateAccountAndChildrenNotAssociatedWithTaxes(child);
            }
        }
    }
    
    /**
     * Valida el nombre/descripción de la cuenta.
     * Permite caracteres alfanuméricos y algunos especiales.
     * Normaliza la descripción eliminando espacios extra y validando formato.
     * 
     * @param description la descripción a validar
     * @throws InvalidAccountCodeException si la descripción no es válida
     */
    public void validateAccountDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidAccountCodeException("La descripción de la cuenta no puede estar vacía");
        }
        
        String trimmedDescription = description.trim();
        
        // Validar que no tenga múltiples espacios consecutivos
        if (trimmedDescription.contains("  ")) {
            throw new InvalidAccountCodeException(
                "La descripción de la cuenta no puede contener múltiples espacios consecutivos"
            );
        }
        
        // Permitir alfanuméricos, espacios, guiones, puntos, comas, paréntesis
        if (!Pattern.matches("^[a-zA-Z0-9\\s\\-.,()áéíóúÁÉÍÓÚñÑ]+$", trimmedDescription)) {
            throw new InvalidAccountCodeException(
                "La descripción de la cuenta contiene caracteres no válidos. " +
                "Solo se permiten letras, números, espacios y los siguientes caracteres especiales: - . , ( )"
            );
        }
        
        // Validar longitud mínima y máxima
        if (trimmedDescription.length() < 2) {
            throw new InvalidAccountCodeException("La descripción de la cuenta debe tener al menos 2 caracteres");
        }
        
        if (trimmedDescription.length() > 100) {
            throw new InvalidAccountCodeException("La descripción de la cuenta no puede exceder 100 caracteres");
        }
    }
    
    /**
     * Valida que no exista ya una cuenta con la misma descripción para la misma empresa (case-insensitive).
     * 
     * @param description la descripción de la cuenta
     * @param idEnterprise el ID de la empresa
     * @throws AccountCatalogueDescriptionAlreadyExistsException si ya existe una cuenta con esta descripción
     */
    public void validateAccountDescriptionDoesNotExist(String description, String idEnterprise) {
        if (description == null || description.trim().isEmpty()) {
            return; // La validación de descripción vacía se maneja en validateAccountDescription
        }
        
        AccountCatalogue existingAccount = accountCatalogueSearchOutputPort.getAccountCatalogueByDescriptionIgnoreCaseAndIdEnterprise(description.trim(), idEnterprise);
        if (existingAccount != null) {
            throw new AccountCatalogueDescriptionAlreadyExistsException(
                "Ya existe una cuenta con la descripción '" + description.trim() + "' para la empresa '" + idEnterprise + "'"
            );
        }
    }
    
    /**
     * Valida que no exista ya otra cuenta con la misma descripción para la misma empresa, excluyendo una cuenta específica (case-insensitive).
     * Útil para validaciones de actualización.
     * 
     * @param description la descripción de la cuenta
     * @param idEnterprise el ID de la empresa
     * @param excludeId el ID de la cuenta a excluir de la validación
     * @throws AccountCatalogueDescriptionAlreadyExistsException si ya existe otra cuenta con esta descripción
     */
    public void validateAccountDescriptionDoesNotExistExcluding(String description, String idEnterprise, Long excludeId) {
        if (description == null || description.trim().isEmpty()) {
            return; // La validación de descripción vacía se maneja en validateAccountDescription
        }
        
        if (idEnterprise == null || idEnterprise.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de empresa no puede estar vacío para validación de descripción duplicada");
        }
        
        if (excludeId == null) {
            throw new IllegalArgumentException("El ID a excluir no puede ser null para validación de descripción duplicada");
        }
        
        AccountCatalogue existingAccount = accountCatalogueSearchOutputPort.getAccountCatalogueByDescriptionIgnoreCaseAndIdEnterprise(description.trim(), idEnterprise);
        if (existingAccount != null && !existingAccount.getId().equals(excludeId)) {
            throw new AccountCatalogueDescriptionAlreadyExistsException(
                "Ya existe otra cuenta con la descripción '" + description.trim() + "' para la empresa '" + idEnterprise + 
                "'. Cuenta existente: ID=" + existingAccount.getId() + ", Descripción=" + existingAccount.getDescription() + 
                ". Cuenta a actualizar: ID=" + excludeId
            );
        }
    }
    
    /**
     * Valida que los campos crossing y costCenter solo puedan ser establecidos en cuentas auxiliares (8 dígitos).
     * Estos campos pueden ser nulos en cualquier tipo de cuenta.
     * 
     * @param accountCatalogue la cuenta a validar
     * @throws InvalidAccountCodeException si se intenta establecer crossing o costCenter en una cuenta que no es auxiliar
     */
    public void validateCrossingAndCostCenterOnlyForAuxiliaryAccounts(AccountCatalogue accountCatalogue) {
        boolean hasCrossing = accountCatalogue.getCrossing() != null && accountCatalogue.getCrossing();
        boolean hasCostCenter = accountCatalogue.getCostCenter() != null && accountCatalogue.getCostCenter();
        
        if (hasCrossing || hasCostCenter) {
            String code = accountCatalogue.getCode();
            if (code == null || code.trim().length() != 8) {
                String invalidFields = "";
                if (hasCrossing && hasCostCenter) {
                    invalidFields = "crossing y costCenter";
                } else if (hasCrossing) {
                    invalidFields = "crossing";
                } else {
                    invalidFields = "costCenter";
                }
                
                throw new InvalidAccountCodeException(
                    "Los campos " + invalidFields + " solo pueden ser establecidos en cuentas auxiliares (8 dígitos exactamente). " +
                    "Código actual: '" + code + "' tiene " + (code != null ? code.trim().length() : 0) + " dígitos."
                );
            }
        }
    }
    
    /**
     * Valida que el campo costCenter solo pueda ser true cuando el financialStatus sea Estado de Resultados.
     * Esta validación se aplica únicamente a cuentas auxiliares (8 dígitos).
     * 
     * @param accountCatalogue la cuenta a validar
     * @throws InvalidAccountCodeException si se intenta establecer costCenter como true sin el estado financiero correcto
     */
    public void validateCostCenterRequiresIncomeStatement(AccountCatalogue accountCatalogue) {
        boolean hasCostCenter = accountCatalogue.getCostCenter() != null && accountCatalogue.getCostCenter();
        
        if (hasCostCenter) {
            String code = accountCatalogue.getCode();
            // Solo aplicar esta validación a cuentas auxiliares (8 dígitos)
            if (code != null && code.trim().length() == 8) {
                FinancialStatusEnum financialStatus = accountCatalogue.getFinancialStatus();
                
                if (financialStatus == null || financialStatus != FinancialStatusEnum.INCOMESTATEMENT) {
                    throw new InvalidAccountCodeException(
                        "El centro de costo solo puede ser establecido cuando el estado financiero " +
                        "sea 'Estado de Resultados'. Estado financiero actual: " + 
                        (financialStatus != null ? financialStatus.getState() : "No definido")
                    );
                }
            }
        }
    }
}
