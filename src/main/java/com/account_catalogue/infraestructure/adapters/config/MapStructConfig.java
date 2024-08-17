package com.account_catalogue.infraestructure.adapters.config;

import com.account_catalogue.infraestructure.adapters.input.rest.mapper.*;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.ITaxUpdateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.factory.Mappers;
@Configuration
public class MapStructConfig {



    @Bean
    IAccountSearchRestMapper mapStructMapperAccountSearchRestMapper(){
        return Mappers.getMapper(IAccountSearchRestMapper.class);
    }
    @Bean
    IItemAccountCatalogueSearchMapper mapStructMapperitemAccountCatalogueSearchMapper(){
        return Mappers.getMapper(IItemAccountCatalogueSearchMapper.class);
    }
    @Bean
    IItemAccountSearchRestMapper mapStructItemAccountSearchRestMapper(){
        return Mappers.getMapper(IItemAccountSearchRestMapper.class);
    }
    @Bean
    ITaxCreateMapper mapStructTaxCreateMapper(){
        return Mappers.getMapper(ITaxCreateMapper.class);
    }
    @Bean
    ITaxCreateRestMapper mapStructTaxCreateRestMapper(){
        return Mappers.getMapper(ITaxCreateRestMapper.class);
    }
    @Bean
    ITaxSearchMapper mapStructtaxSearchMapper(){
        return Mappers.getMapper(ITaxSearchMapper.class);
    }
    @Bean
    ITaxSearchRestMapper mapStructTaxSearchRestMapper(){
        return Mappers.getMapper(ITaxSearchRestMapper.class);
    }
    @Bean
    ITaxUpdateMapper mapStructUpdateMapper(){
        return Mappers.getMapper(ITaxUpdateMapper.class);
    }
    @Bean
    ITaxUpdateRestMapper mapStructUpdateRestMapper(){
        return Mappers.getMapper(ITaxUpdateRestMapper.class);
    }

}
