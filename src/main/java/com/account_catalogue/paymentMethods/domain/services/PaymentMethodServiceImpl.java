package com.account_catalogue.paymentMethods.domain.services;

import com.account_catalogue.commons.exceptions.paymentMethods.PaymentMethodsAlreadyExistsException;
import com.account_catalogue.commons.exceptions.paymentMethods.PaymentMethodsNotFoundException;
import com.account_catalogue.commons.exceptions.paymentMethods.InvalidAccountingAccountException;
import com.account_catalogue.commons.exceptions.paymentMethods.AccountingAccountImmutableException;
import com.account_catalogue.catalogue.application.services.AccountCatalogueValidationService;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import com.account_catalogue.paymentMethods.dataAccess.mapper.PaymentMethodDataMapper;
import com.account_catalogue.paymentMethods.dataAccess.repository.PaymentMethodRepository;
import com.account_catalogue.paymentMethods.domain.mapper.PaymentMethodDomainMapper;
import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodCreateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements IPaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodDataMapper dataMapper;
    private final PaymentMethodDomainMapper domainMapper;
    private final AccountCatalogueValidationService accountCatalogueValidationService;

    @Transactional
    public PaymentMethod create(PaymentMethodCreateReq request) {
        // Estandarización de nombre
        String standardizedName = standardizeName(request.getName());

        // Validar que la cuenta contable existe y es auxiliar
        validateAccountingAccount(request.getAccountingAccount(), request.getIdEnterprise());

        // Validar unicidad por empresa (solo entre registros no eliminados)
        if (repository.existsByNameAndIdEnterpriseAndIsDeletedFalse(standardizedName, request.getIdEnterprise())) {
            throw new PaymentMethodsAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        PaymentMethod domain = domainMapper.toDomain(request);
        domain.setName(standardizedName);
        PaymentMethodEntity toSave = dataMapper.toEntity(domain);

        PaymentMethodEntity saved = repository.save(toSave);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public PaymentMethod update(PaymentMethodUpdateReq request) {
        PaymentMethodEntity current = repository.findByIdAndIdEnterpriseAndIsDeletedFalse(request.getId(), request.getIdEnterprise())
                .orElseThrow(PaymentMethodsNotFoundException::new);

        String standardizedName = standardizeName(request.getName());

        // Validar que no se esté intentando cambiar la cuenta contable
        if (!current.getAccountingAccount().equals(request.getAccountingAccount())) {
            throw new AccountingAccountImmutableException(
                "No se puede modificar la cuenta contable. Cuenta actual: '" + current.getAccountingAccount() + 
                "', cuenta solicitada: '" + request.getAccountingAccount() + "'"
            );
        }

        // Validar unicidad si el nombre cambió (solo entre registros no eliminados)
        if (!standardizedName.equals(current.getName()) && 
            repository.existsByNameAndIdEnterpriseAndIdNotAndIsDeletedFalse(standardizedName, request.getIdEnterprise(), current.getId())) {
            throw new PaymentMethodsAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        current.setName(standardizedName);
        // No se modifica la cuenta contable - es inmutable después de la creación

        PaymentMethodEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public PaymentMethod findById(Long id, String idEnterprise) {
        return dataMapper.toDomain(repository.findByIdAndIdEnterpriseAndIsDeletedFalse(id, idEnterprise)
                .orElseThrow(PaymentMethodsNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public Page<PaymentMethod> findAllByEnterprise(String idEnterprise, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findAllByIdEnterpriseAndIsDeletedFalse(idEnterprise, pageable).map(dataMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<PaymentMethod> findAllByEnterpriseAndStatus(String idEnterprise, Boolean status, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findAllByIdEnterpriseAndStatusAndIsDeletedFalse(idEnterprise, status, pageable)
                .map(dataMapper::toDomain);
    }

    @Transactional
    public PaymentMethod changeState(Long id, String idEnterprise, Boolean newState) {
        PaymentMethodEntity current = repository.findByIdAndIdEnterpriseAndIsDeletedFalse(id, idEnterprise)
                .orElseThrow(PaymentMethodsNotFoundException::new);

        current.setStatus(newState);
        PaymentMethodEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public PaymentMethod softDelete(Long id, String idEnterprise) {
        PaymentMethodEntity current = repository.findByIdAndIdEnterpriseAndIsDeletedFalse(id, idEnterprise)
                .orElseThrow(PaymentMethodsNotFoundException::new);

        current.setIsDeleted(true);
        PaymentMethodEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    private String standardizeName(String input) {
        if (input == null) return null;
        String s = input.trim().replaceAll("\\s+", " ").toLowerCase(new Locale("es", "ES"));
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(new Locale("es", "ES")) + s.substring(1);
    }

    /**
     * Valida que la cuenta contable existe y es una cuenta auxiliar (8 dígitos exactamente).
     * 
     * @param accountingAccount el código de la cuenta contable
     * @param idEnterprise el ID de la empresa
     * @throws InvalidAccountingAccountException si la cuenta no existe o no es auxiliar
     */
    private void validateAccountingAccount(String accountingAccount, String idEnterprise) {
        if (accountingAccount == null || accountingAccount.trim().isEmpty()) {
            throw new InvalidAccountingAccountException("La cuenta contable no puede estar vacía");
        }

        String trimmedAccount = accountingAccount.trim();

        // Validar que la cuenta tenga exactamente 8 dígitos (cuenta auxiliar)
        if (!trimmedAccount.matches("^\\d{8}$")) {
            throw new InvalidAccountingAccountException(
                "La cuenta contable debe ser una cuenta auxiliar de exactamente 8 dígitos. Cuenta proporcionada: '" + trimmedAccount + "'"
            );
        }

        // Validar que la cuenta existe en el catálogo
        try {
            AccountCatalogue account = accountCatalogueValidationService.validateAccountExists(trimmedAccount, idEnterprise);
            if (account == null) {
                throw new InvalidAccountingAccountException(
                    "La cuenta contable '" + trimmedAccount + "' no existe en el catálogo de cuentas para la empresa '" + idEnterprise + "'"
                );
            }
        } catch (Exception e) {
            if (e instanceof InvalidAccountingAccountException) {
                throw e;
            }
            throw new InvalidAccountingAccountException(
                "La cuenta contable '" + trimmedAccount + "' no existe en el catálogo de cuentas para la empresa '" + idEnterprise + "'"
            );
        }
    }
}
