package com.account_catalogue.domain.service;
import com.account_catalogue.application.output.IAccountCatalogueUpdateOutputPort;
import com.account_catalogue.domain.enums.ClassificationEnum;
import com.account_catalogue.domain.enums.FinancialStatusEnum;
import com.account_catalogue.domain.enums.NatureEnum;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.domain.services.AccountCatalogueUpdateService;
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
public class AccountCatalogueUpdateServiceTest {
    @Mock
    private IAccountCatalogueUpdateOutputPort accountCatalogueUpdateOutputPort;

    @InjectMocks
    private AccountCatalogueUpdateService accountCatalogueUpdateService;


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
    @DisplayName("Test para actualizar una cuenta")
    @Test
    void testUpdateAccountCatalogue(){
        Long accountId=1L;

        //given
        given(accountCatalogueUpdateOutputPort.updateAccountCatalogue(accountId,accountCatalogue)).willReturn(accountCatalogue);
        accountCatalogue.setDescription("pasivo");
        accountCatalogue.setFinancialStatus(FinancialStatusEnum.EMPTY);
        accountCatalogue.setClassification(ClassificationEnum.CURRENTASSETS);

        //when
        AccountCatalogue accountCatalogueUpdate=accountCatalogueUpdateService.updateAccountCatalogue(accountId,accountCatalogue);

        //then
        assertThat(accountCatalogueUpdate.getDescription()).isEqualTo("pasivo");
        assertThat(accountCatalogueUpdate.getFinancialStatus()).isEqualTo(FinancialStatusEnum.EMPTY);
        assertThat(accountCatalogueUpdate.getClassification()).isEqualTo(ClassificationEnum.CURRENTASSETS);
    }
}
