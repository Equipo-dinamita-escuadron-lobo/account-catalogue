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
    @Pattern(regexp = "^\\d{2}$", message = "El código debe tener exactamente 2 dígitos (01-99)")
    private String code;

    @NotBlank(message = "El nombre del banco es obligatorio")
    @Size(max = 100, message = "The name must not exceed 100 characters")
    private String name;

    @NotNull(message = "La moneda es obligatoria")
    private Currency currency;

    @Builder.Default
    private Boolean status = true;
}
