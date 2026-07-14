package ru.zipprey.eventify.booking;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ru.zipprey.eventify",
        exclude = UserDetailsServiceAutoConfiguration.class)
@EntityScan(basePackages = {"ru.zipprey.eventify.booking", "ru.zipprey.eventify.outbox"})
@EnableJpaRepositories(basePackages = {"ru.zipprey.eventify.booking", "ru.zipprey.eventify.outbox"})
@EnableScheduling
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}