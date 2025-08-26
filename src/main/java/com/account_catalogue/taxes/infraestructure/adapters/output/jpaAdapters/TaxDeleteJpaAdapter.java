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
     * Realiza soft delete de un impuesto por su ID.
     * Marca el impuesto como eliminado (isDeleted = true).
     *
     * @param id El ID del impuesto a eliminar.
     * @return true si se marcó como eliminado con éxito, false de lo contrario.
     */
    @Override
    @Transactional
    public boolean deleteByCode(long id) {
        TaxEntity taxEntity = taxRepository.findByIdActive(id);
        if (taxEntity != null) {
            taxEntity.setIsDeleted(true);
            taxRepository.save(taxEntity);
            return true;
        }
        return false;
    }
}
