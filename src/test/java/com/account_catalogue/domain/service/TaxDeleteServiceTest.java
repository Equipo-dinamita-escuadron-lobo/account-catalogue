package com.account_catalogue.domain.service;


import com.account_catalogue.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.application.services.TaxDeleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaxDeleteServiceTest {

    @Mock
    private ITaxDeleteOutputPort taxDeleteOutputPort;

    @InjectMocks
    private TaxDeleteService deleteService;


    @DisplayName("Test eliminar impuesto")
    @Test
    void testDeleteTax(){
        long taxId=1L;
        doReturn(true).when(taxDeleteOutputPort).deleteByCode(taxId);

        //when
        boolean result=deleteService.deleteByCode(taxId);

        //the
        verify(taxDeleteOutputPort,times(1)).deleteByCode(taxId);
        assertThat(result).isTrue();
    }

}
