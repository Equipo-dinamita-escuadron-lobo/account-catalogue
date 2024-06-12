package com.account_catalogue.domain.service;

import com.account_catalogue.application.output.IAccountCatalogueDeleteOutputPort;
import com.account_catalogue.domain.services.AccountCatalogueDeleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountCatalogueDeleteServiceTest {
    @Mock
    private IAccountCatalogueDeleteOutputPort accountCatalogueDeleteOutputPort;

    @InjectMocks
    private AccountCatalogueDeleteService accountCatalogueDeleteService;

    @DisplayName("Test eliminar cuenta por id")
    @Test
    void testdeleteById(){
        //given
        Long accountId=1L;
        willDoNothing().given(accountCatalogueDeleteOutputPort).deleteById(accountId);
        //when
        accountCatalogueDeleteService.deleteById(accountId);
        //then
        verify(accountCatalogueDeleteOutputPort,times(1)).deleteById(accountId);

    }

}
