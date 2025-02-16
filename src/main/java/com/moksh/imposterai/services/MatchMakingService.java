package com.moksh.imposterai.services;

import com.moksh.imposterai.config.MatchmakingProperties;
import com.moksh.imposterai.dtos.MatchFoundData;
import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.enums.MatchState;
import com.moksh.imposterai.dtos.enums.MatchStatus;
import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.exceptions.MatchmakingException;
import com.moksh.imposterai.exceptions.PlayerAlreadyInMatchException;
import com.moksh.imposterai.exceptions.PlayerNotFoundException;
import com.moksh.imposterai.exceptions.PlayerOfflineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchMakingService {
    private final PlayerService playerService;
    private final MatchService matchService;
    private final GameTimerService timerService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final SessionService sessionService;
    private final ModelMapper modelMapper;
    private final MatchmakingProperties matchmakingProperties;
    private final Random random = new Random();

    public void findMatch(String sessionId) throws MatchmakingException {
        PlayerEntity player = initializePlayer(sessionId);
        validatePlayerEligibility(player);

        List<PlayerEntity> waitingPlayers = playerService.getWaitingPlayers();
        if (waitingPlayers.size() <= 1) {
            return;
        }

        PlayerEntity opponent = createOpponent(waitingPlayers.get(0));
        MatchEntity match = createMatch(player, opponent);

        notifyMatchParticipants(match);
        timerService.startGameTimer(match.getId());
    }

    public void handleMatchAbandoned(String sessionId) throws MatchmakingException {

        MatchEntity match = matchService.findByPlayerId(sessionId);

        timerService.stopTimer(match.getId());

        PlayerEntity opponent = match.getOpponent(sessionId)
                .orElseThrow(() -> new PlayerNotFoundException("Opponent not found"));

        if (!opponent.getIsBot()) {
            notificationService.sendMatchAbandoned(opponent.getSessionId());
        }
        cleanupMatch(match);
    }

    private PlayerEntity initializePlayer(String sessionId) {
        UserEntity user = sessionService.getUser(sessionId);
        return playerService.save(PlayerEntity.builder()
                .sessionId(sessionId)
                .user(user)
                .matchStatus(MatchStatus.QUEUED)
                .build());
    }

    private void validatePlayerEligibility(PlayerEntity player) throws MatchmakingException {
        if (player.getMatchStatus() == MatchStatus.IN_MATCH) {
            throw new PlayerAlreadyInMatchException(player.getSessionId());
        }
        if (player.getMatchStatus() == MatchStatus.OFFLINE) {
            throw new PlayerOfflineException(player.getSessionId());
        }
    }

    private MatchEntity createMatch(PlayerEntity player1, PlayerEntity player2) {
        updatePlayerStatuses(player1, player2);
        return matchService.save(MatchEntity.builder()
                .playerOne(player1)
                .playerTwo(player2)
                .currentTyperId(determineFirstPlayer(player1, player2))
                .matchState(MatchState.IN_PROGRESS)
                .build());
    }

    private String determineFirstPlayer(PlayerEntity player1, PlayerEntity player2) {
        return new Random().nextBoolean() ?
                player1.getUser().getId() :
                player2.getUser().getId();
    }

    private PlayerEntity createOpponent(PlayerEntity waitingPlayer) throws MatchmakingException {
        try {
            // Check if we should create a bot opponent (can be configurable)
            boolean shouldCreateBot = matchmakingProperties.getBotMatchingEnabled() && random.nextDouble() < matchmakingProperties.getBotMatchProbability();

            if (shouldCreateBot) {
                UserEntity botUser = userService.getBot();
                return playerService.save(PlayerEntity.builder()
                        .sessionId(UUID.randomUUID().toString())
                        .user(botUser)
                        .isBot(true)
                        .matchStatus(MatchStatus.BOT)
                        .build());
            }

            return waitingPlayer;
        } catch (Exception e) {
            log.error("Error creating opponent: {}", e.getMessage());
            throw new MatchmakingException("Failed to create opponent", e);
        }
    }

    private void updatePlayerStatuses(PlayerEntity player1, PlayerEntity player2) {
        // Update status for first player
        player1.setMatchStatus(MatchStatus.IN_MATCH);
        playerService.updatePlayer(player1);

        // Only update human players
        if (!player2.getIsBot()) {
            player2.setMatchStatus(MatchStatus.IN_MATCH);
            playerService.updatePlayer(player2);
        }

        log.debug("Updated status for players: {} and {}",
                player1.getSessionId(), player2.getSessionId());
    }

    private void notifyMatchParticipants(MatchEntity match) throws MatchmakingException {
        try {
            PlayerEntity playerOne = match.getPlayerOne();
            PlayerEntity playerTwo = match.getPlayerTwo();
            WebSocketSession playerOneSession = sessionService.getSession(playerOne.getSessionId());

            // Create match data for player one
            MatchFoundData player1Data = MatchFoundData.builder()
                    .matchId(match.getId())
                    .currentTyperId(match.getCurrentTyperId())
                    .opponent(modelMapper.map(playerTwo.getUser(), UserDto.class))
                    .build();

            // Notify player one
            notificationService.sendMatchFound(playerOneSession, player1Data);

            // Only notify player two if they're not a bot
            if (!playerTwo.getIsBot()) {
                WebSocketSession playerTwoSession = sessionService.getSession(playerTwo.getSessionId());

                // Create match data for player two
                MatchFoundData player2Data = MatchFoundData.builder()
                        .matchId(match.getId())
                        .currentTyperId(match.getCurrentTyperId())
                        .opponent(modelMapper.map(playerOne.getUser(), UserDto.class))
                        .build();

                notificationService.sendMatchFound(playerTwoSession, player2Data);
            }

            log.info("Match participants notified for match: {}", match.getId());
        } catch (Exception e) {
            log.error("Failed to notify match participants: {}", e.getMessage());
            throw new MatchmakingException("Failed to notify match participants", e);
        }
    }

    private void cleanupMatch(MatchEntity match) {
        matchService.delete(match.getId());
    }
}
