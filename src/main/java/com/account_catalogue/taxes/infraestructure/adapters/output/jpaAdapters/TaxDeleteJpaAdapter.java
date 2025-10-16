package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;

import com.account_catalogue.taxes.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.entity.TaxEntity;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxDeleteJpaAdapter implements ITaxDeleteOutputPort {

    private final ITaxRepository taxRepository;

    /**
     * Realiza eliminación de un impuesto por su ID y empresa.
     *
     * @param id El ID del impuesto a eliminar.
     * @param idEnterprise El ID de la empresa.
     * @return true si se eliminó con éxito, false de lo contrario.
     */
    @Override
    @Transactional
    public boolean deleteByCode(long id, String idEnterprise) {
        TaxEntity taxEntity = taxRepository.findByIdAndEnterpriseActive(Long.valueOf(id), idEnterprise);
        if (taxEntity != null) {
            taxRepository.delete(taxEntity);
            return true;
        }
        return false;
    }
}
