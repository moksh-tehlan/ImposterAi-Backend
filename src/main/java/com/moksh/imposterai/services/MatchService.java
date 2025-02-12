package com.moksh.imposterai.services;

import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import com.moksh.imposterai.repositories.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    protected MatchEntity getMatchEntity(String matchId) throws Exception {
        return matchRepository.findById(matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
    }

    public MatchEntity saveMatch(MatchEntity matchEntity) {
        return matchRepository.save(matchEntity);
    }

    public Optional<MatchEntity> getMatchByPlayerId(String sessionId) {
        return matchRepository.findByPlayerId(sessionId);
    }

    @Transactional
    public void deleteMatchWithPlayerId(String playerId) {
        matchRepository.deleteByPlayerId(playerId);
    }

    public void deleteMatch(String id) {
        matchRepository.deleteById(id);
    }
}
