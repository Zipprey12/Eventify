package ru.zipprey.eventify.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = "ru.zipprey.eventify",
        exclude = UserDetailsServiceAutoConfiguration.class)
@EntityScan(basePackages = {"ru.zipprey.eventify.event", "ru.zipprey.eventify.outbox"})
@EnableJpaRepositories(basePackages = {"ru.zipprey.eventify.event", "ru.zipprey.eventify.outbox"})
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
