package ru.zipprey.eventify.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import ru.zipprey.eventify.kafka.factory.KafkaConsumerFactories;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return KafkaConsumerFactories.defaultConsumerFactory(
                bootstrapServers,
                "telegram-bot",
                "ru.zipprey.eventify.kafka.booking");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        return KafkaConsumerFactories.defaultListenerContainerFactory(consumerFactory);
    }
}
