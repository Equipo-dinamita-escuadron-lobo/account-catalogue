package com.account_catalogue.accounting.infraestructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.*;

@Configuration
public class TreasuryRabbitConfig {
    public static final String TREASURY_EXCHANGE="treasury.accounting.exchange";
    public static final String TREASURY_QUEUE="treasury.accounting.queue";
    public static final String TREASURY_DLX="treasury.accounting.dlx";
    public static final String TREASURY_DLQ="treasury.accounting.dlq";
    public static final String RESULT_EXCHANGE="accounting.result.exchange";
    @Bean FanoutExchange treasuryAccountingExchange(){return new FanoutExchange(TREASURY_EXCHANGE,true,false);}
    @Bean FanoutExchange treasuryAccountingDlx(){return new FanoutExchange(TREASURY_DLX,true,false);}
    @Bean Queue treasuryAccountingQueue(){return QueueBuilder.durable(TREASURY_QUEUE).deadLetterExchange(TREASURY_DLX).build();}
    @Bean Queue treasuryAccountingDlq(){return QueueBuilder.durable(TREASURY_DLQ).build();}
    @Bean Binding treasuryAccountingBinding(){return BindingBuilder.bind(treasuryAccountingQueue()).to(treasuryAccountingExchange());}
    @Bean Binding treasuryAccountingDlqBinding(){return BindingBuilder.bind(treasuryAccountingDlq()).to(treasuryAccountingDlx());}
    @Bean FanoutExchange accountingResultExchange(){return new FanoutExchange(RESULT_EXCHANGE,true,false);}
    @Bean SimpleRabbitListenerContainerFactory treasuryRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(500, 2.0, 5000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return factory;
    }
}
