package com.moksh.imposterai.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moksh.imposterai.dtos.enums.SocketActions;
import com.moksh.imposterai.dtos.response.ErrorResponse;
import com.moksh.imposterai.dtos.response.WsMessage;
import com.moksh.imposterai.exceptions.ChatException;
import com.moksh.imposterai.exceptions.MatchmakingException;
import com.moksh.imposterai.exceptions.MessageParsingException;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {
    private final MatchMakingService matchMakingService;
    private final PlayerChattingService playerChattingService;
    private final SessionService sessionService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            WsMessage<?> wsMessage = parseMessage(message);
            handleMessage(session, wsMessage);
        } catch (Exception e) {
            handleError(session, e);
        }
    }

    private WsMessage<?> parseMessage(TextMessage message) throws MessageParsingException {
        try {
            return objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse WebSocket message: {}", e.getMessage());
            throw new MessageParsingException("Invalid message format", e);
        }
    }

    private void handleMessage(WebSocketSession session, WsMessage<?> wsMessage) throws Exception {
        log.debug("Handling message type: {} from session: {}", wsMessage.getAction(), session.getId());

        switch (wsMessage.getAction()) {
            case FIND_MATCH -> matchMakingService.findMatch(session.getId());
            case CHAT -> playerChattingService.handleChat(session, wsMessage);
            default -> handleInvalidAction(session);
        }
    }

    private void handleError(WebSocketSession session, Exception e) {
        String errorMessage;
        int errorCode;

        if (e instanceof MessageParsingException) {
            errorMessage = "Invalid message format";
            errorCode = 400;
            log.error("Message parsing error: {}", e.getMessage());
        } else if (e instanceof ResourceNotFoundException) {
            errorMessage = e.getMessage();
            errorCode = 404;
            log.error("Resource not found: {}", e.getMessage());
        } else if (e instanceof ChatException || e instanceof MatchmakingException) {
            errorMessage = e.getMessage();
            errorCode = 500;
            log.error("Game error: {}", e.getMessage());
        } else {
            errorMessage = "An unexpected error occurred";
            errorCode = 500;
            log.error("Unexpected error: {}", e.getMessage(), e);
        }

        sendErrorMessage(session, errorMessage, errorCode);
    }

    private void handleInvalidAction(WebSocketSession session) {
        log.warn("Invalid action received for session: {}", session.getId());
        sendErrorMessage(session, "Invalid action", 400);
    }

    private void sendErrorMessage(WebSocketSession session, String message, int code) {
        try {
            ErrorResponse errorResponse = new ErrorResponse(code, message);
            WsMessage<ErrorResponse> errorMessage = WsMessage.<ErrorResponse>builder()
                    .action(SocketActions.ERROR)
                    .data(errorResponse)
                    .build();

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
        } catch (IOException e) {
            log.error("Failed to send error message to client: {}", e.getMessage());
        }
    }

    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            sessionService.saveSession(session.getId(), session);
            log.info("WebSocket connection established for session: {}", session.getId());
        } catch (Exception e) {
            log.error("Error in connection establishment: {}", e.getMessage());
            closeSession(session);
        }
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            log.info("Closing WebSocket connection for session: {}", session.getId());
            matchMakingService.handleMatchAbandoned(session.getId());
            sessionService.removeSession(session.getId());
        } catch (Exception e) {
            log.error("Error in connection closure: {}", e.getMessage());
        }
    }

    private void closeSession(WebSocketSession session) {
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            log.error("Error closing WebSocket session: {}", e.getMessage());
        }
    }
}