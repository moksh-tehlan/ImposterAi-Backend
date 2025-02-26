package com.moksh.imposterai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moksh.imposterai.dtos.ChatResponse;
import com.moksh.imposterai.dtos.MatchFoundData;
import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.enums.SocketActions;
import com.moksh.imposterai.dtos.response.*;
import com.moksh.imposterai.entities.ChatEntity;
import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.exceptions.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final ModelMapper modelMapper;
    private final MatchService matchService;
    private final PlayerService playerService;

    /**
     * Match-related notifications
     */
    public void sendMatchFound(WebSocketSession session, MatchFoundData matchData) {
        WsMessage<MatchResponse> message = WsMessage.<MatchResponse>builder()
                .action(SocketActions.MATCH_FOUND)
                .data(new MatchResponse(
                        matchData.getMatchId(),
                        matchData.getCurrentTyperId(),
                        matchData.getOpponent()
                ))
                .build();

        sendMessage(session, message);
    }

    public void sendMatchAbandoned(String sessionId) {
        WebSocketSession session = sessionService.getSession(sessionId);
        WsMessage<MatchAbandonedResponse> message = WsMessage.<MatchAbandonedResponse>builder()
                .action(SocketActions.PLAYER_LEFT)
                .data(new MatchAbandonedResponse("Opponent has left the match"))
                .build();

        sendMessage(session, message);
    }

    public void sendTimeUpdate(String matchId, int timeLeft) {
        WsMessage<TimerResponse> message = WsMessage.<TimerResponse>builder()
                .action(SocketActions.TIMER)
                .data(new TimerResponse(timeLeft))
                .build();

        sendToMatchParticipants(matchId, message);
    }

    public void sendGameOver(String matchId) {
        WsMessage<GameOverResponse> message = WsMessage.<GameOverResponse>builder()
                .action(SocketActions.GAME_OVER)
                .data(new GameOverResponse("Game over"))
                .build();

        sendToMatchParticipants(matchId, message);
    }

    /**
     * Chat-related notifications
     */
    public void sendChatMessage(MatchEntity match, String senderSessionId, ChatEntity chat) {
        try {
            match.getOpponent(senderSessionId).ifPresent(opponent -> {
                WebSocketSession opponentSession = sessionService.getSession(opponent.getSessionId());
                if (opponentSession != null && opponentSession.isOpen()) {
                    ChatResponse chatResponse = ChatResponse.builder()
                            .id(chat.getId())
                            .message(chat.getMessage())
                            .sender(modelMapper.map(chat.getSender(), UserDto.class))
                            .currentTyperId(opponent.getUser().getId())
                            .build();

                    WsMessage<ChatResponse> message = WsMessage.<ChatResponse>builder()
                            .action(SocketActions.CHAT)
                            .data(chatResponse)
                            .build();

                    sendMessage(opponentSession, message);
                }
            });
        } catch (Exception e) {
            log.error("Failed to send chat message: {}", e.getMessage());
            throw new ChatException("Failed to send chat message", e);
        }
    }

    /**
     * Common helper methods
     */
    private void sendToMatchParticipants(String matchId, WsMessage<?> message) {
        try {
            MatchEntity match = matchService.getMatch(matchId);
            PlayerEntity playerOne = match.getPlayerOne();
            PlayerEntity playerTwo = match.getPlayerTwo();

            sendMessage(sessionService.getSession(playerOne.getSessionId()), message);

            if (!playerTwo.getIsBot()) {
                sendMessage(sessionService.getSession(playerTwo.getSessionId()), message);
            }
        } catch (Exception e) {
            log.error("Error sending message to match participants: {}", e.getMessage());
            throw new ChatException("Failed to send message to participants", e);
        }
    }

    private void sendMessage(WebSocketSession session, WsMessage<?> message) {
        try {
            if (session != null && session.isOpen()) {
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
                log.debug("Sent message: {} to session: {}", message.getAction(), session.getId());
            }
        } catch (IOException e) {
            log.error("Failed to send message: {}", e.getMessage());
            throw new ChatException("Failed to send WebSocket message", e);
        }
    }

    public void sendNotifForAiMessage(String sessionId, ChatEntity chat) {
        PlayerEntity player = playerService.getPlayerBySessionId(sessionId);
        ChatResponse chatResponse = ChatResponse.builder()
                .id(chat.getId())
                .message(chat.getMessage())
                .sender(modelMapper.map(chat.getSender(), UserDto.class))
                .currentTyperId(player.getUser().getId())
                .build();

        WsMessage<ChatResponse> message = WsMessage.<ChatResponse>builder()
                .action(SocketActions.CHAT)
                .data(chatResponse)
                .build();

        WebSocketSession session = sessionService.getSession(sessionId);
        sendMessage(session, message);
    }
}