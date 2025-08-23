package com.account_catalogue.banks.presentation.DTO.request;

import com.account_catalogue.banks.domain.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankUpdateReq {

    @NotNull(message = "El ID es obligatorio")
    private Long id;

    @NotBlank(message = "La empresa es obligatoria")
    private String idEnterprise;

    /**
     * IMPORTANTE: El código del banco NO puede ser modificado una vez creado.
     * Si se envía un valor diferente al actual, se lanzará una excepción.
     * Este campo debe enviarse con el valor actual para validación.
     */
    @NotNull(message = "El código del banco es obligatorio")
    @Min(value = 1, message = "El código debe ser mayor a 0")
    @Max(value = 99, message = "El código debe ser menor o igual a 99")
    private Integer codigo;

    @NotBlank(message = "El nombre del banco es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "La moneda es obligatoria")
    private Currency moneda;

    @NotNull(message = "El estado es obligatorio")
    private Boolean status;
}
