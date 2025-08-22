package com.account_catalogue.paymentMethods.presentation.DTO.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodUpdateReq {

    @NotNull(message = "El ID es obligatorio")
    private Long id;

    @NotBlank(message = "La empresa es obligatoria")
    private String idEnterprise;

    @NotBlank(message = "El nombre del método de pago es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String name;

    @NotBlank(message = "La cuenta contable asociada es obligatoria")
    private String accountingAccount;

    @NotNull(message = "El estado es obligatorio")
    private Boolean status;
}
