package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.input.ITaxDeleteInputPort;
import com.account_catalogue.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.ITaxRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaxDeleteJpaAdapter implements ITaxDeleteOutputPort {

    @Autowired
    private ITaxRepository taxRepository;
    @Override
    public boolean deleteByCode(String code) {
        if(taxRepository.existsByCode(code)){
            taxRepository.deleteByCode(code);
            return true;
        }
        return false;
    }
}
