package com.account_catalogue.domain.service;


import com.account_catalogue.catalogue.application.input.IAccountCatalogueSearchInputPort;
import com.account_catalogue.catalogue.application.services.AccountCatalogueSearchService;
import com.account_catalogue.catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.catalogue.domain.enums.NatureEnum;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AccountCatalogueSearchServiceTest {
    @Mock
    private IAccountCatalogueSearchInputPort accountCatalogueSearchInputPort;

    @InjectMocks
    private AccountCatalogueSearchService accountCatalogueSearchService;

    private AccountCatalogue accountCatalogue;

    @BeforeEach
    void setup(){
        accountCatalogue=AccountCatalogue.builder()
                .id(1L)
                .idEnterprise("1")
                .code("123")
                .description("Activo")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.EQUITY)
                .parent(null)
                .children(new ArrayList<>())
                .build();

    }
    @DisplayName("Test para obtener un cuenta por codigo y ide de la empresa")
    @Test
    void testGetAccountCatalogueByCode(){
        //given
        given(accountCatalogueSearchInputPort.getAccountCatalogueByCode("123","1")).willReturn(accountCatalogue);

        //tax
        AccountCatalogue accountCatalogueAux=accountCatalogueSearchInputPort.getAccountCatalogueByCode("123","1");

        //then
        assertThat(accountCatalogueAux).isNotNull();
        assertThat(accountCatalogueAux.getIdEnterprise()).isEqualTo("1");
        assertThat(accountCatalogueAux.getCode()).isEqualTo("123");

    }
    @DisplayName("Test para obtener un cuenta por codigo y ide de la empresa, codigo o id que no existen")
    @Test
    void testGetAccountCatalogueByCodeIncorrect(){
        //given
        given(accountCatalogueSearchInputPort.getAccountCatalogueByCode("123","1")).willReturn(null);

        //tax
        AccountCatalogue accountCatalogueAux=accountCatalogueSearchInputPort.getAccountCatalogueByCode("123","1");

        //then
        assertThat(accountCatalogueAux).isNull();
    }
    @DisplayName("Test para obtener un cuenta con sus hijos")
    @Test
    void testGetAccountCatalogueTree(){
        AccountCatalogue accountCatalogue2=AccountCatalogue.builder()
                .idEnterprise("1")
                .code("1230")
                .description("Activo")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.EQUITY)
                .parent(accountCatalogue)
                .children(new ArrayList<>())
                .build();

        accountCatalogue.getChildren().add(accountCatalogue2);

        //given
        given(accountCatalogueSearchInputPort.getAccountCatalogueTree("123","1")).willReturn(accountCatalogue);

        //tax
        AccountCatalogue accountCatalogueAux=accountCatalogueSearchInputPort.getAccountCatalogueTree("123","1");

        //then
        assertThat(accountCatalogueAux).isNotNull();
        assertThat(accountCatalogueAux.getIdEnterprise()).isEqualTo("1");
        assertThat(accountCatalogueAux.getCode()).isEqualTo("123");

    }
    @DisplayName("Test para obtener un cuenta con sus hijos, pero el codigo o el id de la empresa no existe")
    @Test
    void testGetAccountCatalogueTreeIncorrect(){
        AccountCatalogue accountCatalogue2=AccountCatalogue.builder()
                .idEnterprise("1")
                .code("1230")
                .description("Activo")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.EQUITY)
                .parent(accountCatalogue)
                .children(new ArrayList<>())
                .build();

        accountCatalogue.getChildren().add(accountCatalogue2);

        //given
        given(accountCatalogueSearchInputPort.getAccountCatalogueTree("123","1")).willReturn(null);

        //tax
        AccountCatalogue accountCatalogueAux=accountCatalogueSearchInputPort.getAccountCatalogueTree("123","1");

        //then
        assertThat(accountCatalogueAux).isNull();
    }

    @DisplayName("Test para obtener un cuenta con su id")
    @Test
    void testgetAccountCatalogueById(){

        //given
        given(accountCatalogueSearchInputPort.getAccountCatalogueById(1L)).willReturn(accountCatalogue);
        //tax
        AccountCatalogue accountCatalogueAux=accountCatalogueSearchInputPort.getAccountCatalogueById(1L);
        //then
        assertThat(accountCatalogueAux).isNotNull();
        assertThat(accountCatalogueAux.getIdEnterprise()).isEqualTo("1");
        assertThat(accountCatalogueAux.getCode()).isEqualTo("123");

    }
    @DisplayName("Test para obtener un cuenta con su id, pero con un id que no existe!")
    @Test
    void testgetAccountCatalogueByIdIncorrect(){

        //given
        given(accountCatalogueSearchInputPort.getAccountCatalogueById(2L)).willReturn(null);
        //tax
        AccountCatalogue accountCatalogueAux=accountCatalogueSearchInputPort.getAccountCatalogueById(2L);
        //then
        assertThat(accountCatalogueAux).isNull();

    }


}
