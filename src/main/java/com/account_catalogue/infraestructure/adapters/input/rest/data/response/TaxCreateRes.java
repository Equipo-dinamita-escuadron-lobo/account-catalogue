package com.account_catalogue.infraestructure.adapters.input.rest.data.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxCreateRes {

    private  Long id;
    private String code;
    private String description;
    private float interest;
    private String refundAccount;
    private String account;
}
