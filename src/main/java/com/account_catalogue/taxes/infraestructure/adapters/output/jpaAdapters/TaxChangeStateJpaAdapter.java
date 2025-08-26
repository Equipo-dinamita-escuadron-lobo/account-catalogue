package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import org.springframework.stereotype.Component;

import com.account_catalogue.taxes.application.output.ITaxChangeStateOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxUpdateMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TaxChangeStateJpaAdapter implements ITaxChangeStateOutputPort {

    private final ITaxRepository taxRepository;
    private final ITaxUpdateMapper taxUpdateMapper;

    /**
     * Cambia el estado de un impuesto en la base de datos.
     * 
     * @param id el ID del impuesto
     * @param status el nuevo estado
     * @return el impuesto actualizado
     */
    @Override
    public Tax changeState(Long id, Boolean status) {
        TaxEntity taxEntity = taxRepository.findByIdActive(id);
        
        if (taxEntity == null) {
            return null;
        }

        taxEntity.setStatus(status);
        taxEntity = taxRepository.save(taxEntity);
        
        return taxUpdateMapper.toModel(taxEntity);
    }
}
