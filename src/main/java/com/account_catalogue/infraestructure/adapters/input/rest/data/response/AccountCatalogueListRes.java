package com.account_catalogue.infraestructure.adapters.input.rest.data.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCatalogueListRes {
    String code;
    String description;
    List<AccountCatalogueListRes> children;
}
