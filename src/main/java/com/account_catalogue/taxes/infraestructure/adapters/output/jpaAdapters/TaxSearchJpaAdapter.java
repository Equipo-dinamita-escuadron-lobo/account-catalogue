package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.domain.models.Tax;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxSearchMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * Obtiene una lista de impuestos activos asociados al ID de empresa proporcionado.
     *
     * @param idEnterprise el ID de la empresa
     * @return una lista de impuestos activos asociados al ID de empresa proporcionado
     */
    @Override
    public List<Tax> getActiveTaxes(String idEnterprise) {
        List<TaxEntity> taxEntities = taxRepository.findActiveByIdEnterprise(idEnterprise);
        return taxSearchMapper.toDomainList(taxEntities);
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

    /**
     * Obtiene una página de impuestos por empresa con paginación y ordenamiento.
     * 
     * @param idEnterprise el ID de la empresa
     * @param page número de página
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (asc/desc)
     * @return página de impuestos
     */
    @Override
    public Page<Tax> getTaxesPaginated(String idEnterprise, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaxEntity> taxEntitiesPage = taxRepository.findAllByIdEnterprise(idEnterprise, pageable);
        return taxEntitiesPage.map(taxSearchMapper::toDomain);
    }

    /**
     * Obtiene una página de impuestos por empresa y descripción con paginación y ordenamiento.
     *
     * @param idEnterprise el ID de la empresa
     * @param search término de búsqueda en la descripción
     * @param page número de página
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (asc/desc)
     * @return página de impuestos que coinciden con la búsqueda
     */
    @Override
    public Page<Tax> getTaxesByDescriptionPaginated(String idEnterprise, String search, int page, int size, String sortField, String sortOrder) {
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaxEntity> taxEntitiesPage = taxRepository.findByIdEnterpriseAndDescriptionContainingIgnoreCase(idEnterprise, search, pageable);
        return taxEntitiesPage.map(taxSearchMapper::toDomain);
    }

    /**
     * Cuenta el total de impuestos por empresa.
     *
     * @param idEnterprise el ID de la empresa
     * @return número total de impuestos
     */
    @Override
    public long countTaxesByEnterprise(String idEnterprise) {
        return taxRepository.countByIdEnterprise(idEnterprise);
    }

    /**
     * Cuenta impuestos por empresa y descripción.
     *
     * @param idEnterprise el ID de la empresa
     * @param search término de búsqueda en la descripción
     * @return número total de impuestos que coinciden con la búsqueda
     */
    @Override
    public long countTaxesByEnterpriseAndDescription(String idEnterprise, String search) {
        return taxRepository.countByIdEnterpriseAndDescriptionContainingIgnoreCase(idEnterprise, search);
    }
}
