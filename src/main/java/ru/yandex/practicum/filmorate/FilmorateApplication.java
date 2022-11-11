package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FilmorateApplication.class, args);
    }
}
