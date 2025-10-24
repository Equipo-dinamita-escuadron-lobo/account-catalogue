package com.account_catalogue.banks.domain.services;

import com.account_catalogue.commons.exceptions.banks.BankAlreadyExistsException;
import com.account_catalogue.commons.exceptions.banks.BankHasAssociatedAccountsException;
import com.account_catalogue.commons.exceptions.banks.BankNotFoundException;
import com.account_catalogue.commons.exceptions.banks.InvalidBankCodeException;
import com.account_catalogue.commons.utils.PaginationHelper;
import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.banks.dataAccess.mapper.BankDataMapper;
import com.account_catalogue.banks.dataAccess.repository.BankRepository;
import com.account_catalogue.bankAccounts.dataAccess.repository.BankAccountRepository;
import com.account_catalogue.banks.domain.mapper.BankDomainMapper;
import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.banks.presentation.DTO.request.BankCreateReq;
import com.account_catalogue.banks.presentation.DTO.request.BankUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements IBankService {

    private final BankRepository repository;
    private final BankDataMapper dataMapper;
    private final BankDomainMapper domainMapper;
    private final BankAccountRepository bankAccountRepository;
    private final PaginationHelper paginationHelper;

    @Transactional
    public Bank create(BankCreateReq request) {
        // Validar formato del código
        validateBankCode(request.getCode());

        // Estandarización del nombre a mayúsculas
        String standardizedName = standardizeName(request.getName());

               // Validar unicidad del código por empresa
               if (repository.existsByCodeAndIdEnterprise(request.getCode(), request.getIdEnterprise())) {
                   throw new BankAlreadyExistsException("código", request.getCode(), request.getIdEnterprise());
               }

        // Validar unicidad del nombre por empresa
        if (repository.existsByNameAndIdEnterprise(standardizedName, request.getIdEnterprise())) {
            throw new BankAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        Bank domain = domainMapper.toDomain(request);
        domain.setName(standardizedName);
        BankEntity toSave = dataMapper.toEntity(domain);

        BankEntity saved = repository.save(toSave);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public Bank update(BankUpdateReq request) {
        BankEntity current = repository.findByIdAndIdEnterprise(request.getId(), request.getIdEnterprise())
                .orElseThrow(BankNotFoundException::new);

        // Validar formato del código
        validateBankCode(request.getCode());

        // Validar unicidad del código si cambió
        if (!current.getCode().equals(request.getCode()) &&
                repository.existsByCodeAndIdEnterprise(request.getCode(), request.getIdEnterprise())) {
            throw new BankAlreadyExistsException("código", request.getCode(), request.getIdEnterprise());
        }

        String standardizedName = standardizeName(request.getName());

        // Validar unicidad del nombre si cambió (solo entre registros no eliminados)
        if (!standardizedName.equals(current.getName()) &&
                repository.existsByNameAndIdEnterpriseAndIdNot(standardizedName, request.getIdEnterprise(),
                        current.getId())) {
            throw new BankAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        current.setCode(request.getCode());
        current.setName(standardizedName);
        current.setCurrency(request.getCurrency());
        current.setStatus(request.getStatus());

        BankEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public Bank findById(Long id, String idEnterprise) {
        return dataMapper.toDomain(repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public Page<Bank> findAllByEnterpriseWithFilters(String idEnterprise, Integer page, Integer size,
                                                   String sortField, String sortOrder, String search) {
        // Validar sortField - solo permitir "code" y "name"
        if (sortField != null && !sortField.isEmpty()) {
            if (!"code".equals(sortField) && !"name".equals(sortField)) {
                throw new IllegalArgumentException("El campo de ordenamiento debe ser 'code' o 'name'");
            }
        } else {
            // Por defecto ordenar por nombre
            sortField = "name";
        }

        // Validar sortOrder - solo permitir "asc" y "desc"
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortOrder != null && !sortOrder.isEmpty()) {
            if ("desc".equalsIgnoreCase(sortOrder)) {
                direction = Sort.Direction.DESC;
            } else if (!"asc".equalsIgnoreCase(sortOrder)) {
                throw new IllegalArgumentException("El orden debe ser 'asc' o 'desc'");
            }
        }

        // Determinar el número total de registros (considerando búsqueda si existe)
        long totalRecords;
        if (StringUtils.hasText(search)) {
            // Si hay búsqueda, contar registros que coincidan con la búsqueda
            totalRecords = repository.countByIdEnterpriseAndSearch(idEnterprise, search.trim());
        } else {
            // Sin búsqueda, contar todos los registros
            totalRecords = repository.countByIdEnterprise(idEnterprise);
        }

        // Crear paginación inteligente
        Pageable pageable = paginationHelper.createFlexiblePageable(
            Optional.ofNullable(page),
            Optional.ofNullable(size),
            totalRecords
        );

        // Aplicar ordenamiento
        Pageable pageableWithSort = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(direction, sortField)
        );

        // Ejecutar consulta con filtros
        Page<BankEntity> result;
        if (StringUtils.hasText(search)) {
            // Búsqueda por código o nombre
            result = repository.findByIdEnterpriseAndSearch(idEnterprise, search.trim(), pageableWithSort);
        } else {
            // Sin búsqueda
            result = repository.findAllByIdEnterprise(idEnterprise, pageableWithSort);
        }

        return result.map(dataMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<Bank> findAllActiveByEnterprise(String idEnterprise, Integer page, Integer size) {
        // Contar el total de bancos activos para paginación inteligente
        long totalRecords = repository.countByIdEnterpriseAndStatus(idEnterprise, true);

        Pageable pageable = paginationHelper.createFlexiblePageable(
                Optional.ofNullable(page),
                Optional.ofNullable(size),
                totalRecords);

        // Crear nuevo Pageable con ordenamiento por nombre
        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "name"));

        return repository.findAllByIdEnterpriseAndStatus(idEnterprise, true, pageableWithSort)
                .map(dataMapper::toDomain);
    }

    @Transactional
    public Bank changeState(Long id, String idEnterprise, Boolean newState) {
        BankEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankNotFoundException::new);

        current.setStatus(newState);
        BankEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public Bank delete(Long id, String idEnterprise) {
        BankEntity current = repository.findByIdAndIdEnterprise(id, idEnterprise)
                .orElseThrow(BankNotFoundException::new);

        // Verificar si el banco tiene cuentas bancarias asociadas
        boolean hasAssociatedAccounts = bankAccountRepository
                .findAllByIdEnterpriseAndBankId(idEnterprise, id, PageRequest.of(0, 1)).hasContent();
        if (hasAssociatedAccounts) {
            throw new BankHasAssociatedAccountsException(current.getName());
        }

        Bank domain = dataMapper.toDomain(current);
        repository.delete(current);
        return domain;
    }

    /**
     * Estandariza el nombre del banco convirtiéndolo a mayúsculas.
     */
    private String standardizeName(String input) {
        if (input == null)
            return null;
        return input.trim().toUpperCase(new Locale("es", "ES"));
    }

    /**
     * Valida que el código del banco tenga el formato correcto (exactamente 2
     * dígitos: 01-99).
     */
    private void validateBankCode(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new InvalidBankCodeException("El código del banco no puede estar vacío");
        }

        String trimmedCodigo = codigo.trim();

        if (!trimmedCodigo.matches("^\\d{2}$")) {
            throw new InvalidBankCodeException(
                    "El código debe ser exactamente 2 dígitos (01-99). Código proporcionado: '" + trimmedCodigo + "'");
        }

        int codigoInt = Integer.parseInt(trimmedCodigo);
        if (codigoInt < 1 || codigoInt > 99) {
            throw new InvalidBankCodeException(
                    "El código debe estar entre 01 y 99. Código proporcionado: '" + trimmedCodigo + "'");
        }
    }
}
