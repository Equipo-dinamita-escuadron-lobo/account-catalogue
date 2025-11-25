package com.account_catalogue.bankAccounts.domain.services;

import com.account_catalogue.commons.exceptions.bankAccounts.BankAccountAlreadyExistsException;
import com.account_catalogue.commons.exceptions.bankAccounts.BankAccountInUseException;
import com.account_catalogue.commons.exceptions.bankAccounts.BankAccountNotFoundException;
import com.account_catalogue.commons.exceptions.bankAccounts.BankNotFoundForAccountException;
import com.account_catalogue.commons.exceptions.bankAccounts.InvalidAccountNumberException;
import com.account_catalogue.commons.exceptions.bankAccounts.InvalidAccountingAccountForBankAccountException;
import com.account_catalogue.commons.utils.PaginationHelper;
import com.account_catalogue.catalogue.application.services.validation.AccountCatalogueValidationService;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.banks.domain.services.IBankService;
import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import com.account_catalogue.bankAccounts.dataAccess.mapper.BankAccountDataMapper;
import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.bankAccounts.dataAccess.repository.BankAccountRepository;
import com.account_catalogue.bankAccounts.domain.mapper.BankAccountDomainMapper;
import com.account_catalogue.bankAccounts.domain.model.BankAccount;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountCreateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @brief Implementación de servicios para gestión de cuentas bancarias
 *
 * Proporciona operaciones CRUD completas con validaciones de negocio,
 * filtros avanzados y manejo de relaciones con bancos y cuentas contables.
 */
