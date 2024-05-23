package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.ITaxCreateOutputPort;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.ITaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaxCreateJpaAdapter implements ITaxCreateOutputPort {
   @Autowired
    private ITaxCreateMapper taxCreateMapper;
    @Autowired
    private  ITaxRepository taxRepository;
    @Override
    public Tax createTax(Tax tax) {
       TaxEntity taxEntity=taxRepository.save(taxCreateMapper.toEntity(tax));
        return taxCreateMapper.toModel(taxEntity);
    }
}
