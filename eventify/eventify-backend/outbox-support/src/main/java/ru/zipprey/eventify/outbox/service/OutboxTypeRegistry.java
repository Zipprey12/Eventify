package ru.zipprey.eventify.outbox.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OutboxTypeRegistry {

    private final Map<String, Class<?>> aliasToClass = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> classToAlias = new ConcurrentHashMap<>();

    public void register(String alias, Class<?> type) {
        aliasToClass.put(alias, type);
        classToAlias.put(type, alias);
    }

    public Class<?> resolve(String alias) {
        var type = aliasToClass.get(alias);
        if (type == null) {
            throw new IllegalStateException("Неизвестный alias outbox-события: " + alias);
        }
        return type;
    }

    public String aliasFor(Class<?> type) {
        var alias = classToAlias.get(type);
        if (alias == null) {
            throw new IllegalStateException("Класс не зарегистрирован в OutboxTypeRegistry: " + type.getName());
        }
        return alias;
    }
}
