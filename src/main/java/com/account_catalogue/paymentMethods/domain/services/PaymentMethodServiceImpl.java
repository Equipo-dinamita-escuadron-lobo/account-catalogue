package com.account_catalogue.paymentMethods.domain.services;

import com.account_catalogue.commons.exceptions.paymentMethods.PaymentMethodsAlreadyExistsException;
import com.account_catalogue.commons.exceptions.paymentMethods.PaymentMethodsNotFoundException;
import com.account_catalogue.commons.exceptions.paymentMethods.InvalidAccountingAccountException;
import com.account_catalogue.commons.exceptions.paymentMethods.AccountingAccountImmutableException;
import com.account_catalogue.catalogue.application.services.AccountCatalogueValidationService;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import com.account_catalogue.paymentMethods.dataAccess.mapper.PaymentMethodDataMapper;
import com.account_catalogue.paymentMethods.dataAccess.repository.PaymentMethodRepository;
import com.account_catalogue.paymentMethods.domain.mapper.PaymentMethodDomainMapper;
import com.account_catalogue.paymentMethods.domain.model.PaymentMethod;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodCreateReq;
import com.account_catalogue.paymentMethods.presentation.DTO.request.PaymentMethodUpdateReq;
import com.account_catalogue.commons.utils.PaginationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements IPaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodDataMapper dataMapper;
    private final PaymentMethodDomainMapper domainMapper;
    private final AccountCatalogueValidationService accountCatalogueValidationService;
    private final PaginationHelper paginationHelper;


    @Transactional
    public PaymentMethod create(PaymentMethodCreateReq request) {
        // Estandarización de nombre
        String standardizedName = standardizeName(request.getName());

        // Validar que la cuenta contable existe por ID y empresa
        AccountCatalogue account = accountCatalogueValidationService.validateAccountExistsByIdAndEnterprise(
            request.getAccountingAccountId(), request.getIdEnterprise());

        // Validar que la cuenta contable es auxiliar
        validateAccountingAccount(account.getCode(), request.getIdEnterprise());

        // Validar unicidad por empresa (solo entre registros no eliminados)
        if (repository.existsByNameAndIdEnterprise(standardizedName, request.getIdEnterprise())) {
            throw new PaymentMethodsAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        PaymentMethod domain = domainMapper.toDomain(request);
        domain.setName(standardizedName);

        // Crear el dominio con la entidad de cuenta
        domain.setAccountingAccountEntity(account);

        // Crear la entidad con la relación usando la entidad completa
        PaymentMethodEntity toSave = dataMapper.toEntity(domain);
        // Crear la entidad AccountCatalogueEntity completa con todos los campos
        AccountCatalogueEntity accountEntity = new AccountCatalogueEntity();
        accountEntity.setId(account.getId());
        accountEntity.setCode(account.getCode());
        accountEntity.setDescription(account.getDescription());
        toSave.setAccountingAccount(accountEntity);

        PaymentMethodEntity saved = repository.save(toSave);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public PaymentMethod update(PaymentMethodUpdateReq request) {
        PaymentMethodEntity current = repository.findByIdAndIdEnterprise(request.getId(), request.getIdEnterprise())
                .orElseThrow(PaymentMethodsNotFoundException::new);

        String standardizedName = standardizeName(request.getName());

        // Validar que no se esté intentando cambiar la cuenta contable
        Long currentAccountId = current.getAccountingAccount() != null ? current.getAccountingAccount().getId() : null;
        if (currentAccountId != null && !request.getAccountingAccountId().equals(currentAccountId)) {
            throw new AccountingAccountImmutableException(
                "No se puede modificar la cuenta contable. ID actual: '" + currentAccountId +
                "', ID solicitado: '" + request.getAccountingAccountId() + "'"
            );
        }

        // Validar unicidad si el nombre cambió (solo entre registros no eliminados)
        if (!standardizedName.equals(current.getName()) &&
            repository.existsByNameAndIdEnterpriseAndIdNot(standardizedName, request.getIdEnterprise(), current.getId())) {
            throw new PaymentMethodsAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        current.setName(standardizedName);
        // No se modifica la cuenta contable - es inmutable después de la creación

        PaymentMethodEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public PaymentMethod findById(Long id, String idEnterprise) {
        return dataMapper.toDomain(repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(PaymentMethodsNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public Page<PaymentMethod> findAllByEnterprise(String idEnterprise, Optional<Integer> page, Optional<Integer> size, String sortField, String sortOrder) {
        long totalRecords = repository.countByIdEnterprise(idEnterprise);
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);

        Pageable pageable = paginationHelper.createFlexiblePageable(page, size, totalRecords);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return repository.findAllByIdEnterprise(idEnterprise, pageable).map(dataMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<PaymentMethod> findAllByEnterpriseAndStatus(String idEnterprise, Boolean status, Optional<Integer> page, Optional<Integer> size) {
        // Obtener el total de registros filtrados por empresa y estado
        long totalRecords = repository.countByIdEnterpriseAndStatus(idEnterprise, status);

        // Ordenamiento fijo: name ASC
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        // Crear el Pageable usando PaginationHelper
        Pageable pageable = paginationHelper.createFlexiblePageable(page, size, totalRecords);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return repository.findAllByIdEnterpriseAndStatus(idEnterprise, status, pageable)
                .map(dataMapper::toDomain);
    }

    @Transactional
    public PaymentMethod changeState(Long id, String idEnterprise, Boolean newState) {
        PaymentMethodEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(PaymentMethodsNotFoundException::new);

        current.setStatus(newState);
        PaymentMethodEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public PaymentMethod delete(Long id, String idEnterprise) {
        PaymentMethodEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(PaymentMethodsNotFoundException::new);

        PaymentMethod domain = dataMapper.toDomain(current);
        repository.delete(current);
        return domain;
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
