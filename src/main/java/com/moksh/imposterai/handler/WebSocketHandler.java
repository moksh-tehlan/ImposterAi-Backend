package com.moksh.imposterai.handler;

import com.moksh.imposterai.services.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;

    public WebSocketHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        webSocketService.handleTextMessage(session, message);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketService.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketService.afterConnectionClosed(session, status);
    }
}
