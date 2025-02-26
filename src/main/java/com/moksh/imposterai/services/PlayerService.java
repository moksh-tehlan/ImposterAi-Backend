package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.enums.MatchStatus;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.exceptions.PlayerAlreadyInMatchException;
import com.moksh.imposterai.exceptions.PlayerNotFoundException;
import com.moksh.imposterai.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerEntity getPlayerBySessionId(String sessionId) {
        return playerRepository.findById(sessionId).orElseThrow(() -> new PlayerNotFoundException("Player Not Found"));
    }

    public PlayerEntity save(PlayerEntity playerEntity) {
        if (playerRepository.findById(playerEntity.getSessionId()).isPresent()) {
            throw new PlayerAlreadyInMatchException(playerEntity.getSessionId());
        }
        return playerRepository.save(playerEntity);
    }

    public List<PlayerEntity> getWaitingPlayers() {
        return playerRepository.getWaitingPlayer(MatchStatus.QUEUED);
    }

    public void updatePlayer(PlayerEntity playerEntity) {
        PlayerEntity updatedPlayerEntity = PlayerEntity.builder()
                .isBot(playerEntity.getIsBot())
                .user(playerEntity.getUser())
                .matchStatus(playerEntity.getMatchStatus())
                .sessionId(playerEntity.getSessionId())
                .build();
        playerRepository.save(updatedPlayerEntity);
    }

    public void delete(PlayerEntity playerOne) {
        playerRepository.delete(playerOne);
    }
}
