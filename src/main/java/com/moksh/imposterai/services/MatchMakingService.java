package com.moksh.imposterai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.enums.MatchState;
import com.moksh.imposterai.dtos.enums.MatchStatus;
import com.moksh.imposterai.dtos.enums.SocketActions;
import com.moksh.imposterai.dtos.response.*;
import com.moksh.imposterai.entities.GameResultEntity;
import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchMakingService {
    private final MatchService matchService;
    private final PlayerService playerService;
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final UserServices userServices;
    private final ConcurrentHashMap<String, ScheduledExecutorService> matchSchedulers = new ConcurrentHashMap<>();
    private final GameResultService gameResultService;
    private final Random random = new Random();

    public void handleFindMatch(String sessionId) throws Exception {
        UserEntity user = sessionService.getUser(sessionId);
        PlayerEntity currentPlayer = initializePlayer(sessionId, user);

        if (!isPlayerEligible(currentPlayer)) return;
        if (playerService.getWaitingPlayer().size() <= 1) return;

        PlayerEntity opponent = createOpponent(playerService.getWaitingPlayer().get(0));
        MatchEntity match = setupMatch(currentPlayer, opponent);

        notifyPlayers(match);
    }

    private PlayerEntity initializePlayer(String sessionId, UserEntity user) {
        return playerService.savePlayer(PlayerEntity.builder()
                .sessionId(sessionId)
                .user(user)
                .matchStatus(MatchStatus.QUEUED)
                .build());
    }

    private PlayerEntity createOpponent(PlayerEntity waitingPlayer) throws ResourceNotFoundException {
        boolean vsBot = false;
        if (vsBot) {
            UserEntity botUser = userServices.getBot();
            PlayerEntity playerEntity = PlayerEntity.builder()
                    .sessionId(UUID.randomUUID().toString())
                    .user(botUser)
                    .isBot(true)
                    .matchStatus(MatchStatus.BOT)
                    .build();
            return playerService.savePlayer(playerEntity);
        }
        return waitingPlayer;
    }

    private MatchEntity setupMatch(PlayerEntity currentPlayer, PlayerEntity opponent) {
        updatePlayerStatuses(currentPlayer, opponent);
        boolean isPlayerOneTurn = random.nextBoolean();

        return matchService.saveMatch(MatchEntity.builder()
                .playerOne(currentPlayer)
                .playerTwo(opponent)
                .currentTyperId(isPlayerOneTurn ? currentPlayer.getUser().getId() : opponent.getUser().getId())
                .matchState(MatchState.IN_PROGRESS)
                .build());
    }

    private void updatePlayerStatuses(PlayerEntity currentPlayer, PlayerEntity opponent) {
        boolean vsBot = opponent.getIsBot();
        currentPlayer.setMatchStatus(MatchStatus.IN_MATCH);
        playerService.updatePlayer(currentPlayer);

        if (!vsBot) {
            opponent.setMatchStatus(MatchStatus.IN_MATCH);
            playerService.updatePlayer(opponent);
        }
    }

    private void notifyPlayers(MatchEntity match) throws IOException {
        PlayerEntity playerOne = match.getPlayerOne();
        PlayerEntity playerTwo = match.getPlayerTwo();


        WebSocketSession playerOneSession = sessionService.getSession(playerOne.getSessionId());

        sendMatchFoundMessage(playerOneSession, match.getId(), match.getCurrentTyperId(), playerTwo.getUser());

        if (!playerTwo.getIsBot()) {
            WebSocketSession playerTwoSession = sessionService.getSession(playerTwo.getSessionId());
            sendMatchFoundMessage(playerTwoSession, match.getId(), match.getCurrentTyperId(), playerOne.getUser());
        }

        startGameTimer(match.getId());
    }

    private void sendMatchFoundMessage(WebSocketSession session, String matchId,
                                       String currentPlayerId,
                                       UserEntity opponent) throws IOException {
        MatchResponse response = new MatchResponse(matchId, currentPlayerId, modelMapper.map(opponent, UserDto.class));
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                new WsMessage<>(SocketActions.MATCH_FOUND, response))));
    }

    private void startGameTimer(String matchId) {
        AtomicInteger timeLeft = new AtomicInteger(10);
        ScheduledExecutorService matchScheduler = Executors.newSingleThreadScheduledExecutor();
        matchSchedulers.put(matchId, matchScheduler);

        matchScheduler.scheduleAtFixedRate(() -> {
            try {
                if (timeLeft.get() < 0) {
                    gameOver(matchId);
                    stopMatchScheduler(matchId);
                    return;
                }
                sendTimeUpdate(matchId, timeLeft.get());
            } catch (Exception e) {
                stopMatchScheduler(matchId);
                throw new RuntimeException(e);
            }
            timeLeft.decrementAndGet();
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopMatchScheduler(String matchId) {
        ScheduledExecutorService scheduler = matchSchedulers.remove(matchId);
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // Wait for tasks to complete with a timeout
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void sendTimeUpdate(String matchId, int i) throws Exception {
        MatchEntity match = matchService.getMatchEntity(matchId);
        PlayerEntity playerOne = match.getPlayerOne();
        PlayerEntity playerTwo = match.getPlayerTwo();
        WsMessage<TimerResponse> timeLeft = WsMessage.<TimerResponse>builder()
                .action(SocketActions.TIMER)
                .data(new TimerResponse(i)).build();

        WebSocketSession playerOneSession = sessionService.getSession(playerOne.getSessionId());
        if (!playerTwo.getIsBot()) {
            WebSocketSession playerTwoSession = sessionService.getSession(playerTwo.getSessionId());
            playerTwoSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(timeLeft)));
        }
        playerOneSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(timeLeft)));
    }

    private void gameOver(String matchId) throws Exception {
        MatchEntity match = matchService.getMatchEntity(matchId);
        PlayerEntity playerOne = match.getPlayerOne();
        PlayerEntity playerTwo = match.getPlayerTwo();

        WsMessage<GameOverResponse> gameOverResponse = WsMessage.<GameOverResponse>builder()
                .action(SocketActions.GAME_OVER)
                .data(new GameOverResponse("Game over")).build();

        WebSocketSession playerOneSession = sessionService.getSession(playerOne.getSessionId());
        WebSocketSession playerTwoSession = sessionService.getSession(playerTwo.getSessionId());

        if (!playerTwo.getIsBot()) {
            playerTwoSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(gameOverResponse)));
        }

        playerOneSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(gameOverResponse)));
        GameResultEntity gameResult = GameResultEntity.builder()
                .matchId(matchId)
                .vsBot(playerTwo.getIsBot())
                .build();

        gameResultService.saveGameResult(gameResult);
        matchService.deleteMatch(matchId);

        playerService.delete(playerOne.getSessionId());
        playerService.delete(playerTwo.getSessionId());
        playerOneSession.close();
        if (!playerTwo.getIsBot()) {
            playerTwoSession.close();
        }
    }

    private boolean isPlayerEligible(PlayerEntity player) throws Exception {
        if (player.getMatchStatus() == MatchStatus.IN_MATCH) {
            throw new Exception("User already is in matchmaking");
        }
        if (player.getMatchStatus() == MatchStatus.OFFLINE) {
            throw new Exception("User is offline");
        }
        return true;
    }

    public void connectionClosed(WebSocketSession session) throws Exception {
        Optional<PlayerEntity> optionalPlayerEntity = playerService.getPlayerBySessionId(session.getId());
        if (optionalPlayerEntity.isEmpty()) return;

        PlayerEntity player = optionalPlayerEntity.get();

        Optional<MatchEntity> optionalMatchEntity = matchService.getMatchByPlayerId(player.getSessionId());
        if (optionalMatchEntity.isPresent()) {
            MatchEntity match = optionalMatchEntity.get();
            stopMatchScheduler(match.getId());

            PlayerEntity opponent = match.getOpponent(player.getSessionId()).orElseThrow(() -> new ResourceNotFoundException("Opponent not found"));
            WebSocketSession opponentSession = sessionService.getSession(opponent.getSessionId());

            if (!opponent.getIsBot()) {
                WsMessage<MatchAbandonedResponse> response = WsMessage.<MatchAbandonedResponse>builder()
                        .action(SocketActions.PLAYER_LEFT)
                        .data(new MatchAbandonedResponse("Opponent has left the match"))
                        .build();
                opponentSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            }

            matchService.deleteMatch(match.getId());
            if (!opponent.getIsBot()) {
                opponentSession.close();
            }
            playerService.delete(opponent.getSessionId());
        }
        playerService.delete(player.getSessionId());
    }
}
