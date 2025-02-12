package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.enums.MatchStatus;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import com.moksh.imposterai.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerEntity getPlayerByUserId(String userId) throws ResourceNotFoundException {
        return playerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Player Not Found"));
    }

    public Optional<PlayerEntity> getPlayerBySessionId(String sessionId){
        return playerRepository.findById(sessionId);
    }

    public PlayerEntity savePlayer(PlayerEntity playerEntity) {
        return playerRepository.save(playerEntity);
    }

    public List<PlayerEntity> getWaitingPlayer() {
        return playerRepository.getWaitingPlayer(MatchStatus.QUEUED);
    }

    public void updatePlayer(PlayerEntity playerEntity) {
        PlayerEntity updatedPlayerEntity = PlayerEntity.builder()
                .isBot(playerEntity.getIsBot())
                .user(playerEntity.getUser())
                .matchStatus(playerEntity.getMatchStatus())
                .sessionId(playerEntity.getSessionId())
                .build();
        savePlayer(updatedPlayerEntity);
    }

    public void remove(String id) {
        playerRepository.deleteById(id);
    }

    @Transactional
    public void delete(String id) {
        playerRepository.deleteById(id);
    }
}
