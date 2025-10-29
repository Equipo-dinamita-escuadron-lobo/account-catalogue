package com.account_catalogue.accounting.infraestructure.output.messageBroker.recovery;
import org.springframework.stereotype.Component;

import com.account_catalogue.accounting.domain.ports.IEventRecoveryActionPort;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.EventDTO;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.InvoiceSyncDto;



@Component
public class InvoiceRecoveryAction implements IEventRecoveryActionPort<EventDTO<InvoiceSyncDto>> {

    @Override
    public boolean executeRecoveryAction(EventDTO<InvoiceSyncDto> event) {
        // TODO por hacer para Rodrigo
        throw new UnsupportedOperationException("Unimplemented method 'executeRecoveryAction'");
    }

    @Override
    public boolean canHandle(EventDTO<InvoiceSyncDto> event) {
        // TODO por hacer para Rodrigo
        throw new UnsupportedOperationException("Unimplemented method 'canHandle'");
    }
    
}
