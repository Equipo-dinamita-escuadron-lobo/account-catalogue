package com.account_catalogue.catalogue.application.input;

import com.account_catalogue.catalogue.domain.models.Receipt;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.ReceiptEventDTO;

public interface IReceiptProcessInputPort {
    Receipt processReceiptEvent(ReceiptEventDTO eventDTO); 
}
