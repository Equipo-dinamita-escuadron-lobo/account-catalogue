package com.account_catalogue.accounting.infraestructure.output.messageBroker.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.account_catalogue.accounting.domain.models.Receipt;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.ReceiptDetailEventDTO;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.ReceiptEventDTO;
import com.account_catalogue.catalogue.domain.models.ReceiptDetail;

import jakarta.inject.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IReceiptEventMapper {
    /*@Mapping(source= "id", target = "originalReceiptId")
    @Mapping(target = "processingStatus", constant = "RECEIVED")
    Receipt toDomain(ReceiptEventDTO eventDTO);*/

    Receipt toDomain(ReceiptEventDTO receiptEventDTO);

    @Named("mapDetails")
    List<ReceiptDetail> mapDetails(List<ReceiptDetailEventDTO> detailEventDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "invoiceId", target = "originalInvoiceId")
    ReceiptDetail toDomain(ReceiptDetailEventDTO detailEventDTO);

}
