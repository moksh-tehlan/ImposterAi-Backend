package com.moksh.imposterai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {
    private final ChatClient.Builder chatClientBuilder;

    @Bean
    public ChatClient chatClientProvider(){
        return chatClientBuilder.build();
    }
}
