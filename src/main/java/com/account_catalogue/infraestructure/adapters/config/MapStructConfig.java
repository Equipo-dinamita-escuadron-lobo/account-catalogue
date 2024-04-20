package com.account_catalogue.infraestructure.adapters.config;

import com.account_catalogue.infraestructure.adapters.input.rest.mapper.IAccountCreateRestMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mapstruct.factory.Mappers;
@Configuration
public class MapStructConfig {

    @Bean
    IAccountCreateRestMapper mapStructMapperAccountCreateRestMapper() {
        return Mappers.getMapper(IAccountCreateRestMapper.class
        );
    }
}
