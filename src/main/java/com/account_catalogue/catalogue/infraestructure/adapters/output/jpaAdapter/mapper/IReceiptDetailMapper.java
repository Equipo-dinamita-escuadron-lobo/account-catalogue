package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.account_catalogue.catalogue.domain.models.ReceiptDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.ReceiptDetailEntity;

public interface IReceiptDetailMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    ReceiptDetailEntity toEntity(ReceiptDetail domain);
    ReceiptDetail toDomain(ReceiptDetailEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orginalReceiptId", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    void updateEntityFromDomain(ReceiptDetail domain, @MappingTarget ReceiptDetailEntity entity);
}
