package com.account_catalogue.accounting.infraestructure.output.jpaAdapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.account_catalogue.accounting.domain.models.MessageProcessingError;
import com.account_catalogue.accounting.infraestructure.output.jpaAdapter.entity.MessageProcessingErrorEntity;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IMessageProcessingErrorPersistenceMapper {
    MessageProcessingError toDomain(MessageProcessingErrorEntity entity);
    MessageProcessingErrorEntity toEntity(MessageProcessingError domain);
}
