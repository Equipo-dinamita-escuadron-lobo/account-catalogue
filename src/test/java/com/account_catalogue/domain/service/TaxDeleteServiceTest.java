package com.account_catalogue.domain.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.account_catalogue.taxes.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.taxes.application.services.TaxDeleteService;
import com.account_catalogue.taxes.application.services.TaxValidationService;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaxDeleteServiceTest {

    @Mock
    private ITaxDeleteOutputPort taxDeleteOutputPort;

    @Mock
    private TaxValidationService taxValidationService;

    @InjectMocks
    private TaxDeleteService deleteService;


    @DisplayName("Test eliminar impuesto")
    @Test
    void testDeleteTax(){
        // given
        long taxId = 1L;
        String enterpriseId = "test-enterprise-123";
        
        // Mock de validación - no debe lanzar excepción cuando el impuesto existe
        doNothing().when(taxValidationService).validateTaxExists(taxId, enterpriseId);
        
        // Mock del output port
        doReturn(true).when(taxDeleteOutputPort).deleteByCode(taxId, enterpriseId);

        // when
        boolean result = deleteService.deleteByCode(taxId, enterpriseId);

        // then
        verify(taxValidationService, times(1)).validateTaxExists(taxId, enterpriseId);
        verify(taxDeleteOutputPort, times(1)).deleteByCode(taxId, enterpriseId);
        assertThat(result).isTrue();
    }

}
