package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxSearchMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class TaxSearchJpaAdapter implements ITaxSearchOutputPort {
    private final ITaxRepository taxRepository;
    private final ITaxSearchMapper taxSearchMapper;

    /**
     * Obtiene la información del impuesto basada en el c digo y el ID de empresa
     * proporcionados.
     *
     * @param code         el c digo del impuesto
     * @param idEnterprise el ID de la empresa
     * @return el impuesto correspondiente al c digo y ID de empresa
     *         proporcionados
     */
    @Override
    public Tax getTax(String code, String idEnterprise) {
        TaxEntity taxEntity=taxRepository.findByCode(code, idEnterprise);
        return taxSearchMapper.toDomain(taxEntity);
    }

    /**
     * Obtiene una lista de impuestos asociados al ID de empresa proporcionado.
     *
     * @param idEnterprise el ID de la empresa
     * @return una lista de impuestos asociados al ID de empresa proporcionado
     */
    @Override
    public List<Tax> getTaxes( String idEnterprise) {
        List<TaxEntity> taxEntities=taxRepository.findAllByIdEnterprise( idEnterprise);
        return taxSearchMapper.toDomainList(taxEntities);

    }

    /**
     * Obtiene un impuesto por su ID.
     * 
     * @param id el ID del impuesto
     * @return el impuesto encontrado o null si no existe
     */
    @Override
    public Tax getTaxById(Long id) {
        TaxEntity taxEntity = taxRepository.findByIdActive(id);
        return taxSearchMapper.toDomain(taxEntity);
    }

    /**
     * Obtiene un impuesto por ID y empresa.
     * Método optimizado que valida que el impuesto pertenece a la empresa especificada.
     * 
     * @param id el ID del impuesto
     * @param idEnterprise el ID de la empresa
     * @return el impuesto encontrado o null si no existe
     */
    @Override
    public Tax getTaxByIdAndEnterprise(Long id, String idEnterprise) {
        TaxEntity taxEntity = taxRepository.findByIdAndEnterpriseActive(id, idEnterprise);
        return taxSearchMapper.toDomain(taxEntity);
    }
}
