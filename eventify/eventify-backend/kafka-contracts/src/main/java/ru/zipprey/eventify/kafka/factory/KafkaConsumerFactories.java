package ru.zipprey.eventify.kafka.factory;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.Map;

@UtilityClass
public class KafkaConsumerFactories {

    public static ConsumerFactory<String, Object> defaultConsumerFactory(
            String bootstrapServers, String groupId, String... trustedPackages) {

        var jsonDeserializer = new JacksonJsonDeserializer<>();
        jsonDeserializer.addTrustedPackages(trustedPackages);

        var props = Map.<String, Object>of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    public static ConcurrentKafkaListenerContainerFactory<String, Object> defaultListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
