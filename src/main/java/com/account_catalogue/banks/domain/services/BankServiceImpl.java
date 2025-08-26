package com.account_catalogue.banks.domain.services;

import com.account_catalogue.commons.exceptions.banks.BankAlreadyExistsException;
import com.account_catalogue.commons.exceptions.banks.BankNotFoundException;
import com.account_catalogue.commons.exceptions.banks.InvalidBankCodeException;
import com.account_catalogue.banks.dataAccess.entity.BankEntity;
import com.account_catalogue.banks.dataAccess.mapper.BankDataMapper;
import com.account_catalogue.banks.dataAccess.repository.BankRepository;
import com.account_catalogue.banks.domain.mapper.BankDomainMapper;
import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.banks.presentation.DTO.request.BankCreateReq;
import com.account_catalogue.banks.presentation.DTO.request.BankUpdateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements IBankService {

    private final BankRepository repository;
    private final BankDataMapper dataMapper;
    private final BankDomainMapper domainMapper;

    @Transactional
    public Bank create(BankCreateReq request) {
        // Validar formato del código
        validateBankCode(request.getCodigo());

        // Estandarización del nombre a mayúsculas
        String standardizedName = standardizeName(request.getNombre());

        // Validar unicidad del código por empresa
        if (repository.existsByCodigoAndIdEnterpriseAndIsDeletedFalse(request.getCodigo(), request.getIdEnterprise())) {
            throw new BankAlreadyExistsException("código", request.getCodigo().toString(), request.getIdEnterprise());
        }

        // Validar unicidad del nombre por empresa
        if (repository.existsByNombreAndIdEnterpriseAndIsDeletedFalse(standardizedName, request.getIdEnterprise())) {
            throw new BankAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        Bank domain = domainMapper.toDomain(request);
        domain.setNombre(standardizedName);
        BankEntity toSave = dataMapper.toEntity(domain);

        BankEntity saved = repository.save(toSave);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public Bank update(BankUpdateReq request) {
        BankEntity current = repository.findByIdAndIdEnterpriseAndIsDeletedFalse(request.getId(), request.getIdEnterprise())
                .orElseThrow(BankNotFoundException::new);

        // Validar que no se esté intentando cambiar el código
        if (!current.getCodigo().equals(request.getCodigo())) {
            throw new InvalidBankCodeException(
                "No se puede modificar el código del banco. Código actual: '" + current.getCodigo() + 
                "', código solicitado: '" + request.getCodigo() + "'"
            );
        }

        String standardizedName = standardizeName(request.getNombre());

        // Validar unicidad del nombre si cambió (solo entre registros no eliminados)
        if (!standardizedName.equals(current.getNombre()) && 
            repository.existsByNombreAndIdEnterpriseAndIdNotAndIsDeletedFalse(standardizedName, request.getIdEnterprise(), current.getId())) {
            throw new BankAlreadyExistsException("nombre", standardizedName, request.getIdEnterprise());
        }

        current.setNombre(standardizedName);
        current.setMoneda(request.getMoneda());
        current.setStatus(request.getStatus());

        BankEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    public Bank findById(Long id, String idEnterprise) {
        return dataMapper.toDomain(repository.findByIdAndIdEnterpriseAndIsDeletedFalse(id, idEnterprise)
                .orElseThrow(BankNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public Page<Bank> findAllByEnterprise(String idEnterprise, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByIdEnterpriseAndIsDeletedFalse(idEnterprise, pageable).map(dataMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<Bank> findAllByEnterpriseAndStatus(String idEnterprise, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByIdEnterpriseAndStatusAndIsDeletedFalse(idEnterprise, status, pageable)
                .map(dataMapper::toDomain);
    }

    @Transactional
    public Bank changeState(Long id, String idEnterprise, Boolean newState) {
        BankEntity current = repository.findByIdAndIdEnterpriseAndIsDeletedFalse(id, idEnterprise)
                .orElseThrow(BankNotFoundException::new);

        current.setStatus(newState);
        BankEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    @Transactional
    public Bank softDelete(Long id, String idEnterprise) {
        BankEntity current = repository.findByIdAndIdEnterpriseAndIsDeletedFalse(id, idEnterprise)
                .orElseThrow(BankNotFoundException::new);

        current.setIsDeleted(true);
        BankEntity saved = repository.save(current);
        return dataMapper.toDomain(saved);
    }

    /**
     * Estandariza el nombre del banco convirtiéndolo a mayúsculas.
     */
    private String standardizeName(String input) {
        if (input == null) return null;
        return input.trim().toUpperCase(new Locale("es", "ES"));
    }

    /**
     * Valida que el código del banco tenga el formato correcto (exactamente 2 dígitos: 01-99).
     */
    private void validateBankCode(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new InvalidBankCodeException("El código del banco no puede estar vacío");
        }

        String trimmedCodigo = codigo.trim();
        
        if (!trimmedCodigo.matches("^\\d{2}$")) {
            throw new InvalidBankCodeException(
                "El código debe ser exactamente 2 dígitos (01-99). Código proporcionado: '" + trimmedCodigo + "'"
            );
        }

        int codigoInt = Integer.parseInt(trimmedCodigo);
        if (codigoInt < 1 || codigoInt > 99) {
            throw new InvalidBankCodeException(
                "El código debe estar entre 01 y 99. Código proporcionado: '" + trimmedCodigo + "'"
            );
        }
    }
}
