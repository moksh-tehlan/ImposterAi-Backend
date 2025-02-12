package com.moksh.imposterai.controllers;

import com.moksh.imposterai.dtos.requests.GameResultRequest;
import com.moksh.imposterai.dtos.response.GameResultResponse;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import com.moksh.imposterai.services.GameResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameResultController {

    private final GameResultService gameResultService;

    @PostMapping("/result")
    public GameResultResponse getResult(@RequestBody GameResultRequest gameResultRequest) throws ResourceNotFoundException {
        return gameResultService.getGameResult(gameResultRequest);
    }
}
