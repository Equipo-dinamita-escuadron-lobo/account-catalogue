package com.account_catalogue.bankAccounts.domain.services;

import org.springframework.data.domain.Page;

import com.account_catalogue.bankAccounts.domain.model.BankAccount;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountCreateReq;
import com.account_catalogue.bankAccounts.presentation.DTO.request.BankAccountUpdateReq;

public interface IBankAccountService {

    BankAccount create(BankAccountCreateReq request);

    BankAccount update(BankAccountUpdateReq request);

    BankAccount findById(Long id, String idEnterprise);

    Page<BankAccount> findAllByEnterpriseWithFilters(String idEnterprise, Integer page, Integer size, String sortField, String sortOrder, String search);

    Page<BankAccount> findAllActiveByEnterprise(String idEnterprise, Integer page, Integer size);

    Page<BankAccount> findAllByEnterpriseAndBank(String idEnterprise, Long bankId, int page, int size);

    BankAccount changeState(Long id, String idEnterprise, Boolean newState);

    BankAccount delete(Long id, String idEnterprise);
}
