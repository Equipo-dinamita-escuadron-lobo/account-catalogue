package com.account_catalogue.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.account_catalogue.catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.catalogue.application.services.AccountCatalogueDeleteService;
import com.account_catalogue.catalogue.application.services.AccountCatalogueValidationService;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AccountCatalogueDeleteServiceTest {
    @Mock
    private IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;
    
    @Mock
    private AccountCatalogueValidationService validationService;

    @InjectMocks
    private AccountCatalogueDeleteService accountCatalogueDeleteService;

    @DisplayName("Test eliminar cuenta por id")
    @Test
    void testdeleteById(){
        //given
        Long accountId=1L;
        String enterpriseId = "enterprise-123";
        AccountCatalogue mockAccount = new AccountCatalogue();
        mockAccount.setId(accountId);
        mockAccount.setCode("1001");
        mockAccount.setDescription("Cuenta de prueba");
        
        // Mock del validation service
        given(validationService.validateAccountExistsByIdAndEnterprise(accountId, enterpriseId))
                .willReturn(mockAccount);
        willDoNothing().given(validationService).validateAccountNotAssociatedWithTaxes(mockAccount);
        
        // Mock del output port
        willDoNothing().given(accountCatalogueDeleteOutputPort).deleteById(accountId);
        
        //when
        accountCatalogueDeleteService.deleteById(accountId, enterpriseId);
        
        //then
        verify(validationService, times(1)).validateAccountExistsByIdAndEnterprise(accountId, enterpriseId);
        verify(validationService, times(1)).validateAccountNotAssociatedWithTaxes(mockAccount);
        verify(accountCatalogueDeleteOutputPort, times(1)).deleteById(accountId);
    }

}
