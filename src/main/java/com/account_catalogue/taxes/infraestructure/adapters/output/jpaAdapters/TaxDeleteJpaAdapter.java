package com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters;


import com.account_catalogue.taxes.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.repository.ITaxRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxDeleteJpaAdapter implements ITaxDeleteOutputPort {


    private final ITaxRepository taxRepository;
    /**
     * Elimina un impuesto por su ID.
     *
     * @param id El ID del impuesto a eliminar.
     * @return true si se elimin con xito, false de lo contrario.
     */
    @Override
    @Transactional
    public boolean deleteByCode(long id) {
        if(taxRepository.existsById(id)){
            taxRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
