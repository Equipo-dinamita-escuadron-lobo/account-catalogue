package com.account_catalogue.domain.service;


import com.account_catalogue.application.output.ITaxDeleteOutputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.domain.services.TaxDeleteService;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.willDoNothing;
import static org.assertj.core.api.Assertions.as;
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
        String taxId="1L";
        doReturn(true).when(taxDeleteOutputPort).deleteByCode(taxId);

        //when
        boolean result=deleteService.deleteByCode(taxId);

        //the
        verify(taxDeleteOutputPort,times(1)).deleteByCode(taxId);
        assertThat(result).isTrue();
    }

}
