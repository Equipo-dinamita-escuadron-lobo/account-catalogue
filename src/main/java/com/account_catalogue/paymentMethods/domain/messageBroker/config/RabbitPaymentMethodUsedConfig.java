package com.account_catalogue.paymentMethods.domain.messageBroker.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@Configuration
@Slf4j
@Profile("!test")
public class RabbitPaymentMethodUsedConfig {
    
    public static final String PAYMENT_METHOD_USED_EXCHANGE = "paymentmethod.used.exchange";
    public static final String PAYMENT_METHOD_USED_QUEUE = "paymentmethod.used.queue";

    @Bean
    Queue paymentMethodUsedQueue() {
        return QueueBuilder.durable(PAYMENT_METHOD_USED_QUEUE).build();
    }

    @Bean
    FanoutExchange paymentMethodUsedExchange() {
        return new FanoutExchange(PAYMENT_METHOD_USED_EXCHANGE, true, false);
    }

    @Bean
    Binding paymentMethodUsedQueueBinding() {
        return BindingBuilder.bind(paymentMethodUsedQueue()).to(paymentMethodUsedExchange());
    }
}
