package com.account_catalogue.copy.infraestructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de response para la cancelación de un proceso de copia.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyCancelResponseDto {

    private String estado;
}
