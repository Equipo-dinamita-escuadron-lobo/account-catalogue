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

    @NotBlank(message = "El código del banco es obligatorio")
    @Pattern(regexp = "^\\d{2}$", message = "El código debe tener exactamente 2 dígitos (01-99)")
    private String code;

    @NotBlank(message = "El nombre del banco es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String name;

    @NotNull(message = "La moneda es obligatoria")
    private Currency currency;

    @NotNull(message = "El estado es obligatorio")
    private Boolean status;
}
