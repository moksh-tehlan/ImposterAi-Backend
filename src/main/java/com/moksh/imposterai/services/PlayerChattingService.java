package com.moksh.imposterai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moksh.imposterai.dtos.ChatMessage;
import com.moksh.imposterai.dtos.ChatResponse;
import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.dtos.enums.SocketActions;
import com.moksh.imposterai.dtos.response.WsMessage;
import com.moksh.imposterai.entities.ChatEntity;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerChattingService {
    private final MatchService matchService;
    private final PlayerService playerService;
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final ChatService chatService;

    public void handleChat(WebSocketSession session, WsMessage<?> wsMessage) throws Exception {
        ChatMessage chatMessage = objectMapper.convertValue(wsMessage.getData(), ChatMessage.class);
        PlayerEntity playerEntity = playerService.getPlayerBySessionId(session.getId()).orElseThrow(() -> new ResourceNotFoundException("Player not found"));
        MatchEntity match = matchService.getMatchByPlayerId(playerEntity.getSessionId()).orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        UserEntity sender = (UserEntity) session.getAttributes().get("User");
        ChatEntity savedChat = chatService.saveChat(
                match.getId(),
                sender.getId(),
                chatMessage.getMessage()
        );

        notifyOpponent(match, session.getId(), savedChat);
    }

    private void notifyOpponent(MatchEntity match, String senderSessionId, ChatEntity chat) {
        match.getOpponent(senderSessionId).ifPresentOrElse(
                opponent -> {
                    sendChatMessage(opponent, chat);
                },
                () -> log.warn("Opponent not found for match: {}", match.getId())
        );
    }

    private void sendChatMessage(PlayerEntity opponent, ChatEntity chat) {
        WebSocketSession opponentSessions = sessionService.getSession(opponent.getSessionId());
        if (opponentSessions != null && opponentSessions.isOpen()) {
            try {
                ChatResponse chatResponse = ChatResponse.builder()
                        .id(chat.getId())
                        .message(chat.getMessage())
                        .sender(modelMapper.map(chat.getSender(), UserDto.class))
                        .currentTyperId(opponent.getUser().getId())
                        .build();
                opponentSessions.sendMessage(new TextMessage(
                        objectMapper.writeValueAsString(
                                new WsMessage<>(SocketActions.CHAT, chatResponse)
                        )
                ));
            } catch (IOException e) {
                log.error("Error sending chat: {}", e.getMessage());
            }
        }
    }
}
