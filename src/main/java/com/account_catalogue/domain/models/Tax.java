package com.account_catalogue.domain.models;

import lombok.*;


import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Tax {
    private  Long id;
    private String code;
    private String description;
    private float interest;
    private String refundAccount;
    private String account;
    private Set<AccountCatalogue> accounts;

}
