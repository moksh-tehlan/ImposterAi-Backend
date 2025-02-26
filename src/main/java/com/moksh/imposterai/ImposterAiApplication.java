package com.moksh.imposterai;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImposterAiApplication {
    public static void main(String[] args) {
        String profile = System.getProperty("spring.profiles.active", "dev");
        Dotenv dotenv = Dotenv.configure()
                .filename("." + profile + "_env")
                .load();

        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        SpringApplication.run(ImposterAiApplication.class, args);
    }
}
