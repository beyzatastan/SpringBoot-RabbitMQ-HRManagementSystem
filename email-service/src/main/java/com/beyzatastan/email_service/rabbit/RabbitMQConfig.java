package com.beyzatastan.email_service.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bu sınıf RabbitMQ'nun temel yapı taşlarını oluşturur:
 * - Queue (Kuyruk): Mesajların saklandığı yer
 * - Exchange (Değiştirici): Mesajları hangi kuyruğa yönlendireceğine karar veren router
 * - Binding (Bağlama): Queue ile Exchange arasındaki ilişki
 * - Dead Letter Queue: Hatalı mesajların gittiği özel kuyruk
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Ana kuyruk adı
     * Email mesajlarının bekleyeceği kuyruk
     * Defalt: mailQueue
     */
    @Value("${rabbitmq.queue.name:mailQueue}")
    private String queueName;

    /**
     * Ana exchange adı
     * Gelen mesajları kuyruklara yönlendiren yapı
     * Default: mailExchange
     */
    @Value("${rabbitmq.exchange.name:mailExchange}")
    private String exchangeName;

    /**
     * Routing key
     * Exchange'in mesajı hangi kuyruğa göndereceğini belirleyen anahtar
     * Default: mail.routing.key
     */
    @Value("${rabbitmq.routing.key:mail.routing.key}")
    private String routingKey;

    /**
     * Dead Letter Queue adı
     * Hatalı veya işlenemeyen mesajların gittiği kuyruk
     * Defalt: mailDLQ
     */
    @Value("${rabbitmq.dlq.name:mailDLQ}")
    private String dlqName;

    /**
     * Dead Letter Exchange adı
     * Hatalı mesajları DLQ'ya yönlendiren exchange
     * Default: mailDLX
     */
    @Value("${rabbitmq.dlx.name:mailDLX}")
    private String dlxName;


    /**
     * Ana Mail Kuyruğu
     *
     * Bu kuyruk:
     * - Email mesajlarını tutar
     * - Durable: Sunucu restart olsa bile mesajlar kaybolmaz
     * - Dead Letter Queue tanımlı: Hatalı mesajlar DLQ'ya gider
     *
     * x-dead-letter-exchange: Hata durumunda mesajın gideceği exchange
     * x-dead-letter-routing-key: DLX'e gönderilirken kullanılacak routing key
     */
    @Bean
    public Queue mailQueue() {
        return QueueBuilder.durable(queueName)  // Kalıcı kuyruk oluştur
                .withArgument("x-dead-letter-exchange", dlxName)  // Hata durumunda DLX'e gönder
                .withArgument("x-dead-letter-routing-key", "mail.dlq.routing.key")  // DLX routing key
                .build();
    }

    /**
     * Dead Letter Queue (DLQ)
     *
     * Bu kuyruk:
     * - İşlenemeyen, hata veren mesajları tutar
     * - Mesajlar burada manuel olarak incelenebilir
     * - Retry mekanizması için kullanılabilir
     * - Durable: Mesajlar kaybolmaz
     *
     * Örnek senaryolar:
     * - Email gönderimi 3 kez başarısız oldu → DLQ'ya gider
     * - Format hatası olan mesaj → DLQ'ya gider
     * - Timeout olan işlem → DLQ'ya gider
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    /**
     * Ana Topic Exchange
     *
     * Topic Exchange:
     * - Routing key pattern'leri kullanarak mesajları yönlendirir
     * - Wildcard destekler (* ve #)
     * - Örnek: "mail.welcome.*", "mail.#"
     *
     * Bu exchange'e gelen mesajlar routing key'e göre ilgili kuyruklara gider
     * Örnek: mail.routing.key → mailQueue
     */
    @Bean
    public TopicExchange mailExchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Dead Letter Exchange (DLX)
     *
     * Direct Exchange:
     * - Exact routing key match ile çalışır
     * - Hatalı mesajları DLQ'ya yönlendirir
     *
     * Ana kuyruktan reject edilen mesajlar buraya düşer
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxName);
    }

    /**
     * Ana Mail Binding
     *
     * Bu binding:
     * - mailExchange'den gelen mesajları mailQueue'ya bağlar
     * - Sadece belirtilen routing key'e sahip mesajları alır
     *
     * Akış:
     * Producer → mailExchange (routing: mail.routing.key) → mailQueue → Consumer
     */
    @Bean
    public Binding mailBinding() {
        return BindingBuilder
                .bind(mailQueue())           // mailQueue'yu
                .to(mailExchange())          // mailExchange'e bağla
                .with(routingKey);           // Bu routing key ile
    }

    /**
     * Dead Letter Binding
     *
     * Bu binding:
     * - deadLetterExchange'den gelen mesajları deadLetterQueue'ya bağlar
     * - Ana kuyruktan reject edilen mesajlar buraya düşer
     *
     * Akış:
     * mailQueue (hata) → deadLetterExchange → deadLetterQueue
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())     // DLQ'yu
                .to(deadLetterExchange())    // DLX'e bağla
                .with("mail.dlq.routing.key"); // Bu routing key ile
    }

    /**
     * JSON Message Converter
     * Bu converter:
     * - Java nesnelerini JSON'a çevirir (Producer tarafında)
     * - JSON'u Java nesnesine çevirir (Consumer tarafında)
     * - EmailMessage DTO'su otomatik serialize/deserialize olur
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate Bean
     * RabbitTemplate:
     * - RabbitMQ'ya mesaj göndermek için kullanılır
     * - Producer tarafında kullanılır
     * - MessageConverter ile JSON dönüşümü yapar
     * Kullanım:
     * rabbitTemplate.convertAndSend(exchange, routingKey, message);
     * Bu method:
     * 1. message nesnesini JSON'a çevirir (messageConverter ile)
     * 2. Belirtilen exchange'e gönderir
     * 3. Routing key ile ilgili queue'ya yönlendirir
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    //mail işlemi fail olduğunda retryı önlüyor
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}