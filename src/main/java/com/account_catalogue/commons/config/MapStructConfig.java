package com.account_catalogue.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper.*;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxCreateRestMapper;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxSearchRestMapper;
import com.account_catalogue.taxes.infraestructure.adapters.input.rest.mapper.ITaxUpdateRestMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxCreateMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxSearchMapper;
import com.account_catalogue.taxes.infraestructure.adapters.output.jpaAdapters.mapper.ITaxUpdateMapper;

import org.mapstruct.factory.Mappers;

@Configuration
public class MapStructConfig {

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * IAccountSearchRestMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el modelo de dominio AccountCatalogue al objeto de
     * transferencia de datos AccountCatalogueListRes
     * que se utiliza para respuestas en la API REST. El mapeador se registra como
     * un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    IAccountSearchRestMapper mapStructMapperAccountSearchRestMapper() {
        return Mappers.getMapper(IAccountSearchRestMapper.class);
    }

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * IItemAccountCatalogueSearchMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el modelo de dominio AccountCatalogue al objeto de
     * transferencia de datos ItemAccountCatalogueSearchRes
     * que se utiliza para respuestas en la API REST. El mapeador se registra como
     * un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    IItemAccountCatalogueSearchMapper mapStructMapperitemAccountCatalogueSearchMapper() {
        return Mappers.getMapper(IItemAccountCatalogueSearchMapper.class);
    }

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * IItemAccountSearchRestMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el modelo de dominio AccountCatalogue al objeto de
     * transferencia de datos ItemAccountCatalogueSearchRes
     * que se utiliza para respuestas en la API REST. El mapeador se registra como
     * un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    IItemAccountSearchRestMapper mapStructItemAccountSearchRestMapper() {
        return Mappers.getMapper(IItemAccountSearchRestMapper.class);
    }

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * ITaxCreateMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el modelo de dominio Tax al objeto de transferencia de
     * datos TaxEntity
     * que se utiliza para interacciones con el repositorio JPA. El mapeador se
     * registra como un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    ITaxCreateMapper mapStructTaxCreateMapper() {
        return Mappers.getMapper(ITaxCreateMapper.class);
    }

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * ITaxCreateRestMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el objeto de transferencia de datos TaxCreateReq al
     * modelo de dominio Tax
     * que se utiliza en la lógica de negocio de la aplicación. El mapeador se
     * registra como un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    ITaxCreateRestMapper mapStructTaxCreateRestMapper() {
        return Mappers.getMapper(ITaxCreateRestMapper.class);
    }

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * ITaxSearchMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el modelo de dominio Tax al objeto de transferencia de
     * datos TaxEntity
     * que se utiliza para interacciones con el repositorio JPA. El mapeador se
     * registra como un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    ITaxSearchMapper mapStructtaxSearchMapper() {
        return Mappers.getMapper(ITaxSearchMapper.class);
    }

    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * ITaxSearchRestMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el modelo de dominio Tax al objeto de transferencia de
     * datos TaxSearchRes
     * que se utiliza para respuestas en la API REST. El mapeador se registra como
     * un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    ITaxSearchRestMapper mapStructTaxSearchRestMapper() {
        return Mappers.getMapper(ITaxSearchRestMapper.class);
    }


    /**
     * Este método se utiliza para generar una implementación de la interfaz
     * ITaxUpdateRestMapper
     * en tiempo de compilación utilizando la biblioteca MapStruct. La interfaz se
     * utiliza para mapear el objeto de transferencia de datos TaxUpdateReq al
     * modelo de dominio Tax
     * que se utiliza en la lógica de negocio de la aplicación. El mapeador se
     * registra como un Bean de Spring
     * para hacerlo disponible para inyección de dependencias.
     */
    @Bean
    ITaxUpdateRestMapper mapStructUpdateRestMapper() {
        return Mappers.getMapper(ITaxUpdateRestMapper.class);
    }
}
