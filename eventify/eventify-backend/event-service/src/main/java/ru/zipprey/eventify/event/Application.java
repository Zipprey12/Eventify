package ru.zipprey.eventify.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "ru.zipprey.eventify",
        exclude = UserDetailsServiceAutoConfiguration.class)

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
