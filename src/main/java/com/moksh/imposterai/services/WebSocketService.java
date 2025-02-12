package com.moksh.imposterai.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moksh.imposterai.dtos.enums.SocketActions;
import com.moksh.imposterai.dtos.response.ErrorResponse;
import com.moksh.imposterai.dtos.response.WsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    private final MatchMakingService matchMakingService;
    private final PlayerChattingService playerChattingService;
    private final SessionService sessionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {

            WsMessage<?> wsMessage = objectMapper.readValue(message.getPayload(), new TypeReference<>() {
            });

            switch (wsMessage.getAction()) {
                case FIND_MATCH -> matchMakingService.handleFindMatch(session.getId());
                case CHAT -> playerChattingService.handleChat(session, wsMessage);
                default -> sendError(session, "Invalid Action");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, e.getMessage());
        }
    }

    public void afterConnectionEstablished(WebSocketSession session) {
        sessionService.saveSession(session.getId(), session);
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionService.removeSession(session.getId());
        log.info("Session closed: {}", session.toString());
        matchMakingService.connectionClosed(session);
    }

    private void sendError(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    new WsMessage<>(SocketActions.ERROR, new ErrorResponse(500, message))
            )));
        } catch (IOException e) {
            log.error("Failed to send error message", e);
        }
    }
}
