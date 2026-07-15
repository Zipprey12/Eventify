package ru.zipprey.eventify.notification.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class NotificationConfig {

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor(
            @Value("${email.executor.core-pool-size:2}") int corePoolSize,
            @Value("${email.executor.max-pool-size:16}") int maxPoolSize,
            @Value("${email.executor.queue-capacity:300}") int queueCapacity
    ) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("email-");
        executor.initialize();
        return executor;
    }

}
