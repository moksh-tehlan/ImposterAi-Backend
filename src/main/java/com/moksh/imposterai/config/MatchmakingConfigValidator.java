package com.moksh.imposterai.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchmakingConfigValidator {
    private final MatchmakingProperties properties;

    @PostConstruct
    public void validateConfiguration() {
        log.info("Validating matchmaking configuration...");

        if (properties.getBotMatchProbability() < 0 ||
                properties.getBotMatchProbability() > 1) {
            throw new IllegalStateException(
                    "Bot match probability must be between 0 and 1, current value: " +
                            properties.getBotMatchProbability()
            );
        }

        if (properties.getMaxWaitTimeSeconds() < 10 ||
                properties.getMaxWaitTimeSeconds() > 300) {
            throw new IllegalStateException(
                    "Max wait time must be between 10 and 300 seconds, current value: " +
                            properties.getMaxWaitTimeSeconds()
            );
        }

        log.info("Matchmaking configuration validated successfully");
    }
}