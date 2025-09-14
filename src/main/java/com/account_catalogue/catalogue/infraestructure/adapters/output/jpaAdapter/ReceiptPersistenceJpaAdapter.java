package com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.account_catalogue.catalogue.application.output.IReceiptPersistenceOutputPort;
import com.account_catalogue.catalogue.domain.models.Receipt;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.ReceiptDetailEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.ReceiptEntity;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IReceiptDetailMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IReceiptMapper;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.repository.IReceiptRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ReceiptPersistenceJpaAdapter implements IReceiptPersistenceOutputPort {
    private final IReceiptRepository receiptRepository;
    private final IReceiptMapper receiptMapper;
    private final IReceiptDetailMapper receiptDetailMapper;
    
    @Override
    public Receipt save(Receipt receipt) {
        ReceiptEntity receiptEntity = receiptMapper.toEntity(receipt);
        List<ReceiptDetailEntity> detailEntities = receipt.getDetails().stream()
                .map(receiptDetailMapper::toEntity)
                .peek(detailEntity -> detailEntity.setReceipt(receiptEntity))
                .collect(Collectors.toList());
        receiptEntity.setDetails(detailEntities);

        ReceiptEntity savedEntity = receiptRepository.save(receiptEntity);
        return receiptMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Receipt> findByOriginalReceiptId(Long originalReceiptId) {
        return receiptRepository.findByOriginalReceiptId(originalReceiptId)
                .map(receiptMapper::toDomain);
    }
    
}