@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements IBankAccountService {

    private final BankAccountRepository repository;
    private final BankAccountDataMapper dataMapper;
    private final BankAccountDomainMapper domainMapper;
    private final IBankService bankService;
    private final AccountCatalogueValidationService accountCatalogueValidationService;
    private final PaginationHelper paginationHelper;

    /**
     * @brief Crea una nueva cuenta bancaria con validaciones completas
     * @param request Datos de creación de la cuenta bancaria
     * @return Cuenta bancaria creada con ID generado
     */
    @Override
    @Transactional
    public BankAccount create(BankAccountCreateReq request) {
        validateAccountNumber(request.getAccountNumber());

        Bank bank = validateBankExists(request.getBankId(), request.getIdEnterprise());

        AccountCatalogue accountingAccount = validateAccountingAccountExists(request.getAccountingAccountId(),
                request.getIdEnterprise());

        if (repository.existsByAccountNumberAndIdEnterprise(request.getAccountNumber(), request.getIdEnterprise())) {
            throw new BankAccountAlreadyExistsException("número de cuenta", request.getAccountNumber().toString(),
                    request.getIdEnterprise());
        }

        BankAccount domain = domainMapper.toDomain(request);
        domain.setBank(bank);
        domain.setAccountingAccountId(accountingAccount.getId());
        BankAccountEntity toSave = dataMapper.toEntity(domain);

        // Configurar la entidad de cuenta contable
        AccountCatalogueEntity accountingAccountEntity = new AccountCatalogueEntity();
        accountingAccountEntity.setId(accountingAccount.getId());
        toSave.setAccountingAccount(accountingAccountEntity);

        BankAccountEntity saved = repository.save(toSave);
        return dataMapper.toDomain(saved);
    }

    /**
     * @brief Actualiza una cuenta bancaria existente con validaciones
     * @param request Datos de actualización de la cuenta bancaria
     * @return Cuenta bancaria actualizada
     */
    @Override
    @Transactional
    public BankAccount update(BankAccountUpdateReq request) {
        BankAccountEntity current = repository.findByIdAndIdEnterprise(request.getId(), request.getIdEnterprise())
                .orElseThrow(BankAccountNotFoundException::new);

        // Validar que la cuenta bancaria no tenga movimientos contables registrados
        BankAccount domain = dataMapper.toDomain(current);
        if (domain.isInUse()) {
            throw new BankAccountInUseException(current.getAccountNumber().toString(), true); // true indica operación de edición
        }

        validateAccountNumber(request.getAccountNumber());

        if (!current.getAccountNumber().equals(request.getAccountNumber())) {
            if (repository.existsByAccountNumberAndIdEnterprise(request.getAccountNumber(), request.getIdEnterprise())) {
                throw new BankAccountAlreadyExistsException("número de cuenta", request.getAccountNumber().toString(),
                        request.getIdEnterprise());
            }
        }

        validateBankExists(request.getBankId(), request.getIdEnterprise());
        validateAccountingAccountExists(request.getAccountingAccountId(), request.getIdEnterprise());

        current.setAccountNumber(request.getAccountNumber());

        // Solo actualizar el banco si cambió
        if (!current.getBank().getId().equals(request.getBankId())) {
            BankEntity bankEntity = new BankEntity();
            bankEntity.setId(request.getBankId());
            current.setBank(bankEntity);
        }
        current.setAccountType(request.getAccountType());

        AccountCatalogueEntity accountingAccountEntity = new AccountCatalogueEntity();
        accountingAccountEntity.setId(request.getAccountingAccountId());
        current.setAccountingAccount(accountingAccountEntity);
        current.setStatus(request.getStatus());

        BankAccountEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccount findById(Long id, String idEnterprise) {
        return dataMapper.toDomain(repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankAccountNotFoundException::new));
    }

    /**
     * @brief Consulta paginada con filtros avanzados y búsqueda por número de cuenta
     * @param idEnterprise ID de la empresa
     * @param page Número de página (0-based)
     * @param size Tamaño de página
     * @param sortField Campo para ordenamiento (solo 'accountNumber')
     * @param sortOrder Dirección del ordenamiento (asc/desc)
     * @param search Término de búsqueda parcial por número de cuenta
     * @return Página de cuentas bancarias filtradas
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BankAccount> findAllByEnterpriseWithFilters(String idEnterprise, Integer page, Integer size,
                                                          String sortField, String sortOrder, String search) {
        if (sortField != null && !sortField.isEmpty()) {
            if (!"accountNumber".equals(sortField)) {
                throw new IllegalArgumentException("El campo de ordenamiento debe ser 'accountNumber'");
            }
        } else {
            sortField = "accountNumber";
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortOrder != null && !sortOrder.isEmpty()) {
            if ("desc".equalsIgnoreCase(sortOrder)) {
                direction = Sort.Direction.DESC;
            } else if (!"asc".equalsIgnoreCase(sortOrder)) {
                throw new IllegalArgumentException("El orden debe ser 'asc' o 'desc'");
            }
        }

        long totalRecords;
        if (StringUtils.hasText(search)) {
            totalRecords = repository.countByIdEnterpriseAndAccountNumberSearch(idEnterprise, search.trim());
        } else {
            totalRecords = repository.countByIdEnterprise(idEnterprise);
        }

        // Crear paginación inteligente
        Pageable pageable = paginationHelper.createFlexiblePageable(
            Optional.ofNullable(page),
            Optional.ofNullable(size),
            totalRecords
        );

        Pageable pageableWithSort = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(direction, sortField)
        );

        Page<BankAccountEntity> result;
        if (StringUtils.hasText(search)) {
            result = repository.findByIdEnterpriseAndAccountNumberSearch(idEnterprise, search.trim(), pageableWithSort);
        } else {
            result = repository.findAllByIdEnterprise(idEnterprise, pageableWithSort);
        }

        return result.map(dataMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccount> findAllActiveByEnterprise(String idEnterprise, Integer page, Integer size) {
        
        long totalRecords = repository.countByIdEnterpriseAndStatus(idEnterprise, true);

        Pageable pageable = paginationHelper.createFlexiblePageable(
                Optional.ofNullable(page),
                Optional.ofNullable(size),
                totalRecords
        );

        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "accountNumber")
        );

        return repository.findAllByIdEnterpriseAndStatus(idEnterprise, true, pageableWithSort)
                .map(dataMapper::toDomain);
    }

    @Override
    @Transactional
    public BankAccount changeState(Long id, String idEnterprise, Boolean newState) {
        BankAccountEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankAccountNotFoundException::new);

        current.setStatus(newState);
        BankAccountEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public BankAccount delete(Long id, String idEnterprise) {
        BankAccountEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankAccountNotFoundException::new);

        // Validar que la cuenta bancaria no tenga movimientos contables registrados
        BankAccount domain = dataMapper.toDomain(current);
        if (domain.isInUse()) {
            throw new BankAccountInUseException(current.getAccountNumber().toString(), false); // false indica operación de eliminación
        }

        repository.delete(current);
        return domain;
    }

    /**
     * @brief Valida formato del número de cuenta bancaria
     * @param accountNumber Número de cuenta a validar
     */
    private void validateAccountNumber(Long accountNumber) {
        if (accountNumber == null) {
            throw new InvalidAccountNumberException("El número de cuenta no puede estar vacío");
        }

        // Contar dígitos del número
        int digits = String.valueOf(accountNumber).length();

        if (digits < 8 || digits > 16) {
            throw new InvalidAccountNumberException(
                    "El número de cuenta debe tener entre 8 y 16 dígitos. Número proporcionado: '" + accountNumber
                            + "' (" + digits + " dígitos)");
        }
    }

    /**
     * @brief Valida existencia y estado activo del banco
     * @param bankId ID del banco a validar
     * @param idEnterprise ID de la empresa
     * @return Entidad de banco si existe y está activo
     */
    private Bank validateBankExists(Long bankId, String idEnterprise) {
        try {
            Bank bank = bankService.findById(bankId, idEnterprise);
            // Verificar que el banco esté activo
            if (!Boolean.TRUE.equals(bank.getStatus())) {
                throw new BankNotFoundForAccountException(
                        "El banco con ID '" + bankId + "' existe pero no está activo.");
            }
            return bank;
        } catch (BankNotFoundForAccountException e) {
            throw e;
        } catch (Exception e) {
            throw new BankNotFoundForAccountException(
                    "El banco con ID '" + bankId + "' no existe o no está disponible.");
        }
    }

    /**
     * @brief Valida existencia y formato de cuenta contable auxiliar
     * @param accountingAccountId ID de la cuenta contable a validar
     * @param idEnterprise ID de la empresa
     * @return Entidad de cuenta contable si existe y es auxiliar
     */
    private AccountCatalogue validateAccountingAccountExists(Long accountingAccountId, String idEnterprise) {
        if (accountingAccountId == null) {
            throw new InvalidAccountingAccountForBankAccountException(
                    "El ID de la cuenta contable no puede estar vacío");
        }

        // Validar que la cuenta existe en el catálogo
        try {
            AccountCatalogue account = accountCatalogueValidationService
                    .validateAccountExistsByIdAndEnterprise(accountingAccountId, idEnterprise);
            if (account == null) {
                throw new InvalidAccountingAccountForBankAccountException(
                        "La cuenta contable con ID '" + accountingAccountId + "' no existe en el catálogo de cuentas.");
            }

            String accountCode = account.getCode();
            if (accountCode == null || accountCode.trim().isEmpty()) {
                throw new InvalidAccountingAccountForBankAccountException(
                        "La cuenta contable tiene un código vacío o nulo.");
            }

            String trimmedCode = accountCode.trim();
            if (!trimmedCode.matches("^\\d{8}$")) {
                throw new InvalidAccountingAccountForBankAccountException(
                        "La cuenta contable debe ser una cuenta auxiliar con exactamente 8 dígitos.");
            }

            return account;
        } catch (InvalidAccountingAccountForBankAccountException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidAccountingAccountForBankAccountException(
                    "La cuenta contable con ID '" + accountingAccountId + "' no existe en el catálogo de cuentas.");
        }
    }

    @Override
    public void updateUsageCount(Long id, String enterpriseId, Integer usageCount) {
        BankAccountEntity entity = repository.findByIdAndIdEnterprise(id, enterpriseId)
                .orElseThrow(() -> new BankAccountNotFoundException("Cuenta bancaria no encontrada con ID: " + id));

        entity.setUsageCount(usageCount);
        repository.save(entity);
    }
}
