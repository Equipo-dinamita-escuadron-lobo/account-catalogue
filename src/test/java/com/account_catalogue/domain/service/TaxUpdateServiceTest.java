package com.account_catalogue.domain.service;

import com.account_catalogue.application.output.ITaxUpdateOutputPort;
import com.account_catalogue.application.services.TaxUpdateService;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;


import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaxUpdateServiceTest {
    @Mock
    private ITaxUpdateOutputPort taxUpdateOutputPort;

    @InjectMocks
    private TaxUpdateService taxUpdateService;


    private TaxDTO taxDTO;

    private Tax tax;
    @BeforeEach
    void setup (){
        AccountCatalogueEntity account1;
        AccountCatalogueEntity account2;
        account1=AccountCatalogueEntity.builder()
                .id(1L)
                .code("2")
                .description("dos")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.CURRENTASSETS)
                .idEnterprise("1")
                .parent(null)
                .children(null)
                .build();

        account2=AccountCatalogueEntity.builder()
                .id(2L)
                .code("3")
                .description("tres")
                .nature(NatureEnum.CREDIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.CURRENTASSETS)
                .idEnterprise("1")
                .parent(null)
                .children(null)
                .build();


        taxDTO = TaxDTO.builder()
                .id(1L)
                .idEnterprise("1")
                .code("123")
                .description("iva")
                .interest(2.7f)
                .refundAccount("1L")
                .depositAccount("2L")
                .build();

        tax = Tax.builder()
                .id(1L)
                .idEnterprise("1")
                .code("123")
                .description("iva")
                .interest(2.7f)
                .refundAccount(account1)
                .depositAccount(account2)
                .build();


    }
    @DisplayName("Test para actualizar impuesto")
    @Test
    void testUpdateTax(){
        //given
        long id=1L;
        given(taxUpdateOutputPort.update(taxDTO,id)).willReturn(tax);

        tax.setDescription("retefuente");
        tax.setInterest(2.0f);

        //when
        Tax taxUpdate=taxUpdateService.update(taxDTO,id);

        //then
        assertThat(taxUpdate.getDescription()).isEqualTo("retefuente");
        assertThat(taxUpdate.getInterest()).isEqualTo(2.0f);

    }
}
