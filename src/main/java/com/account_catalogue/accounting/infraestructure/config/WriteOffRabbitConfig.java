package com.account_catalogue.accounting.infraestructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WriteOffRabbitConfig {
     public static final String WRITEOFF_EXCHANGE = "writeoff.exchange";
    public static final String WRITEOFF_ACCOUNTING_QUEUE = "writeoff.accounting.queue";
    public static final String WRITEOFF_ACCOUNTING_DLX = "writeoff.accounting.dlx";
    public static final String WRITEOFF_ACCOUNTING_DLQ = "writeoff.accounting.dlq";
    public static final String WRITEOFF_ACCOUNTING_RETRY_QUEUE = "writeoff.accounting.retry.queue";

    // --- EXCHANGES ---
    // Exchange principal para publicar los eventos de castigos.
    @Bean
    public FanoutExchange writeOffExchange() {
        return new FanoutExchange(WRITEOFF_EXCHANGE, true, false);
    }

    // Dead Letter Exchange: Recibe los mensajes que fallan en la cola principal o en la de reintentos.
    @Bean
    public FanoutExchange writeOffAccountingDlx() {
        return new FanoutExchange(WRITEOFF_ACCOUNTING_DLX, true, false);
    }

    // --- QUEUES ---
    // Cola principal que consume el WriteOffMessageListener.
    // Si un mensaje falla, se envía al DLX (writeOffAccountingDlx).
    @Bean
    public Queue writeOffAccountingQueue() {
        return QueueBuilder.durable(WRITEOFF_ACCOUNTING_QUEUE)
                .withArgument("x-dead-letter-exchange", WRITEOFF_ACCOUNTING_DLX).build();
    }

    // Dead Letter Queue: El destino final para mensajes que fallaron repetidamente.
    // Sirve para análisis manual de errores.
    @Bean
    public Queue writeOffAccountingDlq() {
        return QueueBuilder.durable(WRITEOFF_ACCOUNTING_DLQ).build();
    }

    // Cola de Reintentos: Retiene un mensaje fallido por un tiempo (TTL) antes de devolverlo
    // al DLX para que sea procesado de nuevo por la cola principal.
    @Bean
    public Queue writeOffAccountingRetryQueue() {
        return QueueBuilder.durable(WRITEOFF_ACCOUNTING_RETRY_QUEUE)
                .withArgument("x-message-ttl", 10000) // 10 segundos de espera para reintento
                .withArgument("x-dead-letter-exchange", WRITEOFF_ACCOUNTING_DLX).build();
    }

    // --- BINDINGS ---
    // Enlaza la cola principal con el exchange principal.
    @Bean
    public Binding writeOffAccountingBinding() {
        return BindingBuilder.bind(writeOffAccountingQueue()).to(writeOffExchange());
    }

    // Enlaza la DLQ con el DLX para recibir los mensajes fallidos permanentemente.
    @Bean
    public Binding writeOffAccountingDlqBinding() {
        return BindingBuilder.bind(writeOffAccountingDlq()).to(writeOffAccountingDlx());
    }

    // Enlaza la cola de reintentos con el DLX para recibir los mensajes que necesitan reintentarse.
    @Bean
    public Binding writeOffAccountingRetryBinding() {
        return BindingBuilder.bind(writeOffAccountingRetryQueue()).to(writeOffAccountingDlx());
    }
}
