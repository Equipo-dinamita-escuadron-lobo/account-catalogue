package com.account_catalogue.catalogue.infraestructure.config.rabbitConfig;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;


@Configuration
public class RabbitAccountCatalogueConfig {
    
    public static final String ACCOUNT_USED_EXCHANGE = "account.used.exchange";
    public static final String ACCOUNT_USED_QUEUE = "account.used.queue";

    @Bean
    FanoutExchange accountUsedExchange() {
        return new FanoutExchange(ACCOUNT_USED_EXCHANGE, true, false);
    }

    @Bean
    Queue accountUsedQueue() {
        return QueueBuilder.durable(ACCOUNT_USED_QUEUE).build();
    }

    @Bean
    Binding accountUsedQueueBinding() {
        return BindingBuilder.bind(accountUsedQueue()).to(accountUsedExchange());
    }
    
}
