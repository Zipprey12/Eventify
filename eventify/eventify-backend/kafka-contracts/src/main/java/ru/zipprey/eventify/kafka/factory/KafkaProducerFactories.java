package ru.zipprey.eventify.kafka.factory;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.Map;

@UtilityClass
public class KafkaProducerFactories {

    public static ProducerFactory<String, Object> defaultProducerFactory(String bootstrapServers) {
        var props = Map.<String, Object>of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(props);
    }
}
