package com.chartaccounts.infraestructure.adapters.config;

import com.chartaccounts.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueCreateMapper;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.factory.Mappers;
@Configuration
public class MapStructConfig {
    @Bean
    IAccountCatalogueCreateMapper mapStructMapperAccountCatalogueMapper() {
        return Mappers.getMapper(IAccountCatalogueCreateMapper.class
        );
    }


    @Bean
    IAccountCreateRestMapper mapStructMapperAccountCreateRestMapper() {
        return Mappers.getMapper(IAccountCreateRestMapper.class
        );
    }
}
