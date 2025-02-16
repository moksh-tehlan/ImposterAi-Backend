package com.moksh.imposterai.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "game.matchmaking")
@Validated
@Data
public class MatchmakingProperties {
    /**
     * Probability of matching with a bot (0.0 to 1.0)
     */
    @NotNull
    @Min(0)
    @Max(1)
    private Double botMatchProbability = 0.3;

    /**
     * Maximum time to wait for a match in seconds
     */
    @NotNull
    @Min(10)
    @Max(300)
    private Integer maxWaitTimeSeconds = 30;

    /**
     * Whether to enable bot matching
     */
    @NotNull
    private Boolean botMatchingEnabled = true;

    /**
     * Maximum number of concurrent matches
     */
    @NotNull
    @Min(1)
    @Max(10000)
    private Integer maxConcurrentMatches = 1000;

    /**
     * Total duration of the game
     */
    @NotNull
    @Min(0)
    @Max(240)
    private Integer gameDuration = 120;
}