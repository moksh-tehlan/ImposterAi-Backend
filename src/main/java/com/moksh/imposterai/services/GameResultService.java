package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.requests.GameResultRequest;
import com.moksh.imposterai.dtos.response.GameResultResponse;
import com.moksh.imposterai.entities.GameResultEntity;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import com.moksh.imposterai.repositories.GameResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameResultService {
    private final GameResultRepository gameResultRepository;
    private final ModelMapper modelMapper;

    public GameResultResponse getGameResult(GameResultRequest request) throws ResourceNotFoundException {

        GameResultEntity entity = gameResultRepository.findById(request.getMatchId()).orElseThrow(() -> new ResourceNotFoundException("Game nout found"));
        return GameResultResponse.builder()
                .isCorrectAnswer(!entity.isVsBot() == request.isOpponentAHuman()).build();
    }

    public void saveGameResult(GameResultEntity gameResult) {
        gameResultRepository.save(gameResult);
    }
}
