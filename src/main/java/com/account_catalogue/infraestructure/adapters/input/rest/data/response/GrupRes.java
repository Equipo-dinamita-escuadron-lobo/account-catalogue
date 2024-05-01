package com.account_catalogue.infraestructure.adapters.input.rest.data.response;

import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrupRes {
    private long id;
    private String code;
    private String description;
    private AccountRes account;
}
