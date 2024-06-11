package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper;

import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.TaxEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface  ITaxSearchMapper {
    default Tax toDomain(TaxEntity taxEntity){
        if(taxEntity==null){
            return null;
        }
        return Tax.builder()
                .id(taxEntity.getId())
                .code(taxEntity.getCode())
                .description(taxEntity.getDescription())
                .interest(taxEntity.getInterest())
                .refundAccount(taxEntity.getRefundAccount())
                .depositAccount(taxEntity.getDepositAccount())
                .build();
    }

    default  List<Tax> toDomainList(List<TaxEntity> taxes){
        if(taxes==null){
            return null;
        }
        return  taxes.stream()
                .map(taxEntity->{
                    Tax tax  =Tax.builder()
                            .id(taxEntity.getId())
                            .code(taxEntity.getCode())
                            .description(taxEntity.getDescription())
                            .interest(taxEntity.getInterest())
                            .depositAccount(taxEntity.getDepositAccount())
                            .refundAccount(taxEntity.getRefundAccount())
                            .build();
                    return tax;

                })
                .toList();
    }
}
