package com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.account_catalogue.catalogue.domain.models.Receipt;
import com.account_catalogue.catalogue.domain.models.ReceiptDetail;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.ReceiptDetailEventDTO;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.ReceiptEventDTO;

import jakarta.inject.Named;

@Mapper(componentModel = "spring")
public interface IReceiptEventMapper {
    @Mapping(source= "id", target = "originalReceiptId")
    @Mapping(target = "processingStatus", constant = "RECEIVED")
    Receipt toDomain(ReceiptEventDTO eventDTO);

    @Named("mapDetails")
    List<ReceiptDetail> mapDetails(List<ReceiptDetailEventDTO> detailEventDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "invoiceId", target = "originalInvoiceId")
    ReceiptDetail toDomain(ReceiptDetailEventDTO detailEventDTO);

}
