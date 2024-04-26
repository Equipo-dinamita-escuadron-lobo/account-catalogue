package com.account_catalogue.infraestructure.adapters.config;

import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.factory.Mappers;
@Configuration
public class MapStructConfig {

    @Bean
    IAccountCreateRestMapper mapStructMapperAccountCreateRestMapper() {
        return Mappers.getMapper(IAccountCreateRestMapper.class);
    }

    @Bean
    IAccountCatalogueSearchMapper mapStructMapperAccountSearchMapper(){
        return Mappers.getMapper(IAccountCatalogueSearchMapper.class);
    }
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
}
