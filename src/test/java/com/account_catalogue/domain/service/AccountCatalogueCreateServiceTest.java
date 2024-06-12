package com.account_catalogue.domain.service;


import com.account_catalogue.application.output.IAccountCatalogueCreateOutputPort;
import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.domain.services.AccountCatalogueCreateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AccountCatalogueCreateServiceTest {
    @Mock
    private IAccountCatalogueCreateOutputPort accountCatalogueCreateOutputPort;
    @InjectMocks
    private AccountCatalogueCreateService accountCatalogueCreateService;


    private AccountCatalogue accountCatalogue;

    @BeforeEach
    void setup(){
        accountCatalogue=AccountCatalogue.builder()
                .idEnterprise("1")
                .code("123")
                .description("Activo")
                .nature(NatureEnum.DEBIT)
                .financialStatus(FinancialStatusEnum.INCOMESTATEMENT)
                .classification(ClassificationEnum.EQUITY)
                .parent(null)
                .children(null)
                .build();

    }
    @DisplayName("test para agregar cuenta")
    @Test
    void testCreateAccoubtCatalogue(){
        //given
        given(accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue)).willReturn(accountCatalogue);

        //when
        AccountCatalogue accountCatalogueSave=accountCatalogueCreateOutputPort.createAccountCatalogue(accountCatalogue);

        //then
        assertThat(accountCatalogueSave).isNotNull();
        assertThat(accountCatalogueSave.getCode()).isEqualTo("123");


    }
}
