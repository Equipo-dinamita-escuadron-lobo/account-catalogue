package com.account_catalogue.catalogue.application.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IAccountCatalogueDeleteInputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.application.services.validation.AccountCatalogueValidationService;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.commons.audit.annotation.Auditable;
import com.account_catalogue.commons.audit.annotation.OperationType;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueHasChildrenException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueInUseException;
import com.account_catalogue.commons.exceptions.catalogue.AccountCatalogueNotFoundException;

/**
 * @brief Servicio para operaciones de eliminación de cuentas contables
 *
 *        Maneja la eliminación de cuentas del catálogo,
 *        con validaciones para asegurar integridad referencial.
 */
@Service
@AllArgsConstructor
public class AccountCatalogueDeleteService implements IAccountCatalogueDeleteInputPort {

    private final IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;
    private final IAccountCatalogueSearchOutputPort accountCatalogueSearchOutputPort;
    private final AccountCatalogueValidationService validationService;

    /**
     * @brief Elimina cuenta y jerarquía completa con validaciones
     * @param id           ID del catálogo de cuenta a eliminar
     * @param idEnterprise ID de la empresa
     */
    @Transactional
    @Override
    @Auditable(operationType = OperationType.DELETE, affectedTable = "ACCOUNT_CATALOGUE", moduleName = "ACCOUNTING", idArgIndex = 0, enterpriseIdArgIndex = 1)
    public void deleteById(Long id, String idEnterprise) {
        // Obtener el árbol completo de la cuenta con todas sus cuentas hijas
        AccountCatalogue accountTreeToDelete = accountCatalogueSearchOutputPort
                .getAccountCatalogueTreeByIdAndIdEnterprise(id, idEnterprise);

        // Validar que la cuenta existe
        if (accountTreeToDelete == null) {
            throw new AccountCatalogueNotFoundException(
                    "No se encontró una cuenta con el ID '" + id + "'");
        }

        // Validar que la cuenta no tenga movimientos contables registrados
        if (accountTreeToDelete.isInUse()) {
            throw new AccountCatalogueInUseException(accountTreeToDelete.getCode(), false); // false indica operación de
                                                                                            // eliminación
        }

        if (accountTreeToDelete.getChildren() != null && !accountTreeToDelete.getChildren().isEmpty()) {
            throw new AccountCatalogueHasChildrenException(
                    "No se puede eliminar la cuenta '" + accountTreeToDelete.getCode()
                            + "' porque tiene cuentas hijas asociadas.");
        }

        // Validar recursivamente que ni la cuenta padre ni ninguna de sus hijas estén
        // asociadas a impuestos
        validationService.validateAccountAndChildrenNotAssociatedWithTaxes(accountTreeToDelete);

        // Validar recursivamente que ni la cuenta padre ni ninguna de sus hijas estén
        // asociadas a cuentas bancarias
        validationService.validateAccountAndChildrenNotAssociatedWithBankAccounts(accountTreeToDelete);

        // Validar recursivamente que ni la cuenta padre ni ninguna de sus hijas estén
        // asociadas a métodos de pago
        validationService.validateAccountAndChildrenNotAssociatedWithPaymentMethods(accountTreeToDelete);

        // Proceder con la eliminación recursiva
        accountCatalogueDeleteOutputPort.deleteById(id);
    }
}
