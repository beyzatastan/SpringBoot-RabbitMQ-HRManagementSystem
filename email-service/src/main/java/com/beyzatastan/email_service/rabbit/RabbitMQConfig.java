package com.beyzatastan.email_service.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name:mailQueue}")
    private String queueName;

    @Value("${rabbitmq.exchange.name:mailExchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:mail.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.dlq.name:mailDLQ}")
    private String dlqName;

    @Value("${rabbitmq.dlx.name:mailDLX}")
    private String dlxName;

    // Main Queue
    @Bean
    public Queue mailQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", "mail.dlq.routing.key")
                .build();
    }

    // Dead Letter Queue
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    // Main Exchange
    @Bean
    public TopicExchange mailExchange() {
        return new TopicExchange(exchangeName);
    }

    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxName);
    }

    // Binding
    @Bean
    public Binding mailBinding() {
        return BindingBuilder
                .bind(mailQueue())
                .to(mailExchange())
                .with(routingKey);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("mail.dlq.routing.key");
    }

    // JSON Converter

}
