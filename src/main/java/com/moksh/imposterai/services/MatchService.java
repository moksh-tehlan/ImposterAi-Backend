package com.moksh.imposterai.services;

import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.exceptions.MatchNotFoundException;
import com.moksh.imposterai.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    protected MatchEntity getMatch(String matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));
    }

    public MatchEntity save(MatchEntity matchEntity) {
        return matchRepository.save(matchEntity);
    }

    public MatchEntity findByPlayerId(String sessionId) {
        return matchRepository.findByPlayerId(sessionId).orElseThrow(() -> new MatchNotFoundException(sessionId));
    }

    public void delete(String id) {
        matchRepository.deleteById(id);
    }
}
