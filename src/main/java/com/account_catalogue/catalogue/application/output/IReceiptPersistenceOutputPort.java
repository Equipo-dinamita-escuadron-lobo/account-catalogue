package com.account_catalogue.catalogue.application.output;

import java.util.Optional;

import com.account_catalogue.catalogue.domain.models.Receipt;

public interface IReceiptPersistenceOutputPort {
   Receipt save(Receipt receipt);
   Optional<Receipt> findByOriginalReceiptId(Long originalReceiptId); 
}
