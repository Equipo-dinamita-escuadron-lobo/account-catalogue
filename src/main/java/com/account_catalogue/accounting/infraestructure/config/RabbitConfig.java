package com.account_catalogue.accounting.infraestructure.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;

@Configuration
@Slf4j
@Profile("!test")
public class RabbitConfig {
    //Constants for invoice
    public static final String INVOICE_EXCHANGE = "invoice.exchange";
    public static final String INVOICE_ACCOUNTING_QUEUE = "invoice.accounting.queue";
    public static final String INVOICE_ACCOUNTING_DLX = "invoice.accounting.dlx";
    public static final String INVOICE_ACCOUNTING_DLQ = "invoice.accounting.dlq";
    public static final String INVOICE_ACCOUNTING_RETRY_QUEUE = "invoice.accounting.retry.queue";

    //Jackson2JsonMessageConverter
    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // STATEMENT EXCHANGES
    // Primary and message exchange dead for third invoices
    @Bean
    FanoutExchange invoiceExchange() {
        return new FanoutExchange(INVOICE_EXCHANGE, true, false);
    }

    @Bean
    FanoutExchange invoiceAccountingDlx() {
        return new FanoutExchange(INVOICE_ACCOUNTING_DLX, true, false);
    }

    // STATEMENT OF QUEUES AND BINDINGS
    // Invoice Queues and Bindings
    @Bean
    Queue invoiceAccountingQueue() {
        return QueueBuilder.durable(INVOICE_ACCOUNTING_QUEUE)
                .withArgument("x-dead-letter-exchange", INVOICE_ACCOUNTING_DLX)
                .build();
    }

    @Bean
    Queue invoiceAccountingDlq() {
        return QueueBuilder.durable(INVOICE_ACCOUNTING_DLQ).build();
    }

    @Bean
    Queue invoiceAccountingRetryQueue() {
        return QueueBuilder.durable(INVOICE_ACCOUNTING_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000) // 1 minuto de espera para reintento
                .withArgument("x-dead-letter-exchange", INVOICE_ACCOUNTING_DLX) // Si falla después de reintento, va al DLX
                .build();
    }

    @Bean
    Binding invoiceAccountingBinding() {
        return BindingBuilder.bind(invoiceAccountingQueue()).to(invoiceExchange());
    }

    @Bean
    Binding invoiceAccountingDlqBinding() {
        return BindingBuilder.bind(invoiceAccountingDlq()).to(invoiceAccountingDlx());
    }

    @Bean
    Binding invoiceAccountingRetryBinding() {
        return BindingBuilder.bind(invoiceAccountingRetryQueue()).to(invoiceAccountingDlx());
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setMandatory(true);
        
        template.setReturnsCallback(returned -> {
            log.error("Message returned: {}", returned.getMessage());
        });

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message not delivered to exchange. Cause: {}", cause);
            }
        });
        return template;
    }

    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
