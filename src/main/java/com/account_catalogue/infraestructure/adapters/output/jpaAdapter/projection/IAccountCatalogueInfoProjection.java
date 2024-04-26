package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.projection;

import lombok.Builder;
import lombok.Getter;



public interface IAccountCatalogueInfoProjection {
    String getCode();
    String getDescription();
}
