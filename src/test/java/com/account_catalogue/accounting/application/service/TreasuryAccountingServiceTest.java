package com.account_catalogue.accounting.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.account_catalogue.accounting.application.input.IAccountBalanceUpdateInputPort;
import com.account_catalogue.accounting.application.output.IAccountingEntryPersistenceOutputPort;
import com.account_catalogue.accounting.application.output.IAccountingSearchOutputPort;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PaymentVoucherEventDto;
import com.account_catalogue.bankAccounts.dataAccess.repository.BankAccountRepository;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.paymentMethods.dataAccess.entity.PaymentMethodEntity;
import com.account_catalogue.paymentMethods.dataAccess.repository.PaymentMethodRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class TreasuryAccountingServiceTest {
    private final IAccountingSearchOutputPort search = mock(IAccountingSearchOutputPort.class);
    private final IAccountingEntryPersistenceOutputPort entries = mock(IAccountingEntryPersistenceOutputPort.class);
    private final IAccountBalanceUpdateInputPort balances = mock(IAccountBalanceUpdateInputPort.class);
    private final PaymentMethodRepository methods = mock(PaymentMethodRepository.class);
    private final IAccountCatalogueSearchOutputPort accounts = mock(IAccountCatalogueSearchOutputPort.class);
    private final BankAccountRepository banks = mock(BankAccountRepository.class);
    private final TreasuryAccountingService service =
            new TreasuryAccountingService(search, entries, balances, methods, accounts, banks);

    @BeforeEach
    void defaults() {
        when(search.findBySourceDocumentIdAndType(55L, "PAYMENT_VOUCHER"))
                .thenReturn(Optional.empty());
        AccountCatalogueEntity cashEntity = AccountCatalogueEntity.builder().id(1105L).code("1105").build();
        when(methods.findByIdAndIdEnterprise(8L, "enterprise-a")).thenReturn(Optional.of(
                PaymentMethodEntity.builder().id(8L).status(true).idEnterprise("enterprise-a")
                        .accountingAccount(cashEntity).build()));
        when(accounts.getAccountCatalogueByCode("2205", "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(2205L).code("2205").status(true).build());
        when(accounts.getAccountCatalogueById(1105L, "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(1105L).code("1105").status(true).build());
        when(entries.save(any())).thenAnswer(invocation -> {
            AccountingEntry value = invocation.getArgument(0);
            value.setId(900L);
            return value;
        });
    }

    @Test
    void createsBalancedMultiSupplierEntryAndUpdatesBalances() {
        AccountingEntry result = service.createVoucher(event(new BigDecimal("100.00")));

        assertThat(result.getId()).isEqualTo(900L);
        assertThat(result.getMovements()).hasSize(3);
        assertThat(result.getMovements().stream().map(m -> m.getDebit())
                .reduce(BigDecimal.ZERO, BigDecimal::add)).isEqualByComparingTo("100.00");
        assertThat(result.getMovements().stream().map(m -> m.getCredit())
                .reduce(BigDecimal.ZERO, BigDecimal::add)).isEqualByComparingTo("100.00");
        assertThat(result.getMovements().get(0).getThirdPartyId()).isEqualTo(71L);
        assertThat(result.getMovements().get(1).getThirdPartyId()).isEqualTo(72L);
        verify(balances).updateBalancesFromAccountingEntry(result);
    }

    @Test
    void rejectsUnbalancedVoucherWithoutPersisting() {
        assertThatThrownBy(() -> service.createVoucher(event(new BigDecimal("101.00"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("balanceado");
        verify(entries, never()).save(any());
    }

    @Test
    void returnsExistingEntryForRepeatedSourceDocument() {
        AccountingEntry existing = AccountingEntry.builder().id(44L).build();
        when(search.findBySourceDocumentIdAndType(55L, "PAYMENT_VOUCHER"))
                .thenReturn(Optional.of(existing));

        assertThat(service.createVoucher(event(new BigDecimal("100.00")))).isSameAs(existing);
        verify(entries, never()).save(any());
    }

    @Test
    void resolvesPayableAccountByIdWhenCodeIsNumericId() {
        when(accounts.getAccountCatalogueByCode("5219", "enterprise-a")).thenReturn(null);
        when(accounts.getAccountCatalogueById(5219L, "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(5219L).code("22050501").status(true).build());

        AccountingEntry result = service.createVoucher(new PaymentVoucherEventDto(56L, "CE-56", "enterprise-a",
                LocalDate.of(2026, 8, 13), "POSTING", 8L, null, new BigDecimal("100.00"), null,
                "tenant-a",
                List.of(new PaymentVoucherEventDto.Detail(71L, 501L, "FC-501", 5219L,
                        "5219", new BigDecimal("100.00")))));

        assertThat(result.getMovements()).hasSize(2);
        assertThat(result.getMovements().get(0).getAccount()).isEqualTo(5219L);
    }

    private PaymentVoucherEventDto event(BigDecimal total) {
        return new PaymentVoucherEventDto(55L, "CE-55", "enterprise-a",
                LocalDate.of(2026, 8, 10), "POSTING", 8L, null, total, null,
                "tenant-a",
                List.of(
                        new PaymentVoucherEventDto.Detail(71L, 501L, "FC-501", 2205L,
                                "2205", new BigDecimal("40.00")),
                        new PaymentVoucherEventDto.Detail(72L, 502L, "FC-502", 2205L,
                                "2205", new BigDecimal("60.00"))));
    }
}
