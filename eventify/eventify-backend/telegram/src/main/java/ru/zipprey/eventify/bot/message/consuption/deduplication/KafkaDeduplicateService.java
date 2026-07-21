package ru.zipprey.eventify.bot.message.consuption.deduplication;

public interface KafkaDeduplicateService {

    boolean isDuplicate(String topic, Object key);

}
