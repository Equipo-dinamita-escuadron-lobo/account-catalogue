package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;


import com.account_catalogue.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.ITaxRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxDeleteJpaAdapter implements ITaxDeleteOutputPort {


    private final ITaxRepository taxRepository;
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
