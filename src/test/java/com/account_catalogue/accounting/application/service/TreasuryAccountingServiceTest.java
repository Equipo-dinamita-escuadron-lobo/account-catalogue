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
import com.account_catalogue.accounting.domain.enums.AccountingEntryStatus;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.domain.models.AccountingMovement;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PayableWriteOffEventDto;
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

    @Test
    void createWriteOffUsesPayableDebitAndCounterpartCredit() {
        when(search.findBySourceDocumentIdAndType(12L, "PAYABLE_WRITEOFF")).thenReturn(Optional.empty());
        when(accounts.getAccountCatalogueById(4295L, "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(4295L).code("429501").status(true).build());
        when(accounts.getAccountCatalogueByCode("220501", "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(2205L).code("220501").status(true).build());

        var event = new com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PayableWriteOffEventDto(
                12L, "enterprise-a", "condonacion", 4295L, "429501", new BigDecimal("200.00"), "tenant-a",
                List.of(new com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.PayableWriteOffEventDto.Detail(
                        7L, 11L, 2205L, "220501", new BigDecimal("200.00"))));

        AccountingEntry result = service.createWriteOff(event);

        assertThat(result.getMovements()).hasSize(2);
        assertThat(result.getMovements().get(0).getAccount()).isEqualTo(2205L);
        assertThat(result.getMovements().get(0).getDebit()).isEqualByComparingTo("200.00");
        assertThat(result.getMovements().get(0).getCredit()).isZero();
        assertThat(result.getMovements().get(1).getAccount()).isEqualTo(4295L);
        assertThat(result.getMovements().get(1).getCredit()).isEqualByComparingTo("200.00");
        assertThat(result.getMovements().stream().map(m -> m.getDebit())
                .reduce(BigDecimal.ZERO, BigDecimal::add)).isEqualByComparingTo("200.00");
        assertThat(result.getMovements().stream().map(m -> m.getCredit())
                .reduce(BigDecimal.ZERO, BigDecimal::add)).isEqualByComparingTo("200.00");
    }

    @Test
    void paymentDebitsObligationAccountAndCreditsConfiguredPaymentAccount() {
        AccountingEntry result = service.createVoucher(new PaymentVoucherEventDto(57L, "CE-57", "enterprise-a",
                LocalDate.of(2026, 8, 18), "POSTING", 8L, null, new BigDecimal("100000"), null,
                "tenant-a", List.of(new PaymentVoucherEventDto.Detail(71L, 501L, "FC-501", 2205L,
                        "2205", new BigDecimal("100000")))));

        assertThat(result.getMovements()).hasSize(2);
        assertThat(result.getMovements().get(0).getAccount()).isEqualTo(2205L);
        assertThat(result.getMovements().get(0).getDebit()).isEqualByComparingTo("100000");
        assertThat(result.getMovements().get(0).getCredit()).isZero();
        assertThat(result.getMovements().get(1).getAccount()).isEqualTo(1105L);
        assertThat(result.getMovements().get(1).getDebit()).isZero();
        assertThat(result.getMovements().get(1).getCredit()).isEqualByComparingTo("100000");
    }

    @Test
    void writeOffResolvesPayableByIdAndCreditsCounterpart() {
        when(search.findBySourceDocumentIdAndType(58L, "PAYABLE_WRITEOFF")).thenReturn(Optional.empty());
        when(accounts.getAccountCatalogueById(4295L, "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(4295L).code("429501").status(true).build());
        when(accounts.getAccountCatalogueByCode("5219", "enterprise-a")).thenReturn(null);
        when(accounts.getAccountCatalogueById(5219L, "enterprise-a"))
                .thenReturn(AccountCatalogue.builder().id(5219L).code("22050501").status(true).build());

        AccountingEntry result = service.createWriteOff(new PayableWriteOffEventDto(58L, "enterprise-a",
                "Condonación", 4295L, "429501", new BigDecimal("100000"), "tenant-a",
                List.of(new PayableWriteOffEventDto.Detail(71L, 501L, 5219L, "5219",
                        new BigDecimal("100000")))));

        assertThat(result.getMovements()).hasSize(2);
        assertThat(result.getMovements().get(0).getAccount()).isEqualTo(5219L);
        assertThat(result.getMovements().get(0).getDebit()).isEqualByComparingTo("100000");
        assertThat(result.getMovements().get(1).getAccount()).isEqualTo(4295L);
        assertThat(result.getMovements().get(1).getCredit()).isEqualByComparingTo("100000");
        verify(methods, never()).findByIdAndIdEnterprise(any(), any());
        verify(banks, never()).findByIdAndIdEnterprise(any(), any());
    }

    @Test
    void voidKeepsOriginalAccountsAndAmountsForHistoricalQuery() {
        AccountingEntry original = AccountingEntry.builder()
                .id(59L)
                .status(AccountingEntryStatus.ACTIVE)
                .movements(List.of(
                        AccountingMovement.builder().account(2205L).debit(new BigDecimal("100000"))
                                .credit(BigDecimal.ZERO).build(),
                        AccountingMovement.builder().account(4295L).debit(BigDecimal.ZERO)
                                .credit(new BigDecimal("100000")).build()))
                .build();
        when(search.findBySourceDocumentIdAndType(58L, "PAYABLE_WRITEOFF")).thenReturn(Optional.of(original));

        AccountingEntry result = service.voidEntry(58L, "PAYABLE_WRITEOFF");

        assertThat(result.getStatus()).isEqualTo(AccountingEntryStatus.VOIDED);
        assertThat(result.getMovements()).extracting(AccountingMovement::getAccount)
                .containsExactly(2205L, 4295L);
        assertThat(result.getMovements().get(0).getDebit()).isEqualByComparingTo("100000");
        assertThat(result.getMovements().get(1).getCredit()).isEqualByComparingTo("100000");
        verify(balances).reverseBalancesFromAccountingEntry(original);
        verify(entries).save(original);
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
