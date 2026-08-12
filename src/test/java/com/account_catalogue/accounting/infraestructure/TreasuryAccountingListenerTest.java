package com.account_catalogue.accounting.infraestructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.account_catalogue.accounting.application.service.TreasuryAccountingService;
import com.account_catalogue.accounting.domain.models.AccountingEntry;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.DTO.Pp8EventEnvelopeDto;
import com.account_catalogue.accounting.infraestructure.output.messageBroker.listener.TreasuryAccountingListener;
import com.account_catalogue.commons.config.aspect.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class TreasuryAccountingListenerTest {
    private final TreasuryAccountingService service = mock(TreasuryAccountingService.class);
    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    private final RabbitTemplate rabbit = mock(RabbitTemplate.class);
    private final JwtTokenService jwtTokenService = mock(JwtTokenService.class);
    private final TreasuryAccountingListener listener = new TreasuryAccountingListener(
            service, mapper, rabbit, jwtTokenService);

    @BeforeEach
    void setUp() {
        when(jwtTokenService.getToken()).thenReturn("propagated-user-token");
    }

    @Test
    void republishesTheSameDeterministicResultWhenAccountingAlreadyExists() throws Exception {
        when(service.createVoucher(any())).thenReturn(AccountingEntry.builder().id(44L).build());
        List<Map<String, Object>> results = new ArrayList<>();
        doAnswer(invocation -> {
            results.add(invocation.getArgument(2));
            CorrelationData correlation = invocation.getArgument(4);
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).convertAndSend(any(String.class), eq(""), any(), any(MessagePostProcessor.class), any(CorrelationData.class));

        listener.listen(message("tenant-a"));
        listener.listen(message("tenant-a"));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).get("eventId")).isEqualTo("result-source-event");
        assertThat(results.get(1).get("eventId")).isEqualTo("result-source-event");
        verify(service, org.mockito.Mockito.times(2)).createVoucher(any());
    }

    @Test
    void publisherNackPropagatesSoRabbitCanRedeliver() throws Exception {
        when(service.createVoucher(any())).thenReturn(AccountingEntry.builder().id(44L).build());
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(4);
            correlation.getFuture().complete(new CorrelationData.Confirm(false, "nack"));
            return null;
        }).when(rabbit).convertAndSend(any(String.class), eq(""), any(), any(MessagePostProcessor.class), any(CorrelationData.class));

        assertThatThrownBy(() -> listener.listen(message("tenant-a")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("nack");
    }

    @Test
    void republishUsesPropagatedHumanJwt() throws Exception {
        when(service.createVoucher(any())).thenReturn(AccountingEntry.builder().id(44L).build());
        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(3);
            MessageProperties properties = new MessageProperties();
            processor.postProcessMessage(new Message(new byte[0], properties));
            CorrelationData correlation = invocation.getArgument(4);
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).convertAndSend(any(String.class), eq(""), any(), any(MessagePostProcessor.class), any(CorrelationData.class));

        listener.listen(message("tenant-a"));

        verify(jwtTokenService).getToken();
    }

    private Message message(String tenant) throws Exception {
        var payload = mapper.valueToTree(Map.of(
                "id", 55L, "voucherNumber", "CE-55", "enterpriseId", "enterprise-a",
                "issueDate", LocalDate.of(2026, 8, 10), "status", "POSTING",
                "paymentMethodId", 8L, "total", new BigDecimal("10.00"), "tenantId", tenant,
                "details", List.of(Map.of("supplierId", 71L, "invoiceId", 501L,
                        "invoiceReference", "FC-501", "payableAccountId", 2205L,
                        "payableAccountCode", "2205", "amountPaid", new BigDecimal("10.00")))));
        var envelope = new Pp8EventEnvelopeDto("source-event", "PAYMENT_VOUCHER_CREATED", 1,
                Instant.parse("2026-08-10T12:00:00Z"), tenant, "enterprise-a", "correlation",
                new Pp8EventEnvelopeDto.SourceDocument("PAYMENT_VOUCHER", 55L), payload);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("x-tenant-id", tenant);
        properties.setHeader("x-jwt-token", "Bearer signed-treasury");
        return new Message(mapper.writeValueAsBytes(envelope), properties);
    }
}
