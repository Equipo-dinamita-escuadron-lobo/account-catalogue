package com.account_catalogue.accounting.infraestructure.input.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.account_catalogue.accounting.domain.models.MessageProcessingError;
import com.account_catalogue.accounting.infraestructure.input.data.response.MessageProcessingErrorResponse;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IMessageProcessingErrorRestMapper {
   MessageProcessingErrorResponse toResponse(MessageProcessingError domain); 
}
