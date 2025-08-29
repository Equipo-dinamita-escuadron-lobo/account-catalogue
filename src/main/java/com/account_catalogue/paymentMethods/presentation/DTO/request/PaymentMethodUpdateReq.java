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

    /**
     * IMPORTANTE: La cuenta contable NO puede ser modificada una vez creado el método de pago.
     * Si se envía un valor diferente al actual, se lanzará una excepción.
     * Este campo debe enviarse con el valor actual para validación.
     */
    @NotBlank(message = "La cuenta contable asociada es obligatoria")
    private String accountingAccount;
}
