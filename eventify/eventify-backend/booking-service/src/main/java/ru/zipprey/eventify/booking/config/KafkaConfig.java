package ru.zipprey.eventify.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.zipprey.eventify.kafka.booking.*;
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
                "booking-service-event-overbooked",
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
            registry.register("booking.book-tickets", BookTicketsMessage.class);
            registry.register("booking.force-canceled", BookingsOutcompetedMessage.class);
            registry.register("booking.canceled-cascade", BookingsCascadeCanceledMessage.class);
            registry.register("booking.force-cancellation-error", CancellationFailedMessage.class);
            registry.register("booking.confirmed", BookingConfirmedMessage.class);
            registry.register("booking.deleted-by-admin", BookingDeletedByAdminMessage.class);
            registry.register("booking.deleted", BookingDeletedMessage.class);
        };
    }
}