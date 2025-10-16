package com.account_catalogue.taxes.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxDTO {
    private Long id;
    private String idEnterprise;
    private String code;
    private String description;
    private Float interest;
    private Long depositAccountId;
    private Long refundAccountId;
}
