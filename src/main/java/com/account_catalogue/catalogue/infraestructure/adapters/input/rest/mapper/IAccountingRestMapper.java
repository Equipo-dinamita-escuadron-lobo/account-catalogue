package com.account_catalogue.catalogue.infraestructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;

import com.account_catalogue.catalogue.domain.models.AccountingEntry;
import com.account_catalogue.catalogue.domain.models.AccountingMovement;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountingEntryResponse;
import com.account_catalogue.catalogue.infraestructure.adapters.input.rest.data.response.AccountingMovementResponse;

@Mapper(componentModel = "spring")
public interface IAccountingRestMapper {
    /**
     * Convierte un objeto de dominio AccountingEntry a su DTO de respuesta.
     * MapStruct es lo suficientemente inteligente como para ver que `movements` es una lista
     * y usará automáticamente el método `toMovementResponse` para cada elemento.
     */
    AccountingEntryResponse toEntryResponse(AccountingEntry accountingEntry);

    /**
     * Convierte un objeto de dominio AccountingMovement a su DTO de respuesta.
     * Como los nombres de los campos coinciden, no se necesitan anotaciones @Mapping.
     */
    AccountingMovementResponse toMovementResponse(AccountingMovement accountingMovement);
}
