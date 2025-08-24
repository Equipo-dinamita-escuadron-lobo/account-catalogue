package com.account_catalogue.banks.presentation.DTO.request;

import com.account_catalogue.banks.domain.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankCreateReq {

    @NotBlank(message = "La empresa es obligatoria")
    private String idEnterprise;

    @NotBlank(message = "El código del banco es obligatorio")
    @Pattern(regexp = "^\\d{2}$", message = "El código debe ser exactamente 2 dígitos (01-99)")
    private String codigo;

    @NotBlank(message = "El nombre del banco es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "La moneda es obligatoria")
    private Currency moneda;

    @Builder.Default
    private Boolean status = true;
}
