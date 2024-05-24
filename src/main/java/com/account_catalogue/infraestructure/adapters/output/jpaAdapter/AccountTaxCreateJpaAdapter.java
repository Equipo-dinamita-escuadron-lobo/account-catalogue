package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountTaxCreateOutputPort;
import com.account_catalogue.domain.models.AccountTax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountTaxEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountTaxCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountTaxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AccountTaxCreateJpaAdapter implements IAccountTaxCreateOutputPort {
    private final IAccountTaxCreateMapper accountTaxCreateMapper;
    private final IAccountTaxRepository accountTaxRepository;


    @Override
    public AccountTax createAccounTax(AccountTax accountTax) {

        AccountTaxEntity accountTaxEntity=accountTaxCreateMapper.toEntity(accountTax);
        accountTaxEntity=accountTaxRepository.save(accountTaxEntity);
        return accountTaxCreateMapper.toModel(accountTaxEntity);
    }
}
