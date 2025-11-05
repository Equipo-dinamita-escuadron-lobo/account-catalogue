package com.account_catalogue.bankAccounts.domain.services;

import org.springframework.data.domain.Page;

import com.account_catalogue.bankAccounts.domain.model.BankAccount;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountCreateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountUpdateReq;

/**
 * @brief Contrato para servicios de gestión de cuentas bancarias
 *
 * Define las operaciones principales para crear, actualizar, consultar y gestionar
 * cuentas bancarias con filtros por empresa, banco y estado.
 */
public interface IBankAccountService {

    /**
     * @brief Crea una nueva cuenta bancaria
     * @param request Datos de creación de la cuenta bancaria
     * @return Cuenta bancaria creada
     */
    BankAccount create(BankAccountCreateReq request);

    /**
     * @brief Actualiza una cuenta bancaria existente
     * @param request Datos de actualización de la cuenta bancaria
     * @return Cuenta bancaria actualizada
     */
    BankAccount update(BankAccountUpdateReq request);

    /**
     * @brief Busca una cuenta bancaria por ID y empresa
     * @param id ID de la cuenta bancaria
     * @param idEnterprise ID de la empresa
     * @return Cuenta bancaria encontrada
     */
    BankAccount findById(Long id, String idEnterprise);

    /**
     * @brief Consulta paginada con filtros avanzados
     * @param idEnterprise ID de la empresa
     * @param page Número de página (0-based)
     * @param size Tamaño de página
     * @param sortField Campo para ordenamiento
     * @param sortOrder Dirección del ordenamiento (ASC/DESC)
     * @param search Término de búsqueda por número de cuenta
     * @return Página de cuentas bancarias filtradas
     */
    Page<BankAccount> findAllByEnterpriseWithFilters(String idEnterprise, Integer page, Integer size, String sortField, String sortOrder, String search);

    /**
     * @brief Consulta cuentas activas por empresa
     * @param idEnterprise ID de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @return Página de cuentas bancarias activas
     */
    Page<BankAccount> findAllActiveByEnterprise(String idEnterprise, Integer page, Integer size);

    /**
     * @brief Consulta cuentas por empresa y banco específico
     * @param idEnterprise ID de la empresa
     * @param bankId ID del banco
     * @param page Número de página
     * @param size Tamaño de página
     * @return Página de cuentas bancarias del banco especificado
     */
    Page<BankAccount> findAllByEnterpriseAndBank(String idEnterprise, Long bankId, int page, int size);

    /**
     * @brief Cambia el estado activo/inactivo de una cuenta bancaria
     * @param id ID de la cuenta bancaria
     * @param idEnterprise ID de la empresa
     * @param newState Nuevo estado (true=activo, false=inactivo)
     * @return Cuenta bancaria con estado actualizado
     */
    BankAccount changeState(Long id, String idEnterprise, Boolean newState);

    /**
     * @brief Elimina una cuenta bancaria
     * @param id ID de la cuenta bancaria
     * @param idEnterprise ID de la empresa
     * @return Cuenta bancaria eliminada
     */
    BankAccount delete(Long id, String idEnterprise);
}
