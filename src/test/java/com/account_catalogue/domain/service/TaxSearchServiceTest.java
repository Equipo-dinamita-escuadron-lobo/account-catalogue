package com.account_catalogue.domain.service;

import com.account_catalogue.application.output.ITaxSearchOutputPort;
import com.account_catalogue.domain.DTO.TaxDTO;
import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import com.account_catalogue.domain.models.Tax;
import com.account_catalogue.domain.services.TaxSearchService;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;


import java.util.Collections;
import java.util.List;


import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaxSearchServiceTest {


    @Mock
    private ITaxSearchOutputPort taxSearchOutputPort;

    @InjectMocks
    private TaxSearchService taxSearchService;


    private TaxDTO taxDTO;
    private AccountCatalogueEntity account1;
    private  AccountCatalogueEntity account2;
    private Tax tax;
    @BeforeEach
    void setup (){
       TaxDTO taxDTO;

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
                .code("123")
                .description("iva")
                .interest(2.7f)
                .refundAccount("1L")
                .depositAccount("2L")
                .build();

        tax = Tax.builder()
                .id(1L)
                .code("123")
                .description("iva")
                .interest(2.7f)
                .refundAccount(account1)
                .depositAccount(account2)
                .build();


    }


    @DisplayName("Test para listar impuestos")
    @Test
    void testGetTaxes(){
       Tax tax2 = Tax.builder()
                .id(2L)
                .code("1234")
                .description("iva")
               .interest(2.7f)
                .refundAccount(account1)
                .depositAccount(account2)
                .build();

        //given
        given(taxSearchOutputPort.getTaxes()).willReturn(List.of(tax,tax2));
        //when
        List<Tax> taxes=taxSearchService.getTaxes();

        //then
        assertThat(taxes).isNotNull();
        assertThat(taxes.size()).isEqualTo(2);

    }
    @DisplayName("Test prar retornar una lista vacia de impuestos")
    @Test
    void testGetTaxesEmpty(){
        Tax tax2 = Tax.builder()
                .id(2L)
                .code("1234")
                .description("iva")
                .interest(2.7f)
                .refundAccount(account1)
                .depositAccount(account2)
                .build();
        //given
        given(taxSearchOutputPort.getTaxes()).willReturn(Collections.emptyList());


        //when
        List<Tax> taxes=taxSearchService.getTaxes();

        //then
        assertThat(taxes).isEmpty();

    }
    @DisplayName("Test para obtener un impuesto por el codigo")
    @Test
    void testGetTax(){
        //giiven
        given(taxSearchOutputPort.getTax("123")).willReturn(tax);
        //when
        Tax taxAux=taxSearchService.getTax(tax.getCode());
        //then
        assertThat(taxAux).isNotNull();
        assertThat(taxAux.getId()).isEqualTo(1L);
        assertThat(taxAux.getCode()).isEqualTo("123");

    }
    @DisplayName("Test para obtener un impuesto y su codígo no coicide")
    @Test
    void testGetTaxIncorrrect(){
        //giiven
        given(taxSearchOutputPort.getTax("123")).willReturn(null);
        //when
        Tax taxAux=taxSearchService.getTax(tax.getCode());
        //then
        assertThat(taxAux).isNull();

    }

}
