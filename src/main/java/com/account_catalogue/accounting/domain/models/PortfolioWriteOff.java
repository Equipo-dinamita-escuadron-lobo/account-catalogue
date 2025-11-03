package com.account_catalogue.accounting.domain.models;

import java.time.LocalDate;
import java.util.List;

import com.account_catalogue.accounting.domain.enums.ProcessingStatus;
import com.account_catalogue.accounting.domain.enums.WriteOffStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioWriteOff {
    private Long id;
    private Long originalWriteOffId;
    private String code;
    private String justification;
    private Long totalAmount;
    private LocalDate writeOffDate;
    private Long debitAuxiliaryAccount;
    private Long thirdId;
    private WriteOffStatus status;
    private String enterpriseId;
    private List<WriteOffDetail> details;
    private ProcessingStatus processingStatus;
}
