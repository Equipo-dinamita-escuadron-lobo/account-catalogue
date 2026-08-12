package com.account_catalogue.accounting.application.service;

import com.account_catalogue.accounting.application.input.IAccountBalanceUpdateInputPort;
import com.account_catalogue.accounting.application.output.*;
import com.account_catalogue.accounting.domain.enums.AccountingEntryStatus;
import com.account_catalogue.accounting.domain.models.*;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.*;
import com.account_catalogue.catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.paymentMethods.dataAccess.repository.PaymentMethodRepository;
import com.account_catalogue.bankAccounts.dataAccess.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class TreasuryAccountingService {
    private final IAccountingSearchOutputPort search; private final IAccountingEntryPersistenceOutputPort entries;
    private final IAccountBalanceUpdateInputPort balances; private final PaymentMethodRepository paymentMethods;
    private final IAccountCatalogueSearchOutputPort accounts; private final BankAccountRepository bankAccounts;

    @Transactional
    public AccountingEntry createVoucher(PaymentVoucherEventDto event) {
        var existing=search.findBySourceDocumentIdAndType(event.id(),"PAYMENT_VOUCHER");if(existing.isPresent())return existing.get();
        var method=paymentMethods.findByIdAndIdEnterprise(event.paymentMethodId(),event.enterpriseId())
                .filter(m->Boolean.TRUE.equals(m.getStatus())&&m.getAccountingAccount()!=null)
                .orElseThrow(()->new IllegalArgumentException("Método de pago inactivo o sin equivalencia contable"));
        boolean requiresBank=Boolean.TRUE.equals(method.getRequiresBankAccount());
        if(requiresBank&&event.bankAccountId()==null)throw new IllegalArgumentException("El metodo de pago exige una cuenta bancaria activa");
        if(!requiresBank&&event.bankAccountId()!=null)throw new IllegalArgumentException("El metodo de pago no admite cuenta bancaria");
        List<AccountingMovement> movements=new ArrayList<>();BigDecimal debits=BigDecimal.ZERO;
        for(var detail:event.details()){
            var account=accounts.getAccountCatalogueByCode(detail.payableAccountCode(),event.enterpriseId());
            if(account==null||!Boolean.TRUE.equals(account.getStatus())||!account.getId().equals(detail.payableAccountId()))throw new IllegalArgumentException("Cuenta por pagar inválida");
            movements.add(AccountingMovement.builder().account(account.getId()).thirdPartyId(detail.supplierId()).debit(detail.amountPaid()).credit(BigDecimal.ZERO).description("Pago factura "+detail.invoiceReference()).build());debits=debits.add(detail.amountPaid());
        }
        if(debits.compareTo(event.total())!=0)throw new IllegalArgumentException("El asiento no está balanceado con el total del comprobante");
        Long creditAccountId=method.getAccountingAccount().getId();
        if(event.bankAccountId()!=null){var bank=bankAccounts.findByIdAndIdEnterprise(event.bankAccountId(),event.enterpriseId())
                .filter(b->Boolean.TRUE.equals(b.getStatus())).orElseThrow(()->new IllegalArgumentException("Cuenta bancaria inactiva o ajena a la empresa"));creditAccountId=bank.getAccountingAccount().getId();}
        var creditAccount=accounts.getAccountCatalogueById(creditAccountId,event.enterpriseId());
        if(creditAccount==null||!Boolean.TRUE.equals(creditAccount.getStatus()))throw new IllegalArgumentException("Cuenta de caja/banco inválida");
        movements.add(AccountingMovement.builder().account(creditAccountId).thirdPartyId(null).debit(BigDecimal.ZERO).credit(event.total()).description("Salida de caja/banco "+event.voucherNumber()).build());
        AccountingEntry entry=AccountingEntry.builder().code("AE-PV-"+event.id()).date(event.issueDate()).description("Comprobante de egreso "+event.voucherNumber()).status(AccountingEntryStatus.ACTIVE).sourceDocumentId(event.id()).type("PAYMENT_VOUCHER").movements(movements).idEnterprise(event.enterpriseId()).build();
        entry=entries.save(entry);balances.updateBalancesFromAccountingEntry(entry);return entry;
    }

    @Transactional
    public AccountingEntry createWriteOff(PayableWriteOffEventDto event){
        var existing=search.findBySourceDocumentIdAndType(event.id(),"PAYABLE_WRITEOFF");if(existing.isPresent())return existing.get();
        var counterpart=accounts.getAccountCatalogueById(event.counterpartAccountId(),event.enterpriseId());
        if(counterpart==null||!Boolean.TRUE.equals(counterpart.getStatus())||!counterpart.getCode().equals(event.counterpartAccountCode()))throw new IllegalArgumentException("Cuenta contrapartida inválida");
        List<AccountingMovement> movements=new ArrayList<>();BigDecimal total=BigDecimal.ZERO;
        for(var d:event.details()){var payable=accounts.getAccountCatalogueByCode(d.payableAccountCode(),event.enterpriseId());if(payable==null||!Boolean.TRUE.equals(payable.getStatus())||!payable.getId().equals(d.payableAccountId()))throw new IllegalArgumentException("Cuenta por pagar inválida");movements.add(AccountingMovement.builder().account(payable.getId()).thirdPartyId(d.supplierId()).debit(d.amount()).credit(BigDecimal.ZERO).description("Baja de obligación "+d.invoiceId()).build());total=total.add(d.amount());}
        if(total.compareTo(event.total())!=0)throw new IllegalArgumentException("La baja no está balanceada");movements.add(AccountingMovement.builder().account(counterpart.getId()).debit(BigDecimal.ZERO).credit(total).description(event.reason()).build());
        AccountingEntry entry=AccountingEntry.builder().code("AE-PWO-"+event.id()).date(LocalDate.now()).description("Baja de cuenta por pagar").status(AccountingEntryStatus.ACTIVE).sourceDocumentId(event.id()).type("PAYABLE_WRITEOFF").movements(movements).idEnterprise(event.enterpriseId()).build();entry=entries.save(entry);balances.updateBalancesFromAccountingEntry(entry);return entry;
    }

    @Transactional public AccountingEntry voidEntry(Long id,String type){var entry=search.findBySourceDocumentIdAndType(id,type).orElseThrow(()->new IllegalArgumentException("Asiento no encontrado"));if(entry.getStatus()!=AccountingEntryStatus.VOIDED){balances.reverseBalancesFromAccountingEntry(entry);entry.voidEntry();entry=entries.save(entry);}return entry;}
}
