package com.moksh.imposterai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SessionConfig {
    @Bean
    public ConcurrentHashMap<String, WebSocketSession> sessionRegistry() {
        return new ConcurrentHashMap<>();
    }
}
