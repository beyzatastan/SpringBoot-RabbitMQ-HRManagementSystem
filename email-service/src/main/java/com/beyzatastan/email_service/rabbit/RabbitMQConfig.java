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

    // Dead Letter Queue için
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
        return new Queue(dlqName, true);
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

    // Binding: Queue to Exchange
    @Bean
    public Binding mailBinding(Queue mailQueue, TopicExchange mailExchange) {
        return BindingBuilder
                .bind(mailQueue)
                .to(mailExchange)
                .with(routingKey);
    }

    // Binding: DLQ to DLX
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with("mail.dlq.routing.key");
    }

    // JSON Message Converter
    @Bean
    public MessageConverter messageConverter() {
       return messageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}