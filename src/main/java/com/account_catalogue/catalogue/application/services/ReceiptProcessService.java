package com.account_catalogue.catalogue.application.services;

import org.springframework.stereotype.Service;

import com.account_catalogue.catalogue.application.input.IReceiptProcessInputPort;
import com.account_catalogue.catalogue.application.output.IReceiptPersistenceOutputPort;
import com.account_catalogue.catalogue.domain.models.Receipt;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.DTO.ReceiptEventDTO;
import com.account_catalogue.catalogue.infraestructure.adapters.output.messageBroker.mapper.IReceiptEventMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ReceiptProcessService implements IReceiptProcessInputPort {
    private final IReceiptPersistenceOutputPort receiptPersistenceOutputPort;
    private final IReceiptEventMapper receiptEventMapper;
    

    @Override
    public Receipt processReceiptEvent(ReceiptEventDTO eventDTO) {
        log.info("Processing receipt event for original ID: {}", eventDTO.getId());

        // 1. Verificar idempotencia: ¿Este recibo ya fue procesado?
        return receiptPersistenceOutputPort.findByOriginalReceiptId(eventDTO.getId())
                .map(existingReceipt -> {
                    log.warn("Receipt with original ID {} already exists. Skipping persistence.", eventDTO.getId());
                    // Puedes decidir actualizar el recibo existente o simplemente devolverlo.
                    // Por ahora, solo lo devolvemos si ya existe.
                    return existingReceipt;
                })
                .orElseGet(() -> {
                    // 2. Mapear DTO de evento a modelo de dominio
                    Receipt receipt = receiptEventMapper.toDomain(eventDTO);
                    receipt.setProcessingStatus("RECEIVED"); // Estado inicial

                    // 3. Persistir el recibo
                    Receipt savedReceipt = receiptPersistenceOutputPort.save(receipt);
                    log.info("Receipt with original ID {} saved successfully with internal ID: {}",
                            eventDTO.getId(), savedReceipt.getId());
                    return savedReceipt;
                });
    }

    
}
