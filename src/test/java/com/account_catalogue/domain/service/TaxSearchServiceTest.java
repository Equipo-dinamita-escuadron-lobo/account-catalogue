package com.account_catalogue.domain.service;

import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.taxes.application.output.ITaxSearchOutputPort;
import com.account_catalogue.taxes.application.services.TaxSearchService;
import com.account_catalogue.taxes.application.services.TaxValidationService;
import com.account_catalogue.taxes.domain.models.Tax;

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

    @Mock
    private TaxValidationService taxValidationService;

    @InjectMocks
    private TaxSearchService taxSearchService;



    private AccountCatalogueEntity account1;
    private  AccountCatalogueEntity account2;
    private Tax tax;
    @BeforeEach
    void setup (){

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

        tax = Tax.builder()
                .id(1L)
                .idEnterprise("1")
                .code("123")
                .description("iva")
                .interest(2.7)
                .refundAccount(account1)
                .depositAccount(account2)
                .build();


    }


    @DisplayName("Test para listar impuestos")
    @Test
    void testGetTaxes(){
       Tax tax2 = Tax.builder()
                .id(2L)
               .idEnterprise("1")
                .code("1234")
                .description("iva")
               .interest(2.7)
                .refundAccount(account1)
                .depositAccount(account2)
                .build();

        //given
        given(taxSearchOutputPort.getTaxes("1")).willReturn(List.of(tax,tax2));
        //when
        List<Tax> taxes=taxSearchService.getTaxes("1");

        //then
        assertThat(taxes).isNotNull();
        assertThat(taxes.size()).isEqualTo(2);

    }
    @DisplayName("Test prar retornar una lista vacia de impuestos")
    @Test
    void testGetTaxesEmpty(){
        //given
        given(taxSearchOutputPort.getTaxes("2")).willReturn(Collections.emptyList());


        //when
        List<Tax> taxes=taxSearchService.getTaxes("2");

        //then
        assertThat(taxes).isEmpty();

    }
    @DisplayName("Test para obtener un impuesto por el codigo")
    @Test
    void testGetTax(){
        //giiven
        given(taxSearchOutputPort.getTax("123","1")).willReturn(tax);
        willDoNothing().given(taxValidationService).validateTaxExists("123", "1");
        //when
        Tax taxAux=taxSearchService.getTax(tax.getCode(),tax.getIdEnterprise());
        //then
        assertThat(taxAux).isNotNull();
        assertThat(taxAux.getId()).isEqualTo(1L);
        assertThat(taxAux.getCode()).isEqualTo("123");

    }
    @DisplayName("Test para obtener un impuesto y su codígo no coicide")
    @Test
    void testGetTaxIncorrrect(){
        //giiven
        given(taxSearchOutputPort.getTax("123","2")).willReturn(null);
        willDoNothing().given(taxValidationService).validateTaxExists("123", "2");
        //when
        Tax taxAux=taxSearchService.getTax(tax.getCode(),"2");
        //then
        assertThat(taxAux).isNull();

    }

    @DisplayName("Test para obtener una lista de impuestos activos")
    @Test
    void testGetActiveTaxes(){
        //given
        given(taxSearchOutputPort.getActiveTaxes("1")).willReturn(List.of(tax));
        //when
        List<Tax> activeTaxes = taxSearchService.getActiveTaxes("1");

        //then
        assertThat(activeTaxes).isNotNull();
        assertThat(activeTaxes.size()).isEqualTo(1);
        assertThat(activeTaxes.get(0)).isEqualTo(tax);
        verify(taxSearchOutputPort).getActiveTaxes("1");
    }

}
