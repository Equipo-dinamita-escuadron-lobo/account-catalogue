package com.account_catalogue.banks.domain.services;

import org.springframework.data.domain.Page;

import com.account_catalogue.banks.domain.model.Bank;
import com.account_catalogue.banks.presentation.DTO.request.BankCreateReq;
import com.account_catalogue.banks.presentation.DTO.request.BankUpdateReq;

public interface IBankService {

    Bank create(BankCreateReq request);

    Bank update(BankUpdateReq request);

    Bank findById(Long id, String idEnterprise);

    Page<Bank> findAllByEnterpriseWithFilters(String idEnterprise, Integer page, Integer size, String sortField, String sortOrder, String search);

    Page<Bank> findAllActiveByEnterprise(String idEnterprise, Integer page, Integer size);

    Bank changeState(Long id, String idEnterprise, Boolean newState);

    Bank delete(Long id, String idEnterprise);
}
