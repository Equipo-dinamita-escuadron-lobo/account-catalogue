package com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.account_catalogue.accounting.domain.models.ReceiptDetail;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.ReceiptDetailEntity;

@Mapper(componentModel = "spring")
public interface IReceiptDetailMapper {
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "receipt", ignore = true)
    ReceiptDetailEntity toEntity(ReceiptDetail domain);

    ReceiptDetail toDomain(ReceiptDetailEntity entity);
}
