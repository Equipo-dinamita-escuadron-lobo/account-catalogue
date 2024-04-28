package com.account_catalogue.infraestructure.adapters.config;

import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountUpdateRestMapper;
import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IItemAccountSearchRestMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueUpdateMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.factory.Mappers;
@Configuration
public class MapStructConfig {


    @Bean
    IAccountCatalogueCreateMapper mapStructAccountCatalogueCreateMapper(){
        return Mappers.getMapper(IAccountCatalogueCreateMapper.class);
    }

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
    @Bean
    IAccountCatalogueUpdateMapper mapStructIAccountCatalogueUpdateMapper(){
        return Mappers.getMapper(IAccountCatalogueUpdateMapper.class);
    }
    @Bean
    IAccountUpdateRestMapper mapStructIAccountUpdateRestMapper(){
        return Mappers.getMapper(IAccountUpdateRestMapper.class);
    }
}
