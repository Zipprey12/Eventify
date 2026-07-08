package ru.zipprey.eventify.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.zipprey.eventify")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
