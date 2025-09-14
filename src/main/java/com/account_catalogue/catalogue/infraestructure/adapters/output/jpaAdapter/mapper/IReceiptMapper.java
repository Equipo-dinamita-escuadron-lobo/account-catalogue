package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.account_catalogue.catalogue.domain.models.Receipt;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.ReceiptEntity;

@Mapper(componentModel = "spring", uses = {IReceiptDetailMapper.class})
public interface IReceiptMapper {
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "details", ignore = true) 
    ReceiptEntity toEntity(Receipt domain);
    Receipt toDomain(ReceiptEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originalReceiptId", ignore = true)
    @Mapping(target = "details", ignore = true)
    void updateEntityFromDomain(Receipt domain, @MappingTarget ReceiptEntity entity);
    
}