package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.account_catalogue.catalogue.domain.models.ReceiptDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.ReceiptDetailEntity;

@Mapper(componentModel = "spring")
public interface IReceiptDetailMapper {
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "receipt", ignore = true)
    ReceiptDetailEntity toEntity(ReceiptDetail domain);

    ReceiptDetail toDomain(ReceiptDetailEntity entity);
}
