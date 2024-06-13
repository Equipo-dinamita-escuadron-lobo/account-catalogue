package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.ITaxSearchOutputPort;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.ITaxRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class TaxSearchJpaAdapter implements ITaxSearchOutputPort {
    private final ITaxRepository taxRepository;
    private final ITaxSearchMapper taxSearchMapper;

    @Override
    public Tax getTax(String code, String idEnterprise) {
        TaxEntity taxEntity=taxRepository.findByCode(code, idEnterprise);
        return taxSearchMapper.toDomain(taxEntity);
    }

    @Override
    public List<Tax> getTaxes( String idEnterprise) {
        List<TaxEntity> taxEntities=taxRepository.findAllByIdEnterprise( idEnterprise);
        return taxSearchMapper.toDomainList(taxEntities);

    }
}
