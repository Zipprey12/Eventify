package ru.zipprey.eventify.event.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.zipprey.eventify.kafka.event.EventCreatedMessage;
import ru.zipprey.eventify.kafka.event.EventDateChangedMessage;
import ru.zipprey.eventify.kafka.event.EventDeletedMessage;
import ru.zipprey.eventify.kafka.event.EventOverbookedMessage;
import ru.zipprey.eventify.kafka.factory.KafkaConsumerFactories;
import ru.zipprey.eventify.kafka.factory.KafkaProducerFactories;
import ru.zipprey.eventify.outbox.service.OutboxTypeRegistry;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return KafkaProducerFactories.defaultProducerFactory(bootstrapServers);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return KafkaConsumerFactories.defaultConsumerFactory(
                bootstrapServers,
                "event-service",
                "ru.zipprey.eventify.kafka.booking",
                "ru.zipprey.eventify.kafka.event");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        return KafkaConsumerFactories.defaultListenerContainerFactory(consumerFactory);
    }

    @Bean
    public ApplicationRunner registerOutboxTypes(OutboxTypeRegistry registry) {
        return args -> {
            registry.register("event.created", EventCreatedMessage.class);
            registry.register("event.date-changed", EventDateChangedMessage.class);
            registry.register("event.overbooked", EventOverbookedMessage.class);
            registry.register("event.deleted", EventDeletedMessage.class);
        };
    }
}