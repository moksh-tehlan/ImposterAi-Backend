package com.moksh.imposterai.dtos;

import com.moksh.imposterai.dtos.enums.MatchState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameMatch {
    String matchId;
    PlayerSession player1;
    PlayerSession player2;
    MatchState matchState;

    public Optional<PlayerSession> getOpponent(String sessionId) {
        if (player1.getSessionId().equals(sessionId)) return Optional.of(player2);
        if (player2.getSessionId().equals(sessionId)) return Optional.of(player1);
        return Optional.empty();
    }
}
