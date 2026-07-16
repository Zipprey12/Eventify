package ru.zipprey.eventify.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.zipprey.eventify.kafka.auth.UserRegisteredMessage;
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
    public ApplicationRunner registerOutboxTypes(OutboxTypeRegistry registry) {
        return args -> registry.register("user.registered", UserRegisteredMessage.class);
    }
}
