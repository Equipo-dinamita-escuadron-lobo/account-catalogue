package com.account_catalogue.bankAccounts.presentation.DTO.request;

import com.account_catalogue.bankAccounts.domain.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountUpdateReq {

    @NotNull(message = "El ID es obligatorio")
    private Long id;

    @NotBlank(message = "La empresa es obligatoria")
    private String idEnterprise;

    /**
     * IMPORTANTE: El número de cuenta NO puede ser modificado una vez creado.
     * Si se envía un valor diferente al actual, se lanzará una excepción.
     * Este campo debe enviarse con el valor actual para validación.
     */
    @NotNull(message = "El número de cuenta es obligatorio")
    @Min(value = 10000000L, message = "El número de cuenta debe tener al menos 8 dígitos")
    @Max(value = 9999999999999999L, message = "El número de cuenta debe tener máximo 16 dígitos")
    private Long accountNumber;

    /**
     * IMPORTANTE: El banco NO puede ser modificado una vez creado.
     * Si se envía un valor diferente al actual, se lanzará una excepción.
     * Este campo debe enviarse con el valor actual para validación.
     */
    @NotNull(message = "El banco es obligatorio")
    private Long bankId;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    @NotBlank(message = "La cuenta contable es obligatoria")
    private String cuentaContable;

    @NotNull(message = "El estado es obligatorio")
    private Boolean status;
}
