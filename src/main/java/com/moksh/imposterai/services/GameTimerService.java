package com.moksh.imposterai.services;

import com.moksh.imposterai.config.MatchmakingProperties;
import com.moksh.imposterai.entities.GameResultEntity;
import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.exceptions.MatchmakingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameTimerService {
    private final Map<String, ScheduledExecutorService> matchSchedulers = new ConcurrentHashMap<>();
    private final NotificationService notificationService;
    private final MatchmakingProperties matchmakingProperties;
    private final GameResultService gameResultService;
    private final MatchService matchService;

    public void startGameTimer(String matchId) {
        AtomicInteger timeLeft = new AtomicInteger(matchmakingProperties.getGameDuration());
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        matchSchedulers.put(matchId, scheduler);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (timeLeft.get() < 0) {
                    endGame(matchId);
                    return;
                }
                notificationService.sendTimeUpdate(matchId, timeLeft.get());
            } catch (Exception e) {
                log.error("Error in game timer for match {}: {}", matchId, e.getMessage());
                stopTimer(matchId);
            }
            timeLeft.decrementAndGet();
        }, 0, 1, TimeUnit.SECONDS);
    }


    public void stopTimer(String matchId) {
        ScheduledExecutorService scheduler = matchSchedulers.remove(matchId);
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void endGame(String matchId) throws MatchmakingException {
        MatchEntity match = matchService.getMatch(matchId);

        // Save game result
        GameResultEntity gameResult = GameResultEntity.builder()
                .matchId(matchId)
                .vsBot(match.getPlayerTwo().getIsBot())
                .build();
        gameResultService.saveGameResult(gameResult);
        notificationService.sendGameOver(matchId);
        stopTimer(matchId);
        matchService.delete(matchId);
    }

}