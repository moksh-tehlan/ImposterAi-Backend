package com.moksh.imposterai;

import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class ImposterAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImposterAiApplication.class, args);
    }

}
