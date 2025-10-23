package com.account_catalogue.bankAccounts.domain.services;

import com.account_catalogue.commons.exceptions.bankAccounts.BankAccountAlreadyExistsException;
import com.account_catalogue.commons.exceptions.bankAccounts.BankAccountNotFoundException;
import com.account_catalogue.commons.exceptions.bankAccounts.BankNotFoundForAccountException;
import com.account_catalogue.commons.exceptions.bankAccounts.InvalidAccountNumberException;
import com.account_catalogue.commons.exceptions.bankAccounts.InvalidAccountingAccountForBankAccountException;
import com.account_catalogue.catalogue.application.services.AccountCatalogueValidationService;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.banks.domain.services.IBankService;
import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.bankAccounts.dataAccess.entity.BankAccountEntity;
import com.account_catalogue.bankAccounts.dataAccess.mapper.BankAccountDataMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements IBankAccountService {

    private final BankAccountRepository repository;
    private final BankAccountDataMapper dataMapper;
    private final BankAccountDomainMapper domainMapper;
    private final IBankService bankService;
    private final AccountCatalogueValidationService accountCatalogueValidationService;

    @Transactional
    public BankAccount create(BankAccountCreateReq request) {
        validateAccountNumber(request.getAccountNumber());

        Bank bank = validateBankExists(request.getBankId(), request.getIdEnterprise());

        AccountCatalogue accountingAccount = validateAccountingAccountExists(request.getAccountingAccountId(), request.getIdEnterprise());

        if (repository.existsByAccountNumberAndIdEnterprise(request.getAccountNumber(), request.getIdEnterprise())) {
            throw new BankAccountAlreadyExistsException("número de cuenta", request.getAccountNumber().toString(), request.getIdEnterprise());
        }

        BankAccount domain = domainMapper.toDomain(request);
        domain.setBank(bank);
        domain.setAccountingAccount(accountingAccount);
        BankAccountEntity toSave = dataMapper.toEntity(domain);

        BankAccountEntity saved = repository.save(toSave);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public BankAccount update(BankAccountUpdateReq request) {
        BankAccountEntity current = repository.findByIdAndIdEnterprise(request.getId(), request.getIdEnterprise())
                .orElseThrow(BankAccountNotFoundException::new);

        // Validar que no se esté intentando cambiar el número de cuenta
        if (!current.getAccountNumber().equals(request.getAccountNumber())) {
            throw new InvalidAccountNumberException(
                "No se puede modificar el número de cuenta. Número actual: '" + current.getAccountNumber() + 
                "', número solicitado: '" + request.getAccountNumber() + "'"
            );
        }

        // Validar que no se esté intentando cambiar el banco
        if (!current.getBank().getId().equals(request.getBankId())) {
            throw new BankNotFoundForAccountException(
                "No se puede modificar el banco de la cuenta. Banco actual: '" + current.getBank().getId() + 
                "', banco solicitado: '" + request.getBankId() + "'"
            );
        }

        // Validar que la cuenta contable existe
        AccountCatalogue accountingAccount = validateAccountingAccountExists(request.getAccountingAccountId(), request.getIdEnterprise());

        current.setAccountType(request.getAccountType());
        // Convertir AccountCatalogue del dominio a AccountCatalogueEntity
        AccountCatalogueEntity accountingAccountEntity = new AccountCatalogueEntity();
        accountingAccountEntity.setId(accountingAccount.getId());
        current.setAccountingAccount(accountingAccountEntity);
        current.setStatus(request.getStatus());

        BankAccountEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public BankAccount findById(Long id, String idEnterprise) {
        return dataMapper.toDomain(repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankAccountNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public Page<BankAccount> findAllByEnterprise(String idEnterprise, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByIdEnterprise(idEnterprise, pageable).map(dataMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<BankAccount> findAllByEnterpriseAndStatus(String idEnterprise, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByIdEnterpriseAndStatus(idEnterprise, status, pageable)
                .map(dataMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<BankAccount> findAllByEnterpriseAndBank(String idEnterprise, Long bankId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByIdEnterpriseAndBankId(idEnterprise, bankId, pageable)
                .map(dataMapper::toDomain);
    }

    @Transactional
    public BankAccount changeState(Long id, String idEnterprise, Boolean newState) {
        BankAccountEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankAccountNotFoundException::new);

        current.setStatus(newState);
        BankAccountEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public BankAccount delete(Long id, String idEnterprise) {
        BankAccountEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankAccountNotFoundException::new);

        BankAccount domain = dataMapper.toDomain(current);
        repository.delete(current);
        return domain;
    }

    /**
     * Valida que el número de cuenta esté en el rango válido (8-16 dígitos).
     */
    private void validateAccountNumber(Long accountNumber) {
        if (accountNumber == null) {
            throw new InvalidAccountNumberException("El número de cuenta no puede estar vacío");
        }

        // Contar dígitos del número
        int digits = String.valueOf(accountNumber).length();
        
        if (digits < 8 || digits > 16) {
            throw new InvalidAccountNumberException(
                "El número de cuenta debe tener entre 8 y 16 dígitos. Número proporcionado: '" + accountNumber + "' (" + digits + " dígitos)"
            );
        }
    }

    /**
     * Valida que el banco existe y está activo.
     */
    private Bank validateBankExists(Long bankId, String idEnterprise) {
        try {
            return bankService.findById(bankId, idEnterprise);
        } catch (Exception e) {
            throw new BankNotFoundForAccountException(
                "El banco con ID '" + bankId + "' no existe o no está disponible para la empresa '" + idEnterprise + "'"
            );
        }
    }

    /**
     * Valida que la cuenta contable existe por ID.
     */
    private AccountCatalogue validateAccountingAccountExists(Long accountingAccountId, String idEnterprise) {
        if (accountingAccountId == null) {
            throw new InvalidAccountingAccountForBankAccountException("El ID de la cuenta contable no puede estar vacío");
        }

        // Validar que la cuenta existe en el catálogo
        try {
            AccountCatalogue account = accountCatalogueValidationService.validateAccountExistsByIdAndEnterprise(accountingAccountId, idEnterprise);
            if (account == null) {
                throw new InvalidAccountingAccountForBankAccountException(
                    "La cuenta contable con ID '" + accountingAccountId + "' no existe en el catálogo de cuentas para la empresa '" + idEnterprise + "'"
                );
            }
            return account;
        } catch (Exception e) {
            if (e instanceof InvalidAccountingAccountForBankAccountException) {
                throw e;
            }
            throw new InvalidAccountingAccountForBankAccountException(
                "La cuenta contable con ID '" + accountingAccountId + "' no existe en el catálogo de cuentas para la empresa '" + idEnterprise + "'"
            );
        }
    }
}
